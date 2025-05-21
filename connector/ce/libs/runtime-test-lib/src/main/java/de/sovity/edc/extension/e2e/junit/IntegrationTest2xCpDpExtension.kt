/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit

import de.sovity.edc.client.EdcClient
import de.sovity.edc.extension.e2e.connector.remotes.api_wrapper.E2eTestScenario
import de.sovity.edc.extension.e2e.connector.remotes.api_wrapper.E2eTestScenarioConfig
import de.sovity.edc.extension.e2e.connector.remotes.test_backend_controller.TestBackendRemote
import de.sovity.edc.extension.e2e.junit.edc.SovityEdcTestRuntime
import de.sovity.edc.extension.e2e.junit.utils.Consumer
import de.sovity.edc.extension.e2e.junit.utils.ControlPlane
import de.sovity.edc.extension.e2e.junit.utils.InstancesForJUnitTest
import de.sovity.edc.extension.e2e.junit.utils.Provider
import de.sovity.edc.runtime.config.ConfigUtils
import de.sovity.edc.runtime.modules.model.ConfigPropRef
import de.sovity.edc.runtime.modules.model.EdcModule
import org.eclipse.edc.spi.system.configuration.Config
import org.eclipse.edc.util.io.Ports
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.mockserver.integration.ClientAndServer
import org.mockserver.stop.Stop

/**
 * Launches four EDCs that make two connectors:
 *  - Provider Control Plane
 *  - Provider Data Plane
 *  - Consumer Control Plane
 *  - Consumer Data Plane
 *
 * Prepares some test utils like [ClientAndServer],
 *
 * Resolve JUnit test parameters from the EDCs via annotations:
 *  - [de.sovity.edc.extension.e2e.junit.utils.Consumer]
 *  - [de.sovity.edc.extension.e2e.junit.utils.Provider]
 *  - [de.sovity.edc.extension.e2e.junit.utils.ControlPlane]
 *  - [de.sovity.edc.extension.e2e.junit.utils.DataPlane]
 */
class IntegrationTest2xCpDpExtension(
    private val rootModule: EdcModule,
    private val providerControlPlaneConfig: Map<ConfigPropRef, String> = mapOf(),
    private val providerDataPlaneConfig: (cpConfig: Config, cpConfigUtils: ConfigUtils) -> Map<ConfigPropRef, String> = { _, _ -> mapOf() },
    private val consumerControlPlaneConfig: Map<ConfigPropRef, String> = mapOf(),
    private val consumerDataPlaneConfig: (dpConfig: Config, dpConfigUtils: ConfigUtils) -> Map<ConfigPropRef, String> = { _, _ -> mapOf() },
    private val preBootHook: (runtime: SovityEdcTestRuntime) -> Unit = {},

    // This unfortunately needs to be here for Kotlin's delegation pattern to work
    private val instances: InstancesForJUnitTest = InstancesForJUnitTest()
) : BeforeEachCallback, BeforeAllCallback, AfterAllCallback, AfterEachCallback, ParameterResolver by instances {

    companion object {
        // Workaround for java
        @JvmStatic
        fun withDefaults(
            rootModule: EdcModule,
            providerControlPlaneConfig: Map<ConfigPropRef, String> = mapOf(),
            providerDataPlaneConfig: (cpConfig: Config, cpConfigUtils: ConfigUtils) -> Map<ConfigPropRef, String> = { _, _ -> mapOf() },
            consumerControlPlaneConfig: Map<ConfigPropRef, String> = mapOf(),
            consumerDataPlaneConfig: (dpConfig: Config, dpConfigUtils: ConfigUtils) -> Map<ConfigPropRef, String> = { _, _ -> mapOf() },
        ) =
            IntegrationTest2xCpDpExtension(
                rootModule,
                providerControlPlaneConfig,
                providerDataPlaneConfig,
                consumerControlPlaneConfig,
                consumerDataPlaneConfig
            )
    }

    override fun beforeAll(context: ExtensionContext) {
        instances.addInstance(this)

        listOf(
            Provider::class.java,
            Consumer::class.java
        ).forEach { side ->
            val controlPlaneConfig = when (side) {
                Provider::class.java -> providerControlPlaneConfig
                Consumer::class.java -> consumerControlPlaneConfig
                else -> error("Unknown side: $side")
            }
            val dataPlaneConfig = when (side) {
                Provider::class.java -> providerDataPlaneConfig
                Consumer::class.java -> consumerDataPlaneConfig
                else -> error("Unknown side: $side")
            }

            val extension = IntegrationTestCpDpExtension(
                rootModule = rootModule,
                controlPlaneConfig = controlPlaneConfig,
                dataPlaneConfig = dataPlaneConfig,
                preBootHook = preBootHook,
                runtimeNamePrefixForLogging = side.simpleName
            )

            // Register DbRuntimePerClassExtension
            instances.addAll(extension.instances, listOf(side))

            // Start EDC
            extension.beforeAll(context)
        }

        // Register TestBackendRemote
        instances.addInstanceLazy(TestBackendRemote::class.java, {
            this.buildTestBackendRemote()
        })
    }

    override fun beforeEach(context: ExtensionContext) {
        // Register ClientAndServer
        instances.removeInstance(ClientAndServer::class.java)
        instances.addInstanceLazy(ClientAndServer::class.java, {
            ClientAndServer.startClientAndServer(Ports.getFreePort())
        })

        // Register ConnectorRemoteClient
        instances.removeInstance(E2eTestScenario::class.java)
        instances.addInstanceLazy(E2eTestScenario::class.java, {
            this.buildE2eTestScenario()
        })
    }

    override fun afterEach(context: ExtensionContext) {
        instances.getAllForCleanup(ClientAndServer::class.java).forEach {
            Stop.stopQuietly(it)
        }
        for (extension in instances.getAllForCleanup(IntegrationTestCpDpExtension::class.java)) {
            extension.afterEach(context)
        }
    }

    override fun afterAll(context: ExtensionContext) {
        for (extension in instances.getAllForCleanup(IntegrationTestCpDpExtension::class.java)) {
            extension.afterAll(context)
        }
    }

    private fun buildE2eTestScenario(): E2eTestScenario {
        return E2eTestScenario.builder()
            .consumerClient(getConsumerControlPlaneService(EdcClient::class.java))
            .providerClient(getProviderControlPlaneService(EdcClient::class.java))
            .mockServer(instances.getSingle(ClientAndServer::class.java))
            .config(
                E2eTestScenarioConfig.forConfig(
                    getProviderControlPlaneService(ConfigUtils::class.java),
                    getConsumerControlPlaneService(ConfigUtils::class.java)
                )
            )
            .build()
    }

    private fun buildTestBackendRemote(): TestBackendRemote {
        val defaultApiUrl = getConsumerControlPlaneService(ConfigUtils::class.java).defaultApiUrl
        return TestBackendRemote(defaultApiUrl)
    }

    private fun <T : Any> getProviderControlPlaneService(clazz: Class<T>): T =
        instances.getSingle(clazz, Provider::class.java, ControlPlane::class.java)

    private fun <T : Any> getConsumerControlPlaneService(clazz: Class<T>): T =
        instances.getSingle(clazz, Consumer::class.java, ControlPlane::class.java)
}
