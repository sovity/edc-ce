/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sovity.policies.referring_connector

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.ce.modules.policy_utils.creator.RightExpressionParsers
import de.sovity.edc.ce.modules.policy_utils.creator.SimplePolicyCreator
import org.eclipse.edc.participant.spi.ParticipantAgentPolicyContext
import org.eclipse.edc.policy.engine.spi.PolicyContext
import org.eclipse.edc.runtime.metamodel.annotation.Inject
import org.eclipse.edc.spi.monitor.Monitor
import org.eclipse.edc.spi.result.ServiceResult
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext

/**
 * Validates the counter-party's Participant ID
 */
class ReferringConnectorPolicyExtension : ServiceExtension {
    @Inject
    private lateinit var simplePolicyCreator: SimplePolicyCreator

    override fun initialize(context: ServiceExtensionContext) {
        val config = context.config
        val claimName = CeConfigProps.EDC_AGENT_IDENTITY_KEY.getStringOrThrow(config)

        simplePolicyCreator.registerPolicyFunction(
            leftExpressionName = "REFERRING_CONNECTOR",
            leftExpressionValueFn = { policyContext ->
                requirePristinePolicyContext(policyContext, context.monitor)

                val claims =
                    if (policyContext is ParticipantAgentPolicyContext) {
                        policyContext.participantAgent().claims
                    } else {
                        error("Unexpected policy context: $policyContext")
                    }

                val claim = getStringClaim(claims, claimName)

                claim?.let { ServiceResult.success(listOf(it)) }
                    ?: ServiceResult.unauthorized("String claim $claimName not found in ParticipantAgent claims.")
            },
            rightExpressionParser = RightExpressionParsers::stringValueCommaSeparated
        )
    }

    private fun getStringClaim(claims: Map<String, Any>, claimName: String): String? {
        val claim = claims[claimName]

        if (claim == null || claim !is String || claim.isBlank()) {
            return null
        }

        return claim
    }

    private fun requirePristinePolicyContext(policyContext: PolicyContext, monitor: Monitor) {
        // TODO: This is old code. Does the current version of the EDC still need sanity checks like that?
        require(!policyContext.hasProblems()) {
            val problems = java.lang.String.join(", ", policyContext.problems)
            val message = "${javaClass.simpleName}: Rejecting PolicyContext with problems. Problems: $problems"
            monitor.debug(message)
            message
        }
    }
}
