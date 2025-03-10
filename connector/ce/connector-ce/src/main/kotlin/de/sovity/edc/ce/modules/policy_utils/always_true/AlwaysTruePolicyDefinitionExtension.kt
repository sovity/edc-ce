/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.policy_utils.always_true

import org.eclipse.edc.connector.controlplane.services.spi.policydefinition.PolicyDefinitionService
import org.eclipse.edc.runtime.metamodel.annotation.Inject
import org.eclipse.edc.runtime.metamodel.annotation.Provides
import org.eclipse.edc.spi.monitor.Monitor
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext

/**
 * This extension creates a Policy Definition `always-true` on EDC startup.
 *
 * While the default behavior for contract definitions with empty policies is not "default deny",
 * our UI will be ensuring non-empty access and contract policies.
 *
 * Therefore, it is of interest to have an `always-true` policy to explicitly enable full access in contract definitions.
 */
@Provides(AlwaysTruePolicyDefinitionService::class)
class AlwaysTruePolicyDefinitionExtension : ServiceExtension {
    @Inject
    lateinit var policyDefinitionService: PolicyDefinitionService

    private lateinit var monitor: Monitor

    private lateinit var alwaysTruePolicyDefinitionService: AlwaysTruePolicyDefinitionService

    override fun name(): String = javaClass.name

    override fun initialize(context: ServiceExtensionContext) {
        monitor = context.monitor
        alwaysTruePolicyDefinitionService =
            AlwaysTruePolicyDefinitionService(policyDefinitionService)

        context.registerService(
            AlwaysTruePolicyDefinitionService::class.java,
            alwaysTruePolicyDefinitionService
        )
    }

    override fun start() {
        if (!alwaysTruePolicyDefinitionService.exists()) {
            monitor.info("Creating Always True Policy Definition.")
            alwaysTruePolicyDefinitionService.create()
        } else {
            monitor.debug("Skipping Always True Policy Definition creation, policy definition already exists.")
        }
    }
}
