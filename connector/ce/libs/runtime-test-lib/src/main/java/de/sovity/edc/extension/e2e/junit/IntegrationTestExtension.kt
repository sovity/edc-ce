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
import de.sovity.edc.extension.e2e.junit.utils.InstancesForJUnitTest
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
 *
 * @param preBootHook A hook to edit the EDC runtime before it gets started.
 * This is where you can install mocks using
 * [de.sovity.edc.extension.e2e.junit.edc.SovityEdcTestRuntime.registerServiceMock].
 */
class IntegrationTestExtension @JvmOverloads constructor(
    private val rootModule: EdcModule,
    private val config: Map<ConfigPropRef, String>,
    // Consumer<> to keep clean java code (avoids returning Unit.INSTANCE)
    private val preBootHook: Consumer<SovityEdcTestRuntime> = Consumer {},
    private val runtimeNameForLogging: String = "edc",

    // This unfortunately needs to be here for Kotlin's delegation pattern to work
    val instances: InstancesForJUnitTest = InstancesForJUnitTest(),
) : BeforeAllCallback, AfterAllCallback, AfterEachCallback, ParameterResolver by instances {

    @Throws(Exception::class)
    override fun beforeAll(extensionContext: ExtensionContext) {
        instances.addInstance(this)

        // Start EDC
        EdcPortFinder.withAutoPortHandling(numPorts = 7, maxAttempts = 10, config) { configWithPort ->
            SovityEdcTestRuntimeExtension(
                rootModule,
                configWithPort,
                preBootHook,
                runtimeNameForLogging
            ).also {
                it.beforeAll(extensionContext)
                instances.addAll(it.instances)
            }
        }

        // Configure Clients and Utilities
        val configUtils = instances.getSingle(ConfigUtils::class.java)
        instances.addInstanceLazy(EdcClient::class.java, {
            IntegrationTestUtils.getEdcClient(configUtils)
        })
        instances.addInstanceLazy(ManagementApiConnectorRemote::class.java, {
            IntegrationTestUtils.getManagementApiConnectorRemote(configUtils)
        })
        instances.addInstance(Janitor())
    }

    @Throws(Exception::class)
    override fun afterAll(extensionContext: ExtensionContext?) {
        instances.getSingle(SovityEdcTestRuntimeExtension::class.java)
            .afterAll(extensionContext)
    }

    @Throws(Exception::class)
    override fun afterEach(extensionContext: ExtensionContext?) {
        instances.getSingle(Janitor::class.java).clear()
    }
}
