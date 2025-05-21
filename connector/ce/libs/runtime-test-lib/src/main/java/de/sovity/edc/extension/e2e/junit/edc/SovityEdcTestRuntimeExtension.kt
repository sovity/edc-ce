/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.edc

import de.sovity.edc.extension.e2e.junit.utils.InstanceResolver
import de.sovity.edc.extension.e2e.junit.utils.InstancesForJUnitTest
import de.sovity.edc.extension.e2e.utils.DebugUtils
import de.sovity.edc.runtime.modules.model.ConfigPropRef
import de.sovity.edc.runtime.modules.model.EdcModule
import de.sovity.edc.runtime.modules.runtime.InitialConfigFactory
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterResolver
import java.time.Duration
import java.util.concurrent.Executors
import java.util.function.Consumer

/**
 * JUnit Extension: Start one [SovityEdcTestRuntime]
 */
class SovityEdcTestRuntimeExtension(
    val rootModule: EdcModule,
    val config: Map<ConfigPropRef, String>,
    val preBootHook: Consumer<SovityEdcTestRuntime> = Consumer {},
    val runtimeNameForLogging: String = "edc",

    // This unfortunately needs to be here for Kotlin's delegation pattern to work
    val instances: InstancesForJUnitTest = InstancesForJUnitTest(),
) : BeforeAllCallback, AfterAllCallback, ParameterResolver by instances {
    private val executorService = Executors.newSingleThreadExecutor()
    private lateinit var runtime: SovityEdcTestRuntime

    override fun beforeAll(extensionContext: ExtensionContext) {
        instances.addInstance(this)
        val initialConfig = InitialConfigFactory.initialConfigFromEnv(config.mapKeys { it.key.property })
        runtime = SovityEdcTestRuntime(runtimeNameForLogging, rootModule, initialConfig)
        instances.addInstanceResolver(
            InstanceResolver(
                has = { clazz, _ -> runtime.hasService(clazz) },
                get = { clazz, _ -> listOfNotNull(runtime.getService(clazz)) },
                getAllForCleanup = { clazz ->
                    if (runtime.hasService(clazz)) {
                        listOfNotNull(runtime.getService(clazz))
                    } else {
                        emptyList()
                    }
                },
                annotations = listOf()
            )
        )

        preBootHook.accept(runtime)

        val timeout = if (DebugUtils.isDebug) Duration.ofHours(10) else Duration.ofSeconds(40)
        TimeoutUtils.runDeferred(timeout) {
            runtime.boot()
        }
    }

    override fun afterAll(extensionContext: ExtensionContext?) {
        runtime.shutdown()
        executorService.shutdown()
    }
}
