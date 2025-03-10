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
import de.sovity.edc.extension.e2e.junit.utils.E2eTestSide
import de.sovity.edc.extension.e2e.junit.utils.InstancesForEachConnector
import de.sovity.edc.extension.e2e.junit.utils.InstancesForJunitTest
import de.sovity.edc.runtime.config.ConfigUtils
import de.sovity.edc.runtime.modules.model.ConfigPropRef
import de.sovity.edc.runtime.modules.model.EdcModule
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
 * Launches two EDCs, two full connectors:
 *  - Provider EDC
 *  - Consumer EDC
 *
 * Prepares some test utils like [ClientAndServer],
 *
 * Resolve JUnit test parameters from the EDCs via annotations:
 *  - [de.sovity.edc.extension.e2e.junit.utils.Consumer]
 *  - [de.sovity.edc.extension.e2e.junit.utils.Provider]
 */
class IntegrationTest2xExtension @JvmOverloads constructor(
    private val rootModule: EdcModule,
    private val providerConfig: Map<ConfigPropRef, String> = mapOf(),
    private val consumerConfig: Map<ConfigPropRef, String> = mapOf(),
    private val installEdcMocks: (runtime: SovityEdcTestRuntime) -> Unit = {},

    // This unfortunately needs to be here for Kotlin's delegation pattern to work
    private val instances: InstancesForJunitTest = InstancesForJunitTest()
) : BeforeEachCallback, BeforeAllCallback, AfterAllCallback, AfterEachCallback, ParameterResolver by instances {

    private val instancesForEachConnector = InstancesForEachConnector<E2eTestSide>(
        E2eTestSide.entries,
        getSideOrNull = { parameter, _ -> E2eTestSide.fromParameterContextOrNull(parameter) }
    )

    override fun beforeAll(context: ExtensionContext) {
        instances.put(instancesForEachConnector)
        instances.addParameterResolver(instancesForEachConnector)

        E2eTestSide.entries.forEach { side ->
            val configMerged = when (side) {
                E2eTestSide.CONSUMER -> consumerConfig
                E2eTestSide.PROVIDER -> providerConfig
            }

            val extension = IntegrationTestExtension(
                rootModule = rootModule,
                config = configMerged,
                installEdcMocks = installEdcMocks,
                runtimeNameForLogging = side.name.lowercase(),
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
        for (extension in instancesForEachConnector.all(IntegrationTestExtension::class.java)) {
            extension.afterEach(context)
        }
    }

    override fun afterAll(context: ExtensionContext) {
        for (extension in instancesForEachConnector.all(IntegrationTestExtension::class.java)) {
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
        instancesForEachConnector.forSide(side).get(clazz.java)
}
