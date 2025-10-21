/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sphinx

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.ce.modules.policy_utils.creator.PolicyComparator
import de.sovity.edc.ce.modules.policy_utils.creator.PolicyContextUtils
import de.sovity.edc.ce.modules.policy_utils.creator.RightExpressionParsers
import de.sovity.edc.ce.modules.policy_utils.creator.SimplePolicyCreator
import org.eclipse.edc.runtime.metamodel.annotation.Inject
import org.eclipse.edc.spi.result.ServiceResult
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext

/**
 * Validates the time of negotiation / transfer initiation
 */
class SphinxDidPolicyExtension : ServiceExtension {
    @Inject
    private lateinit var simplePolicyCreator: SimplePolicyCreator

    @Inject
    private lateinit var policyContextUtils: PolicyContextUtils

    @Inject
    private lateinit var policyComparator: PolicyComparator

    override fun initialize(context: ServiceExtensionContext) {
        val issuerDid = CeConfigProps.EDC_IAM_TRUSTED_ISSUER_SPHINX_ID.getStringOrEmpty(context.config)

        simplePolicyCreator.registerPolicyFunction(
            leftExpressionName = "sphinxDid",
            leftExpressionValueFn = { policyContext ->
                val vc = policyContextUtils.getVerifiableCredential(policyContext, issuerDid)
                val did = vc.name
                ServiceResult.success(listOf(did))
            },
            rightExpressionParser = RightExpressionParsers::stringValueCommaSeparated,
            comparator = policyComparator::compareWithEqWorkingLikeIn
        )
    }
}
