/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services.catalog

import org.eclipse.edc.util.reflection.PathItem

/**
 * See [PathItem.parse]
 */
object QuerySpecUtils {
    fun encodeAssetPropertyPath(assetPropertyPath: List<String>): String {
        require(assetPropertyPath.isNotEmpty()) {
            "Property path must not be empty"
        }
        return encodePropertyPath(listOf("properties") + assetPropertyPath)
    }

    fun decodeAssetPropertyPath(assetPropertyPath: String): List<String> =
        decodePropertyPath(assetPropertyPath).filterIndexed { index, property ->
            index != 0 || property != "properties"
        }

    private fun encodePropertyPath(propertyPath: List<String>): String {
        require(propertyPath.isNotEmpty()) {
            "Property path must not be empty"
        }
        require(propertyPath.none { it.contains("'") }) {
            "Property of path must not contain single quotes, as it is used as an escape character: $propertyPath"
        }
        return propertyPath.joinToString(".") { "'$it'" }
    }

    private fun decodePropertyPath(propertyPath: String): List<String> {
        if (propertyPath == "" || propertyPath == "''") {
            return emptyList()
        }

        if (!propertyPath.contains("'")) {
            return decodePropertyPath("'$propertyPath'")
        }

        return PathItem.parse(propertyPath).map { it.toString() }
    }
}
