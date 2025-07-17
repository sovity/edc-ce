/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.model

import org.eclipse.edc.spi.system.configuration.Config

class ConfigPropRef(
    /**
     * EDC property in dot-case. i.e. "edc.my.property"
     */
    val property: String,
    /**
     * Documentation of the property.
     * When the property is used in a module, it is possible to override this documentation, depending on how its used.
     */
    val defaultDocumentation: String
) {

    fun toConfigProp(category: ConfigPropCategory): ConfigProp =
        ConfigProp(property, category, defaultDocumentation, defaultDocumentation)

    fun getStringOrNull(config: Config): String? =
        config.getString(property, null)

    fun getStringOrThrow(config: Config): String =
        config.getString(property)

    fun getStringOrEmpty(config: Config): String =
        // Default should already be handled by ConfigProp
        config.getString(property, "")

    fun getBooleanOrFalse(config: Config): Boolean =
        // Default should already be handled by ConfigProp
        config.getBoolean(property, false)

    fun getIntOrThrow(config: Config): Int =
        config.getInteger(property)

    fun getIntOrNull(config: Config): Int? =
        config.getInteger(property, null)

    fun getListNonEmpty(config: Config): List<String> {
        return getListOrEmpty(config).also {
            require(it.isNotEmpty()) { "Property $property must not be empty. Expecting a comma separated list." }
        }
    }

    fun withWildcardValue(suffix: String): ConfigPropRef {
        require(property.endsWith(".*")) { "Property $property is not a wildcard property." }
        return ConfigPropRef(
            "${property.removeSuffix(".*")}.$suffix",
            "Filled out wildcard property `$property` with value `$suffix`. $defaultDocumentation"
        )
    }

    fun getWildcardValues(config: Config): Map<String, String> {
        require(property.endsWith(".*")) { "Property $property is not a wildcard property." }
        val prefix = property.removeSuffix("*")
        return config.entries
            .filter { it.key.startsWith(prefix) }
            .mapKeys { it.key.removePrefix(prefix) }
    }

    fun getListOrEmpty(config: Config): List<String> {
        return config.getString(property, "")
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ConfigPropRef) return false

        return property == other.property
    }

    override fun hashCode(): Int =
        property.hashCode()
}
