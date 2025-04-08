/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.utils

object ResourceUtils {

    @JvmStatic
    fun loadResourceAsString(name: String): String {
        val bytes = ResourceUtils::class.java.getResourceAsStream(name)?.readAllBytes()

        require(bytes != null) {
            "Failed to load the resource $name"
        }

        return String(bytes)
    }

}
