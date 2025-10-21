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
import jakarta.json.JsonValue
import lombok.SneakyThrows
import org.eclipse.edc.connector.controlplane.catalog.spi.policy.CatalogPolicyContext
import org.eclipse.edc.connector.controlplane.contract.spi.policy.ContractNegotiationPolicyContext
import org.eclipse.edc.connector.controlplane.contract.spi.policy.TransferProcessPolicyContext
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
    private val scopes = listOf(
        CatalogPolicyContext.CATALOG_SCOPE,
        ContractNegotiationPolicyContext.NEGOTIATION_SCOPE,
        TransferProcessPolicyContext.TRANSFER_SCOPE,
    )

    private val actionTypes = listOf(
        "use",
        "USE",
        "http://www.w3.org/ns/odrl/2/use"
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
            actionTypes.forEach { actionType ->
                ruleBindingRegistry.bind(actionType, scope)
            }
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

    @SneakyThrows
    private fun getJsonValue(o: Any?): JsonValue =
        JsonUtils.parseJsonValue(objectMapper.writeValueAsString(o))
}
