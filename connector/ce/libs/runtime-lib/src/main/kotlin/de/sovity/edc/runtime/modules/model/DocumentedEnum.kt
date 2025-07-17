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
interface DocumentedEnum {
    val documentation: String
    val name: String

    fun isSelectedOption(rawValue: String?): Boolean =
        EnumUtils.isSelectedEnumOptionIgnoreCase(name, rawValue)

    val nameKebabCase get() = name.lowercase().replace("_", "-")

    companion object {
        inline fun <reified T> getSelectedValue(stringOrNull: String?): T
            where T : DocumentedEnum, T : Enum<T> =
            EnumUtils.getSelectedEnumOptionIgnoreCase(stringOrNull)
    }
}
