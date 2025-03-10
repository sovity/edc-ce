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
import de.sovity.edc.extension.e2e.junit.utils.ConnectorPlane
import de.sovity.edc.extension.e2e.junit.utils.InstancesForEachConnector
import de.sovity.edc.extension.e2e.junit.utils.InstancesForJunitTest
import de.sovity.edc.runtime.config.ConfigUtils
import de.sovity.edc.runtime.modules.model.ConfigPropRef
import de.sovity.edc.runtime.modules.model.EdcModule
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
    private val installEdcMocks: (runtime: SovityEdcTestRuntime) -> Unit = {},

    // This unfortunately needs to be here for Kotlin's delegation pattern to work
    private val instances: InstancesForJunitTest = InstancesForJunitTest(),
    private val runtimeNamePrefixForLogging: String = "edc",
) : BeforeAllCallback, AfterAllCallback, AfterEachCallback, ParameterResolver by instances {

    private val instancesForEachConnector = InstancesForEachConnector<ConnectorPlane>(
        ConnectorPlane.entries,
        getSideOrNull = { parameter, _ -> ConnectorPlane.fromParameterContextOrNull(parameter) }
    )

    override fun beforeAll(context: ExtensionContext) {
        instances.put(instancesForEachConnector)
        instances.addParameterResolver(instancesForEachConnector)

        // Launch CP
        launchEdc(ConnectorPlane.CONTROL_PLANE, controlPlaneConfig, context)

        // Build DP Config from CP
        // We need this to know the CPs ports / API Keys, etc.
        val effectiveCpConfig = instancesForEachConnector
            .forSide(ConnectorPlane.CONTROL_PLANE)
            .get(Config::class.java)
        val effectiveCpConfigUtils = instancesForEachConnector
            .forSide(ConnectorPlane.CONTROL_PLANE)
            .get(ConfigUtils::class.java)
        val initialDpConfig = dataPlaneConfig(effectiveCpConfig, effectiveCpConfigUtils)

        // Launch DP
        launchEdc(ConnectorPlane.DATA_PLANE, initialDpConfig, context)
    }

    override fun afterEach(context: ExtensionContext) {
        for (extension in instancesForEachConnector.all(IntegrationTestExtension::class.java)) {
            extension.afterEach(context)
        }
    }

    override fun afterAll(context: ExtensionContext) {
        for (extension in instancesForEachConnector.all(IntegrationTestExtension::class.java)) {
            extension.afterAll(context)
        }
    }

    private fun launchEdc(
        plane: ConnectorPlane,
        config: Map<ConfigPropRef, String>,
        context: ExtensionContext
    ) {
        EdcPortFinder.withAutoPortHandling(numPorts = 6, maxAttempts = 5, config) { configWithPort ->
            val runtimeName =
                "$runtimeNamePrefixForLogging-${plane.name.lowercase().split("_").map { it[0] }.joinToString("")}"
            val extension = IntegrationTestExtension(rootModule, configWithPort, installEdcMocks, runtimeName)
            extension.beforeAll(context)
            instancesForEachConnector.forSide(plane).put(extension)
            instancesForEachConnector.forSide(plane).addParameterResolver(extension)
        }
    }
}
