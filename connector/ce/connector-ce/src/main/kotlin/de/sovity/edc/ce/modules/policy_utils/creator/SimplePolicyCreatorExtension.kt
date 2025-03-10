/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.policy_utils.creator

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

    override fun name(): String = javaClass.name

    override fun initialize(context: ServiceExtensionContext) {
        ruleBindingRegistry.bind("USE", PolicyEngine.ALL_SCOPES)

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

        context.registerService(PolicyComparator::class.java, policyComparator)
        context.registerService(SimplePolicyCreator::class.java, simplePolicyCreator)
    }
}
