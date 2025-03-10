/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.model

import de.sovity.edc.runtime.modules.RuntimeConfigProps
import de.sovity.edc.runtime.modules.config_utils.DocumentationUtils
import org.eclipse.edc.spi.system.configuration.Config

/**
 * A [ConfigPropRef] in the context of an [EdcModule].
 *
 * This class has two tasks:
 *  - Define validation and default value calculation logic for the property
 *  - Provide documentation for our documentation generator for this property
 */
class ConfigProp(
    val property: String,
    val category: ConfigPropCategory,

    /**
     * See [ConfigPropRef.defaultDocumentation]
     */
    val defaultDocumentation: String,

    /**
     * Documentation for how this property is used by the module
     */
    var documentation: String,

    /**
     * Turns off all required / defaulting logic, if false
     */
    var onlyValidateAndDefaultIf: DocumentedFn<Config, Boolean>? = null,

    /**
     * Required or not. Will be evaluated before defaults
     */
    var requiredFn: DocumentedFn<Config, Boolean>? = null,

    /**
     * Default value
     */
    var defaultValueFn: DocumentedFn<Config, String?>? = null,

    /**
     * Only applied if value is non-null. An error state should return a non-null string
     */
    var validateFn: ((value: String, config: Config) -> String?)? = null,

    var warnIfOverridden: Boolean = false,
    var warnIfUnset: Boolean = false,

    ) {
    fun enumValues(values: List<ConfigPropEnumValue>) {
        documentation = DocumentationUtils.documentAlternatives(documentation, values)
        require(validateFn == null) { "Can't stack two validation functions" }
        validateFn = { value, _ ->
            if (!values.any { it.value == value }) {
                "Value must be one of ${values.joinToString { "'${it.value}'" }}"
            }

            null
        }
    }

    fun <T> enumValues(clazz: Class<T>) where T : Enum<T>, T : DocumentedEnum {
        enumValues(clazz.enumConstants.map { ConfigPropEnumValue(it.nameKebabCase, it.documentation) })
    }

    fun required() {
        requiredFn = DocumentedFn("Required") { true }
    }

    fun requiredInProd() {
        requiredFn = DocumentedFn("Required in production") {
            RuntimeConfigProps.SOVITY_ENVIRONMENT.getStringOrNull(it) == "PRODUCTION"
        }
    }

    fun defaultValue(value: String) {
        defaultValueFn = DocumentedFn("Defaults to `$value`") { value }
    }

    fun vaultKeyNameFor(vaultEntry: VaultEntry) {
        defaultValue(vaultEntry.key)
    }

    fun defaultValueOutsideProd(
        value: String
    ) {
        defaultValueFn = DocumentedFn("Defaults to `$value` outside prod") {
            if (RuntimeConfigProps.SOVITY_ENVIRONMENT.getStringOrNull(it) == "PRODUCTION") {
                null
            } else {
                value
            }
        }
    }

    fun defaultFromProp(prop: ConfigPropRef) {
        defaultValueFn = DocumentedFn("Defaults to value from `${prop.property}`") {
            prop.getStringOrNull(it)
        }
    }

    fun defaultFromPropPlus(prop: ConfigPropRef, plus: Int) {
        defaultValueFn = DocumentedFn("Defaults to value from `${prop.property}` plus `$plus`") {
            prop.getIntOrThrow(it).plus(plus).toString()
        }
    }
}
