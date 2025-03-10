/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.edc

import de.sovity.edc.runtime.modules.model.EdcModule
import de.sovity.edc.runtime.modules.runtime.SovityEdcRuntime
import org.eclipse.edc.junit.extensions.TestServiceExtensionContext
import org.eclipse.edc.spi.system.configuration.Config

/**
 * Mocked Services for [SovityEdcRuntime]
 */
class SovityEdcTestRuntime(
    private val runtimeNameForLogging: String,
    private val rootModule: EdcModule,
    private val initialConfig: Config,
) {
    private val serviceMocks = LinkedHashMap<Class<*>, Any>()
    private lateinit var monitor: MonitorWithOffSwitch
    private lateinit var runtime: SovityEdcRuntime

    fun boot() {
        monitor = TestMonitor.createTestMonitor(runtimeNameForLogging)
        runtime = SovityEdcRuntime(
            rootModule,
            initialConfig,
            monitor,
            serviceExtensionContextFactory = { monitor, config ->
                TestServiceExtensionContext(monitor, config, serviceMocks)
            }
        )
        runtime.boot()
    }

    fun shutdown() {
        monitor.turnOff()
        runtime.shutdown()
    }

    fun hasService(clazz: Class<*>): Boolean = runtime.hasService(clazz)

    fun <T> getService(clazz: Class<T>): T = runtime.getService(clazz)

    fun <T : Any> registerServiceMock(type: Class<T>, mock: T) = this.apply {
        serviceMocks[type] = mock
    }
}
