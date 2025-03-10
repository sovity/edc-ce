/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.evaluation.utils

import de.sovity.edc.runtime.simple_di.evaluation.EvaluationPath
import org.eclipse.edc.spi.monitor.Monitor
import org.eclipse.edc.spi.system.configuration.Config

/**
 * Stateful orchestrator of one config evaluation process. Delegates between the [ConfigPropQueue]
 * and the [MutableInterceptableConfig].
 */
class RecursiveConfigEvaluator(
    private val rootMutableConfig: MutableInterceptableConfig,
    private val queue: ConfigPropQueue,
    private val monitor: Monitor
) {
    // these units are created here because they are stateful
    private val errorList = ConfigPropErrorList(monitor)
    private val evaluator = ConfigPropSafeEvaluator(
        errorList,
        monitor,
        ConfigPropEvaluator(errorList)
    )

    /**
     * Access the [Config] with un-evaluated properties getting evaluated on demand
     */
    fun <T> useConfig(fn: (config: Config) -> T): T {
        val config = rootMutableConfig.withInterceptor {
            // Recursively evaluate config when accessing any property
            ensureDefinedAndEvaluated(it, EvaluationPath())
        }
        return fn(config)
    }


    /**
     * Before using a property, we want to ensure:
     *
     *  - that all default value generation has been applied
     *  - that there are no circular dependencies when trying to generate said defaults recursively
     */
    private fun ensureDefinedAndEvaluated(property: String, parentPath: EvaluationPath) {
        if (queue.isCompleted(property)) {
            return
        }

        // This entire path thing is only done to give better error messages
        val currentPath = parentPath.forChild(property)
        require(!queue.isVisited(property)) { "Circular dependency detected: $currentPath" }

        // "visiting" starts the evaluation
        val configPropDefs = queue.visit(property)

        // Recursion: On resolving any other property, we do recursion, but with the child path
        val configWithChangedInterceptor = rootMutableConfig.withInterceptor {
            if (it != property) {
                ensureDefinedAndEvaluated(it, parentPath = currentPath)
            }
        }

        // Evaluate the property, this call causes recursion when the evaluation tries to access any other property
        val value = evaluator.evaluateConfigPropSafe(
            configWithChangedInterceptor,
            property,
            configPropDefs
        )

        // accept the new value
        value?.let { rootMutableConfig.set(property, it) }

        // "completing" frees up the property from circular dependency detection
        queue.complete(property)
    }

    /**
     * Drains the queue
     *
     * At the end all config property definitions of the active modules should have been handled
     */
    fun drainAndEvaluateAllProperties(): Config {
        // Drain queue until empty
        while (queue.hasNext()) {
            ensureDefinedAndEvaluated(queue.nextProperty(), EvaluationPath())
        }

        errorList.throwAll()
        return rootMutableConfig.toImmutableConfig()
    }
}
