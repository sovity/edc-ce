/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.runtime

import de.sovity.edc.runtime.modules.RuntimeConfigProps
import de.sovity.edc.runtime.modules.evaluation.EdcModuleSystemResult
import de.sovity.edc.runtime.modules.model.EdcModule
import org.eclipse.edc.spi.monitor.Monitor
import org.eclipse.edc.spi.system.configuration.Config

object DevLoggingUtils {
    fun printConfigAndEdcJars(monitor: Monitor, evaluated: EdcModuleSystemResult, rootModule: EdcModule) {
        val config = evaluated.evaluatedConfig
        if (!RuntimeConfigProps.SOVITY_PRINT_CONFIG.getBooleanOrFalse(config)) {
            return
        }

        // Log dependency bundle JARs
        evaluated.activeModules.flatMap { it.getServices().getJars() }
            .distinct()
            .sorted()
            .forEach { jar ->
                monitor.info("Dependency: $jar")
            }

        // Log modules hierarchy
        logModulesRecursively(monitor, config, evaluated.activeModules.first { it.name == rootModule.name })

        // Log extensions
        listOf(
            org.eclipse.edc.spi.system.BootExtension::class.java,
            org.eclipse.edc.spi.system.ExecutorInstrumentation::class.java,
            org.eclipse.edc.spi.system.SystemExtension::class.java,
            org.eclipse.edc.spi.system.ServiceExtension::class.java,
        ).forEach { serviceClass ->
            evaluated.serviceClasses.getServiceClasses(serviceClass)
                .sortedBy { it.name }
                .forEach {
                    monitor.info("${serviceClass.simpleName}: ${it.name}")
                }
        }

        // Log config
        config.entries.toList().sortedBy { it.first }.forEach { (key, value) ->
            monitor.info("Config: $key=$value")
        }
    }

    private fun logModulesRecursively(
        monitor: Monitor,
        config: Config,
        current: EdcModule,
        indent: Int = 0,
        enabled: Boolean = true,
    ) {
        val status = if (enabled) "✔" else "❌"
        monitor.debug("\t${" | ".repeat(indent)} $status ${current.name}")
        current.getChildModules().forEach {
            val module = it.module
            val childIsEnabled = it.condition.fn(config) && enabled
            logModulesRecursively(monitor, config, module, indent + 1, childIsEnabled)
        }
    }
}
