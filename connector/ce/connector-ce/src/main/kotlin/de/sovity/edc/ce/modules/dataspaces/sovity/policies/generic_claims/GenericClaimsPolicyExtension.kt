/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sovity.policies.generic_claims

import de.sovity.edc.ce.modules.policy_utils.creator.PolicyComparator
import de.sovity.edc.ce.modules.policy_utils.creator.PolicyContextUtils
import de.sovity.edc.ce.modules.policy_utils.creator.RightExpressionParsers
import de.sovity.edc.ce.modules.policy_utils.creator.SimplePolicyCreator
import org.eclipse.edc.policy.model.Operator
import org.eclipse.edc.runtime.metamodel.annotation.Inject
import org.eclipse.edc.spi.result.ServiceResult
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext

/**
 * Validates arbitrary string claims of the counter-party's identity token.
 *
 * A constraint with left operand `POLICY_CLAIM_<NAME>` compares the right operand against the
 * claim `<NAME>` of the ParticipantAgent. New claims only require a mapper in the DAPS (Keycloak),
 * no connector changes.
 *
 * Only the operators [Operator.EQ] and [Operator.IN] are supported, both matching any of the given
 * values. Any other operator denies the constraint, because comparing a claim with e.g. GT would
 * fall back to a lexicographic string comparison, which is never what the policy author means.
 */
class GenericClaimsPolicyExtension : ServiceExtension {
    @Inject
    private lateinit var simplePolicyCreator: SimplePolicyCreator

    @Inject
    private lateinit var policyContextUtils: PolicyContextUtils

    @Inject
    private lateinit var policyComparator: PolicyComparator

    override fun initialize(context: ServiceExtensionContext) {
        simplePolicyCreator.registerDynamicClaimPolicyFunction(
            resolveActualClaimValues = { claimName, policyContext ->
                val claim = policyContextUtils.getStringClaim(policyContext, claimName)

                claim?.let { ServiceResult.success(listOf(it)) }
                    ?: ServiceResult.unauthorized("String claim $claimName not found in ParticipantAgent claims.")
            },
            parseRightValues = RightExpressionParsers::stringValueCommaSeparated,
            compareFn = { claimValues, operator, expectedClaimValues ->
                if (operator in supportedOperators) {
                    policyComparator.compareWithEqWorkingLikeIn(claimValues, operator, expectedClaimValues)
                } else {
                    context.monitor.warning(
                        "Denying claim policy: Unsupported operator $operator. " +
                            "Supported operators: ${supportedOperators.joinToString(", ")}."
                    )
                    false
                }
            }
        )
    }

    companion object {
        private val supportedOperators = setOf(Operator.EQ, Operator.IN)
    }
}
