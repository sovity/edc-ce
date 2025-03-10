/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.evaluation

import de.sovity.edc.runtime.modules.evaluation.utils.ConfigPropQueue
import de.sovity.edc.runtime.modules.evaluation.utils.MutableInterceptableConfig
import de.sovity.edc.runtime.modules.evaluation.utils.RecursiveConfigEvaluator
import de.sovity.edc.runtime.modules.model.EdcModule
import org.eclipse.edc.spi.monitor.Monitor
import org.eclipse.edc.spi.system.configuration.Config

class EdcModuleSystemEvaluator(
    private val monitor: Monitor
) {
    companion object {
        fun evaluate(monitor: Monitor, rootModule: EdcModule, initialConfig: Config): EdcModuleSystemResult =
            EdcModuleSystemEvaluator(monitor).evaluate(rootModule, initialConfig)
    }

    /**
     * Fills the initial config with defaults and decides the active modules
     */
    fun evaluate(rootModule: EdcModule, initialConfig: Config): EdcModuleSystemResult {
        val queue = ConfigPropQueue()
        val rootMutableConfig = MutableInterceptableConfig.root(initialConfig)
        val recursiveConfigEvaluator = RecursiveConfigEvaluator(rootMutableConfig, queue, monitor)

        // Here's the plan:
        // - We try to evaluate all module activation conditions to get the list of active modules
        // - We accumulate property definitions along the way
        // - We evaluate property defaults only if explicitly needed for module activation conditions
        // - Once we have the full list of properties by the active modules, we evaluate the full config
        val activeModules = evaluateActiveModules(queue, recursiveConfigEvaluator, rootModule)

        // evaluate all config properties
        recursiveConfigEvaluator.drainAndEvaluateAllProperties()

        return EdcModuleSystemResult(
            activeModules = activeModules.toList(),
            evaluatedConfig = rootMutableConfig.toImmutableConfig()
        )
    }

    private fun evaluateActiveModules(
        queue: ConfigPropQueue,
        recursiveConfigEvaluator: RecursiveConfigEvaluator,
        rootModule: EdcModule
    ): MutableList<EdcModule> {
        val modules = mutableListOf<EdcModule>()

        fun visitActiveModuleDfs(module: EdcModule) {
            // Collect active module
            modules.add(module)

            // Accumulate property definitions for evaluation
            // Makes property definitions also available for immediate evaluation if required for a child module activation condition
            queue.addAll(module.getConfigProps())

            module.getChildModules()
                .filter {
                    recursiveConfigEvaluator.useConfig { config ->
                        // this might trigger an early evaluation of a config property
                        it.condition.fn(config)
                    }
                }
                .forEach { visitActiveModuleDfs(it.module) }
        }

        visitActiveModuleDfs(rootModule)
        return modules
    }
}
