/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit

import de.sovity.edc.client.EdcClient
import de.sovity.edc.extension.e2e.connector.remotes.management_api.ManagementApiConnectorRemote
import de.sovity.edc.extension.e2e.junit.cleanup.Janitor
import de.sovity.edc.extension.e2e.junit.edc.EdcPortFinder
import de.sovity.edc.extension.e2e.junit.edc.SovityEdcTestRuntime
import de.sovity.edc.extension.e2e.junit.edc.SovityEdcTestRuntimeExtension
import de.sovity.edc.extension.e2e.junit.utils.InstancesForJunitTest
import de.sovity.edc.extension.e2e.junit.utils.IntegrationTestUtils
import de.sovity.edc.runtime.config.ConfigUtils
import de.sovity.edc.runtime.modules.model.ConfigPropRef
import de.sovity.edc.runtime.modules.model.EdcModule
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterResolver
import java.util.function.Consumer

/**
 * Launches one EDC
 *
 * Prepares some test utils like [EdcClient] and [ManagementApiConnectorRemote]
 */
class IntegrationTestExtension @JvmOverloads constructor(
    private val rootModule: EdcModule,
    private val config: Map<ConfigPropRef, String>,
    // Consumer<> to keep clean java code (avoids returning Unit.INSTANCE)
    private val installEdcMocks: Consumer<SovityEdcTestRuntime> = Consumer {},
    private val runtimeNameForLogging: String = "edc",

    // This unfortunately needs to be here for Kotlin's delegation pattern to work
    private val instances: InstancesForJunitTest = InstancesForJunitTest(),
) : BeforeAllCallback, AfterAllCallback, AfterEachCallback, ParameterResolver by instances {

    @Throws(Exception::class)
    override fun beforeAll(extensionContext: ExtensionContext) {
        // Start EDC
        EdcPortFinder.withAutoPortHandling(numPorts = 7, maxAttempts = 5, config) { configWithPort ->
            SovityEdcTestRuntimeExtension(
                rootModule,
                configWithPort,
                installEdcMocks,
                runtimeNameForLogging
            ).also {
                it.beforeAll(extensionContext)
                instances.put(it)
                instances.addParameterResolver(it)
            }
        }

        // Configure Clients and Utilities
        val configUtils = instances.get(ConfigUtils::class.java)
        instances.putLazy(EdcClient::class.java) {
            IntegrationTestUtils.getEdcClient(configUtils)
        }
        instances.putLazy(ManagementApiConnectorRemote::class.java) {
            IntegrationTestUtils.getManagementApiConnectorRemote(configUtils)
        }
        instances.put(Janitor())
    }

    @Throws(Exception::class)
    override fun afterAll(extensionContext: ExtensionContext?) {
        instances.get(SovityEdcTestRuntimeExtension::class.java)
            .afterAll(extensionContext)
    }

    @Throws(Exception::class)
    override fun afterEach(extensionContext: ExtensionContext?) {
        instances.get(Janitor::class.java).clear()
    }
}
