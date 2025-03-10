/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.evaluation.utils

import de.sovity.edc.runtime.modules.model.ConfigProp
import org.eclipse.edc.spi.monitor.Monitor

/**
 * Collects Evaluation Errors.
 *
 * We do this so we do not instantly fail on a single validation error, but have a chance to collect all errors as far
 * as possible and then display them all.
 */
class ConfigPropErrorList(
    private val monitor: Monitor
) {
    private val errors: MutableList<PropertyError> = mutableListOf()


    fun addWarning(property: ConfigProp, message: String) {
        errors.add(PropertyError(PropertyErrorLevel.WARN, property, message))
    }

    fun addError(property: ConfigProp, message: String) {
        errors.add(PropertyError(PropertyErrorLevel.ERROR, property, message))
    }

    fun throwAll() {
        val lines = errors
            .distinct()
            .sortedWith(compareBy({ it.level.order }, { it.property.property }))
            .joinToString("") {
                @Suppress("MaxLineLength")
                "\n    - ${it.level.name} ${it.property.property}: ${it.message.removeSuffix(".")}. Documentation: ${it.property.documentation}"
            }

        if (errors.any { it.level == PropertyErrorLevel.ERROR }) {
            error("Errors occurred during configuration evaluation:$lines")
        } else if (errors.any { it.level == PropertyErrorLevel.WARN }) {
            monitor.warning("Warnings occurred during configuration evaluation:$lines")
        }
    }

    private data class PropertyError(
        val level: PropertyErrorLevel,
        val property: ConfigProp,
        val message: String
    )

    private enum class PropertyErrorLevel(val order: Int) {
        ERROR(0),
        WARN(1),
    }
}
