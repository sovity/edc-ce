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
import org.eclipse.edc.policy.engine.spi.AtomicConstraintFunction
import org.eclipse.edc.policy.engine.spi.PolicyContext
import org.eclipse.edc.policy.engine.spi.PolicyEngine
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry
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
        rightExpressionParser: (JsonValue) -> List<T>
    ) {
        ruleBindingRegistry.bind(leftExpressionName, PolicyEngine.ALL_SCOPES)

        policyEngine.registerFunction(
            PolicyEngine.ALL_SCOPES,
            Permission::class.java,
            leftExpressionName,
            AtomicConstraintFunction { operator, rightValue, _, policyContext ->
                // Evaluate Left
                val leftResult = leftExpressionValueFn(policyContext)
                if (leftResult.failed()) {
                    monitor.severe("Failed to extract left expression value from policy context: ${leftResult.failureDetail}")
                    return@AtomicConstraintFunction false
                }
                val left = leftResult.content

                // Parse Right
                val rightJson = getJsonValue(rightValue)
                val right = rightExpressionParser(rightJson)

                // Compare
                policyComparator.compare(left, operator, right)
            }
        )
    }

    @SneakyThrows
    private fun getJsonValue(o: Any?): JsonValue =
        JsonUtils.parseJsonValue(objectMapper.writeValueAsString(o))
}
