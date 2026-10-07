/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.policy_utils.creator

import de.sovity.edc.utils.jsonld.vocab.Prop
import org.eclipse.edc.connector.controlplane.catalog.spi.policy.CatalogPolicyContext
import org.eclipse.edc.connector.controlplane.contract.spi.policy.ContractNegotiationPolicyContext
import org.eclipse.edc.connector.controlplane.contract.spi.policy.TransferProcessPolicyContext
import org.eclipse.edc.policy.engine.spi.PolicyEngine
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry
import org.eclipse.edc.runtime.metamodel.annotation.Inject
import org.eclipse.edc.runtime.metamodel.annotation.Provides
import org.eclipse.edc.spi.constants.CoreConstants
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext
import org.eclipse.edc.spi.types.TypeManager

@Provides(SimplePolicyCreator::class, PolicyComparator::class)
class SimplePolicyCreatorExtension : ServiceExtension {
    @Inject
    private lateinit var ruleBindingRegistry: RuleBindingRegistry

    @Inject
    private lateinit var policyEngine: PolicyEngine

    @Inject
    private lateinit var typeManager: TypeManager

    override fun initialize(context: ServiceExtensionContext) {
        // Same scopes as Tractus-X: https://github.com/eclipse-tractusx/tractusx-edc/blob/0.9.0/edc-extensions/cx-policy/src/main/java/org/eclipse/tractusx/edc/policy/cx/CxPolicyExtension.java#L102-L104
        // No request.* scopes: they drop the MembershipCredential from the DCP token, the provider then fails with
        // "Required credential type 'MembershipCredential' not present in ClaimToken".
        setOf(
            CatalogPolicyContext.CATALOG_SCOPE,
            ContractNegotiationPolicyContext.NEGOTIATION_SCOPE,
            TransferProcessPolicyContext.TRANSFER_SCOPE
        ).forEach { ruleBindingRegistry.bind(Prop.Odrl.USE, it) }

        val monitor = context.monitor
        val objectMapper = typeManager.getMapper(CoreConstants.JSON_LD)
        val policyComparator = PolicyComparator(monitor)
        val simplePolicyCreator = SimplePolicyCreator(
            policyComparator,
            ruleBindingRegistry,
            policyEngine,
            objectMapper,
            monitor
        )
        val policyContextUtils = PolicyContextUtils()

        context.registerService(PolicyComparator::class.java, policyComparator)
        context.registerService(PolicyContextUtils::class.java, policyContextUtils)
        context.registerService(SimplePolicyCreator::class.java, simplePolicyCreator)
    }
}
