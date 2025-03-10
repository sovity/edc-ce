/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.evaluation.utils

import de.sovity.edc.runtime.modules.model.ConfigProp
import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.ConfigPropRef
import org.eclipse.edc.spi.monitor.Monitor

/**
 * Wrapper around [ConfigPropEvaluator] that catches exceptions, because exceptions during single
 * evaluations should also be handled softly
 */
class ConfigPropSafeEvaluator(
    private val configPropErrorList: ConfigPropErrorList,
    private val monitor: Monitor,
    private val configPropEvaluator: ConfigPropEvaluator
) {

    /**
     * Wraps [ConfigPropEvaluator.evaluateConfigProp] with exception handling
     */
    fun evaluateConfigPropSafe(
        mutableInterceptableConfig: MutableInterceptableConfig,
        property: String,
        configPropDefs: List<ConfigProp>
    ): String? {
        try {
            return configPropEvaluator.evaluateConfigProp(mutableInterceptableConfig, property, configPropDefs)
        } catch (e: InterruptedException) {
            throw e
        } catch (e: Exception) {
            monitor.severe("Exception during property evaluation of '$property'", e)

            configPropErrorList.addError(
                configPropDefs.firstOrNull() ?: dummyProp(property),
                e.message ?: e.javaClass.name
            )

            return null
        }
    }

    private fun dummyProp(property: String) = ConfigPropRef(
        property,
        "An unchecked exception occurred."
    ).toConfigProp(ConfigPropCategory.UNKNOWN)
}
