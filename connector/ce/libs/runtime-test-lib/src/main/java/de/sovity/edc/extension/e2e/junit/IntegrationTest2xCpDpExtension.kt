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
import de.sovity.edc.extension.e2e.junit.utils.ControlPlane
import de.sovity.edc.extension.e2e.junit.utils.E2eTestSide
import de.sovity.edc.extension.e2e.junit.utils.InstancesForEachConnector
import de.sovity.edc.extension.e2e.junit.utils.InstancesForJunitTest
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
import kotlin.reflect.KClass

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
    private val installEdcMocks: (runtime: SovityEdcTestRuntime) -> Unit = {},

    // This unfortunately needs to be here for Kotlin's delegation pattern to work
    private val instances: InstancesForJunitTest = InstancesForJunitTest()
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

    private val instancesForEachConnector = InstancesForEachConnector<E2eTestSide>(
        E2eTestSide.entries,
        getSideOrNull = { parameter, _ -> E2eTestSide.fromParameterContextOrNull(parameter) }
    )

    override fun beforeAll(context: ExtensionContext) {
        instances.put(instancesForEachConnector)
        instances.addParameterResolver(instancesForEachConnector)

        E2eTestSide.entries.forEach { side ->
            val controlPlaneConfig = when (side) {
                E2eTestSide.CONSUMER -> consumerControlPlaneConfig
                E2eTestSide.PROVIDER -> providerControlPlaneConfig
            }
            val dataPlaneConfig = when (side) {
                E2eTestSide.CONSUMER -> consumerDataPlaneConfig
                E2eTestSide.PROVIDER -> providerDataPlaneConfig
            }

            val extension = IntegrationTestCpDpExtension(
                rootModule = rootModule,
                controlPlaneConfig = controlPlaneConfig,
                dataPlaneConfig = dataPlaneConfig,
                installEdcMocks = installEdcMocks,
                runtimeNamePrefixForLogging = side.name.lowercase()
            )

            // Register DbRuntimePerClassExtension
            instancesForEachConnector.forSide(side).put(extension)
            instancesForEachConnector.forSide(side).addParameterResolver(extension)

            // Start EDC
            extension.beforeAll(context)
        }

        // Register TestBackendRemote
        instances.putLazy(TestBackendRemote::class.java) { this.buildTestBackendRemote() }
    }

    override fun beforeEach(context: ExtensionContext) {
        // Register ClientAndServer
        instances.putLazy(ClientAndServer::class.java) {
            ClientAndServer.startClientAndServer(Ports.getFreePort())
        }

        // Register ConnectorRemoteClient
        instances.putLazy(E2eTestScenario::class.java) { this.buildE2eTestScenario() }
    }

    override fun afterEach(context: ExtensionContext) {
        if (instances.isLazyInitialized(ClientAndServer::class.java)) {
            Stop.stopQuietly(instances.get(ClientAndServer::class.java))
        }
        for (extension in instancesForEachConnector.all(IntegrationTestCpDpExtension::class.java)) {
            extension.afterEach(context)
        }
    }

    override fun afterAll(context: ExtensionContext) {
        for (extension in instancesForEachConnector.all(IntegrationTestCpDpExtension::class.java)) {
            extension.afterAll(context)
        }
    }

    private fun buildE2eTestScenario(): E2eTestScenario {
        return E2eTestScenario.builder()
            .consumerClient(getControlPlaneService(E2eTestSide.CONSUMER, EdcClient::class))
            .providerClient(getControlPlaneService(E2eTestSide.PROVIDER, EdcClient::class))
            .mockServer(instances.get(ClientAndServer::class.java))
            .config(
                E2eTestScenarioConfig.forProviderConfig(
                    getControlPlaneService(E2eTestSide.PROVIDER, ConfigUtils::class)
                )
            )
            .build()
    }

    private fun buildTestBackendRemote(): TestBackendRemote {
        val defaultApiUrl = getControlPlaneService(E2eTestSide.CONSUMER, ConfigUtils::class).defaultApiUrl
        return TestBackendRemote(defaultApiUrl)
    }

    private fun <T : Any> getControlPlaneService(side: E2eTestSide, clazz: KClass<T>): T =
        instancesForEachConnector.forSide(side).get(clazz.java, ControlPlane::class.java)
}
