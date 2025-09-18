/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.policy_utils.creator

import org.eclipse.edc.iam.verifiablecredentials.spi.model.VerifiableCredential
import org.eclipse.edc.participant.spi.ParticipantAgentPolicyContext
import org.eclipse.edc.policy.engine.spi.PolicyContext

class PolicyContextUtils {
    fun getStringClaim(policyContext: PolicyContext, claimName: String): String? {
        val claims = getClaims(policyContext)

        val claim = claims[claimName]
        if (claim !is String || claim.isBlank()) {
            return null
        }

        return claim
    }

    fun getVerifiableCredential(policyContext: PolicyContext, issuerDid: String): VerifiableCredential {
        val claims = getClaims(policyContext)

        val vcClaims = claims["vc"]
        require(vcClaims != null) { "Claim 'vc' is null." }
        require(vcClaims is List<*>) { "Claim 'vc' is not a List." }

        val vcClaimsForIssuer = vcClaims
            .filterIsInstance<VerifiableCredential>()
            .filter { it.issuer.id == issuerDid }
        require(vcClaims.size == 1) { "Missing a claim 'vc' of type ${VerifiableCredential::class.java.name} with the issuer ${issuerDid}. Found ${vcClaims.size} elements instead of 1." }

        return vcClaimsForIssuer.single() as VerifiableCredential
    }

    private fun getClaims(policyContext: PolicyContext): Map<String?, Any?> =
        if (policyContext is ParticipantAgentPolicyContext) {
            policyContext.participantAgent().claims
        } else {
            error("Unexpected policy context: $policyContext")
        }
}
