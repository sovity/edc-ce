/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.runtime

import de.sovity.edc.runtime.modules.evaluation.EdcModuleSystemEvaluator
import de.sovity.edc.runtime.modules.model.EdcModule
import org.eclipse.edc.boot.system.DefaultServiceExtensionContext
import org.eclipse.edc.boot.system.ExtensionLoader
import org.eclipse.edc.boot.system.ExtensionLoader.bootServiceExtensions
import org.eclipse.edc.spi.monitor.Monitor
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext
import org.eclipse.edc.spi.system.configuration.Config
import org.eclipse.edc.spi.system.health.HealthCheckResult
import org.eclipse.edc.spi.system.health.HealthCheckService
import org.eclipse.edc.spi.system.health.StartupStatusProvider

/**
 * The extension points of [org.eclipse.edc.boot.system.runtime.BaseRuntime] were not useful to us, so it was easier
 * to write our own class that calls the right code.
 */
class SovityEdcRuntime(
    private val rootModule: EdcModule,
    private val initialConfig: Config,
    private val monitor: Monitor,

    private val serviceExtensionContextFactory: (Monitor, Config) -> ServiceExtensionContext = { monitor, config ->
        DefaultServiceExtensionContext(monitor, config)
    }
) {
    private var serviceExtensions: List<ServiceExtension> = emptyList()
    private var context: ServiceExtensionContext? = null

    fun boot() {
        val evaluated = EdcModuleSystemEvaluator.evaluate(monitor, rootModule, initialConfig)
        val config = evaluated.evaluatedConfig

        // Dev Utility: Print out config and dependencies
        DevLoggingUtils.printConfigAndEdcJars(monitor, evaluated)

        // boot EDC
        val serviceLocator = DynamicServiceLocator(evaluated.serviceClasses)
        context = serviceExtensionContextFactory(monitor, config)
            .also { it.initialize() }
        serviceExtensions = ExtensionLoader(serviceLocator).loadServiceExtensions(context)
            .also { bootServiceExtensions(it, context) }
            .map { it.injectionTarget }

        registerStartupHealthOk()
        monitor.info("Runtime ${context!!.runtimeId} ready")
    }

    fun shutdown() {
        serviceExtensions.reversed().forEach {
            it.shutdown()
            monitor.info("Shutdown ${it.name()}")
        }
        monitor.info("Shutdown complete")
    }

    fun hasService(clazz: Class<*>): Boolean =
        clazz == Config::class.java || context!!.hasService(clazz)

    @Suppress("UNCHECKED_CAST")
    fun <T> getService(clazz: Class<T>): T {
        if (clazz == Config::class.java) {
            return context!!.config as T
        }
        return context!!.getService(clazz)
    }

    private fun registerStartupHealthOk() {
        if (!hasService(HealthCheckService::class.java)) {
            return
        }
        getService(HealthCheckService::class.java).addStartupStatusProvider(StartupStatusProvider {
            HealthCheckResult.Builder.newInstance().component("BaseRuntime").success().build()
        })
    }
}
