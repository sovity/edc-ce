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
import org.eclipse.edc.spi.monitor.Monitor

object DevLoggingUtils {
    fun printConfigAndEdcJars(monitor: Monitor, evaluated: EdcModuleSystemResult) {
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
}
