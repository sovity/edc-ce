/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.utils

fun String?.lowercaseWords(): List<String> {
    if (this.isNullOrBlank()) {
        return listOf()
    }

    return this.split("\\s+".toRegex())
        .map { it.trim().lowercase() }
        .filter { it.isNotBlank() }
}

fun String.escapeForSqlLike(): String {
    return this.replace("\\", "\\\\")
        .replace("%", "\\%")
        .replace("_", "\\_")
}
