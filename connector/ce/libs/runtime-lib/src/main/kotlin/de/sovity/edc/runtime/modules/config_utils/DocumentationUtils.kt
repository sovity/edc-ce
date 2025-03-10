/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.config_utils

import de.sovity.edc.runtime.modules.model.ConfigPropEnumValue

object DocumentationUtils {
    /**
     * Formats the available options for a configuration property
     */
    fun documentAlternatives(description: String, choices: List<ConfigPropEnumValue>): String {
        val choicesEnumerated = choices.joinToString("\n") {
            " * `${it.value}`: ${it.documentation}"
        }

        return "${description.removeSuffix(".")}. Available options:\n\n$choicesEnumerated"
    }
}

