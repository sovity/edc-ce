/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit

import de.sovity.edc.extension.e2e.junit.edc.EdcPortFinder
import de.sovity.edc.extension.e2e.junit.edc.SovityEdcTestRuntime
import de.sovity.edc.extension.e2e.junit.utils.ControlPlane
import de.sovity.edc.extension.e2e.junit.utils.DataPlane
import de.sovity.edc.extension.e2e.junit.utils.InstancesForJUnitTest
import de.sovity.edc.runtime.config.ConfigUtils
import de.sovity.edc.runtime.modules.model.ConfigPropRef
import de.sovity.edc.runtime.modules.model.EdcModule
import org.eclipse.edc.spi.monitor.Monitor
import org.eclipse.edc.spi.security.Vault
import org.eclipse.edc.spi.system.configuration.Config
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterResolver

/**
 * Launches two EDCs that make one connector:
 *  - Control Plane
 *  - Data Plane
 *
 * Resolve JUnit test parameters from the EDCs via annotations:
 *  - [de.sovity.edc.extension.e2e.junit.utils.ControlPlane]
 *  - [de.sovity.edc.extension.e2e.junit.utils.DataPlane]
 */
class IntegrationTestCpDpExtension(
    private val rootModule: EdcModule,
    private val controlPlaneConfig: Map<ConfigPropRef, String> = mapOf(),
    private val dataPlaneConfig: (cpConfig: Config, cpConfigUtils: ConfigUtils) -> Map<ConfigPropRef, String> = { _, _ -> mapOf() },
    private val preBootHook: (runtime: SovityEdcTestRuntime) -> Unit = {},

    // This unfortunately needs to be here for Kotlin's delegation pattern to work
    val instances: InstancesForJUnitTest = InstancesForJUnitTest(),
    private val runtimeNamePrefixForLogging: String = "edc",
) : BeforeAllCallback, AfterAllCallback, AfterEachCallback, ParameterResolver by instances {

    override fun beforeAll(context: ExtensionContext) {
        instances.addInstance(this)

        // Launch CP
        launchEdc(ControlPlane::class.java, controlPlaneConfig, context, preBootHook)

        // Build DP Config from CP
        // We need this to know the CPs ports / API Keys, etc.
        val effectiveCpConfig = instances.getSingle(Config::class.java, ControlPlane::class.java)
        val effectiveCpConfigUtils = instances.getSingle(ConfigUtils::class.java, ControlPlane::class.java)
        val initialDpConfig = dataPlaneConfig(effectiveCpConfig, effectiveCpConfigUtils)

        // Launch DP
        launchEdc(DataPlane::class.java, initialDpConfig, context) {
            // Re-use CP Vault for DP
            val monitor = instances.getSingle(Monitor::class.java, ControlPlane::class.java)
            val vault = instances.getSingle(Vault::class.java, ControlPlane::class.java)
            it.registerServiceMock(Vault::class.java, vault)
            monitor.warning("Overwriting the DP vault with the CP vault so they can both use the same in-memory vault.")

            preBootHook(it)
        }
    }

    override fun afterEach(context: ExtensionContext) {
        for (extension in instances.getAllForCleanup(IntegrationTestExtension::class.java)) {
            extension.afterEach(context)
        }
    }

    override fun afterAll(context: ExtensionContext) {
        for (extension in instances.getAllForCleanup(IntegrationTestExtension::class.java)) {
            extension.afterAll(context)
        }
    }

    private fun launchEdc(
        plane: Class<out Annotation>,
        config: Map<ConfigPropRef, String>,
        context: ExtensionContext,
        preBootHook: (runtime: SovityEdcTestRuntime) -> Unit,
    ) {
        EdcPortFinder.withAutoPortHandling(numPorts = 6, maxAttempts = 5, config) { configWithPort ->
            val runtimeName = "$runtimeNamePrefixForLogging-${plane.simpleName}"
            val extension = IntegrationTestExtension(rootModule, configWithPort, preBootHook, runtimeName)
            extension.beforeAll(context)
            instances.addAll(extension.instances, listOf(plane))
        }
    }
}
