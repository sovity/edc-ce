/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.model

object EnumUtils {
    fun isSelectedEnumOptionIgnoreCase(name: String, rawValue: String?): Boolean =
        name == (rawValue ?: "").uppercase()
            .replace(" ", "_")
            .replace("-", "_")
            .replace(".", "_")


    inline fun <reified T> getSelectedEnumOptionIgnoreCase(stringOrNull: String?): T
        where T : Enum<T> {
        return enumValues<T>().firstOrNull { isSelectedEnumOptionIgnoreCase(it.name, stringOrNull) }
            ?: error("Invalid value for ${T::class.java.simpleName}: $stringOrNull")
    }
}
