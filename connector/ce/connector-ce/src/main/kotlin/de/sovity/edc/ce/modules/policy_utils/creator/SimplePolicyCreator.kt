/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.policy_utils.creator

import com.fasterxml.jackson.databind.ObjectMapper
import de.sovity.edc.utils.JsonUtils
import de.sovity.edc.utils.jsonld.vocab.Prop
import jakarta.json.JsonValue
import lombok.SneakyThrows
import org.eclipse.edc.connector.controlplane.catalog.spi.policy.CatalogPolicyContext
import org.eclipse.edc.connector.controlplane.contract.spi.policy.ContractNegotiationPolicyContext
import org.eclipse.edc.connector.controlplane.contract.spi.policy.TransferProcessPolicyContext
import org.eclipse.edc.participant.spi.ParticipantAgentPolicyContext
import org.eclipse.edc.policy.engine.spi.DynamicAtomicConstraintRuleFunction
import org.eclipse.edc.policy.engine.spi.PolicyContext
import org.eclipse.edc.policy.engine.spi.PolicyEngine
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry
import org.eclipse.edc.policy.model.Operator
import org.eclipse.edc.policy.model.Permission
import org.eclipse.edc.spi.monitor.Monitor
import org.eclipse.edc.spi.result.ServiceResult

class SimplePolicyCreator(
    private val policyComparator: PolicyComparator,
    private val ruleBindingRegistry: RuleBindingRegistry,
    private val policyEngine: PolicyEngine,
    private val objectMapper: ObjectMapper,
    private val monitor: Monitor,
) {
    private val scopes = setOf(
        CatalogPolicyContext.CATALOG_SCOPE,
        ContractNegotiationPolicyContext.NEGOTIATION_SCOPE
    )

    /**
     * Register a policy that implements most operators by relying on java comparator comparison
     */
    fun <T : Comparable<T>> registerPolicyFunction(
        /**
         * Left expression name, e.g. EVALUATION_TIME
         */
        leftExpressionName: String,

        /**
         * Evaluate left value supplier
         */
        leftExpressionValueFn: (PolicyContext) -> ServiceResult<List<T>>,

        /**
         * Extract right expression from JSON-LD
         * This returns a list because some operations (e.g. IN or EQ with comma separation) allow multiple values
         * Normal operations expect this to be a single element list
         */
        rightExpressionParser: (JsonValue) -> List<T>,

        /**
         * Comparator
         */
        comparator: (left: List<T>, operator: Operator, right: List<T>) -> Boolean = policyComparator::compare
    ) {
        scopes.forEach { scope ->
            ruleBindingRegistry.bind(Prop.Odrl.USE, scope)
            ruleBindingRegistry.bind(leftExpressionName, scope)
        }


        fun evaluate(operator: Operator, rightValue: Any?, policyContext: PolicyContext): Boolean {
            // Evaluate Left
            val leftResult = leftExpressionValueFn(policyContext)
            if (leftResult.failed()) {
                monitor.severe("Failed to extract left expression value from policy context: ${leftResult.failureDetail}")
                return false
            }
            val left = leftResult.content

            // Parse Right
            val rightJson = getJsonValue(rightValue)
            val right = rightExpressionParser(rightJson)

            // Compare
            return comparator(left, operator, right)
        }


        policyEngine.registerFunction(
            CatalogPolicyContext::class.java,
            Permission::class.java,
            leftExpressionName
        ) { operator, rightValue, _, policyContext ->
            evaluate(operator, rightValue, policyContext)
        }


        policyEngine.registerFunction(
            ContractNegotiationPolicyContext::class.java,
            Permission::class.java,
            leftExpressionName
        ) { operator, rightValue, _, policyContext ->
            evaluate(operator, rightValue, policyContext)
        }


        policyEngine.registerFunction(
            TransferProcessPolicyContext::class.java,
            Permission::class.java,
            leftExpressionName
        ) { operator, rightValue, _, policyContext ->
            evaluate(operator, rightValue, policyContext)
        }
    }

    /**
     * Register a single policy function for all left operands starting with the prefix
     * [CLAIM_LEFT_EXPRESSION_PREFIX], i.e. "POLICY_CLAIM_COUNTRY", "POLICY_CLAIM_USAGE_PURPOSE", ...
     *
     *
     * The remainder of the left operand is the claim name that is resolved against the policy context.
     * A missing claim is a regular deny and is therefore only logged on debug level.
     */
    fun <T : Comparable<T>> registerDynamicClaimPolicyFunction(
        /**
         * Evaluate present claim values for given claim name in the policy context
         */
        resolveActualClaimValues: (claimName: String, policyContext: PolicyContext) -> ServiceResult<List<T>>,

        /**
         * Extract right expression from JSON-LD
         * This returns a list because some operations (e.g. IN or EQ with comma separation) allow multiple values
         * Normal operations expect this to be a single element list
         */
        parseRightValues: (JsonValue) -> List<T>,

        /**
         * Compare function for policy constraints
         */
        compareFn: (left: List<T>, operator: Operator, right: List<T>) -> Boolean = policyComparator::compare
    ) {
        scopes.forEach { scope -> ruleBindingRegistry.bind(Prop.Odrl.USE, scope) }
        ruleBindingRegistry.dynamicBind { constraintLeftOperand ->
            if (constraintLeftOperand.startsWith(CLAIM_LEFT_EXPRESSION_PREFIX)) scopes else emptySet()
        }

        fun <C : ParticipantAgentPolicyContext> dynamicFunction(): DynamicAtomicConstraintRuleFunction<Permission, C> =
            object : DynamicAtomicConstraintRuleFunction<Permission, C> {
                override fun evaluate(
                    leftValue: Any,
                    operator: Operator,
                    rightValue: Any,
                    rule: Permission,
                    policyContext: C
                ): Boolean {
                    // Evaluate left value
                    val leftOperand = leftValue.toString()
                    // Remove prefix to get the claim name (e.g. POLICY_CLAIM_COUNTRY -> COUNTRY)
                    val claimName = leftOperand.removePrefix(CLAIM_LEFT_EXPRESSION_PREFIX)

                    // Resolve the value that is saved inside Keycloak for the given claimName
                    val claimValuesResult = resolveActualClaimValues(claimName, policyContext)

                    if (claimValuesResult.failed()) {
                        val counterParty = policyContext.participantAgent().identity
                        monitor.debug("Denying policy $leftOperand for counter-party $counterParty: ${claimValuesResult.failureDetail}")
                        return false
                    }
                    val claimValues = claimValuesResult.content

                    // Parse the policies' right operand
                    val rightOperandJson = getJsonValue(rightValue)
                    val expectedClaimValues = parseRightValues(rightOperandJson)

                    // Compare
                    return compareFn(claimValues, operator, expectedClaimValues)
                }

                override fun canHandle(leftValue: Any?) =
                    leftValue.toString().startsWith(CLAIM_LEFT_EXPRESSION_PREFIX)
            }

        policyEngine.registerFunction(
            CatalogPolicyContext::class.java,
            Permission::class.java,
            dynamicFunction()
        )

        policyEngine.registerFunction(
            ContractNegotiationPolicyContext::class.java,
            Permission::class.java,
            dynamicFunction()
        )
    }

    @SneakyThrows
    private fun getJsonValue(o: Any?): JsonValue =
        JsonUtils.parseJsonValue(objectMapper.writeValueAsString(o))

    companion object {
        /**
         * Left expression prefix of the generic claims policy, e.g. POLICY_CLAIM_COUNTRY
         */
        const val CLAIM_LEFT_EXPRESSION_PREFIX = "POLICY_CLAIM_"
    }
}
