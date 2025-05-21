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
import de.sovity.edc.extension.e2e.junit.utils.InstancesForJUnitTest
import de.sovity.edc.extension.e2e.junit.utils.Provider
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
    private val preBootHook: (runtime: SovityEdcTestRuntime, annotations: List<Class<out Annotation>>) -> Unit = { _, _ -> },

    // This unfortunately needs to be here for Kotlin's delegation pattern to work
    private val instances: InstancesForJUnitTest = InstancesForJUnitTest()
) : BeforeEachCallback, BeforeAllCallback, AfterAllCallback, AfterEachCallback, ParameterResolver by instances {

    override fun beforeAll(context: ExtensionContext) {
        instances.addInstance(this)

        listOf(
            Provider::class.java,
            Consumer::class.java
        ).forEach { side ->
            val configMerged = when (side) {
                Provider::class.java -> providerConfig
                Consumer::class.java -> consumerConfig
                else -> error("Unknown side: $side")
            }

            val extension = IntegrationTestExtension(
                rootModule = rootModule,
                config = configMerged,
                preBootHook = { preBootHook(it, listOf(side)) },
                runtimeNameForLogging = side.simpleName,
            )

            // Register instances
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
        for (extension in instances.getAllForCleanup(IntegrationTestExtension::class.java)) {
            extension.afterEach(context)
        }
    }

    override fun afterAll(context: ExtensionContext) {
        for (extension in instances.getAllForCleanup(IntegrationTestExtension::class.java)) {
            extension.afterAll(context)
        }
    }

    private fun buildE2eTestScenario(): E2eTestScenario {
        return E2eTestScenario.builder()
            .consumerClient(getConsumerService(EdcClient::class.java))
            .providerClient(getProviderService(EdcClient::class.java))
            .mockServer(instances.getSingle(ClientAndServer::class.java))
            .config(
                E2eTestScenarioConfig.forConfig(
                    getProviderService(ConfigUtils::class.java),
                    getConsumerService(ConfigUtils::class.java)
                )
            )
            .build()
    }

    private fun buildTestBackendRemote(): TestBackendRemote {
        val defaultApiUrl = getConsumerService(ConfigUtils::class.java).defaultApiUrl
        return TestBackendRemote(defaultApiUrl)
    }

    private fun <T : Any> getProviderService(clazz: Class<T>): T =
        instances.getSingle(clazz, Provider::class.java)

    private fun <T : Any> getConsumerService(clazz: Class<T>): T =
        instances.getSingle(clazz, Consumer::class.java)
}
