/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.model

/**
 * Enums that implement this interface can be directly used to specify the available values for a [ConfigProp].
 */
interface DocumentedEnumTest {
    val documentation: String
    val name: String

    fun isSelectedOption(rawValue: String) = name == rawValue.uppercase()
        .replace(" ", "_")
        .replace("-", "_")
        .replace(".", "_")

    val nameKebabCase get() = name.lowercase().replace("_", "-")
}
