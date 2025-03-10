/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.evaluation.utils

import de.sovity.edc.runtime.modules.model.ConfigProp

/**
 * Evaluates [ConfigProp]s depending on their settings.
 * Applies defaults, logs warnings, etc.
 */
class ConfigPropEvaluator(private val configPropErrorList: ConfigPropErrorList) {

    fun evaluateConfigProp(
        mutableInterceptableConfig: MutableInterceptableConfig,
        property: String,
        configPropDefs: List<ConfigProp>
    ): String? {
        requireOfProperty(configPropDefs, property)
        require(configPropDefs.isNotEmpty()) { "Trying to evaluate property '$property' with no ConfigProps" }

        // handle same property getting defined in multiple modules
        // the defaulting should not be colliding
        val evaluations = configPropDefs.mapNotNull { evaluateConfigPropDef(mutableInterceptableConfig, it) }
        require(evaluations.distinct().size <= 1) {
            "Config Prop '$property' had multiple definitions in multiple modules that evaluated to different values: ${
                evaluations.joinToString(", ")
            }"
        }

        return evaluations.firstOrNull()
    }

    private fun evaluateConfigPropDef(
        config: MutableInterceptableConfig,
        prop: ConfigProp
    ): String? {
        if (prop.onlyValidateAndDefaultIf?.fn?.let { it(config) } == false) {
            return null
        }

        var value = config.getString(prop.property, null)
        warnIfRequired(prop, value)

        value = applyDefaults(value, prop, config)
        validate(value, prop, config)
        return value
    }

    private fun applyDefaults(value: String?, prop: ConfigProp, config: MutableInterceptableConfig): String? {
        if (value != null) {
            return value
        }
        return prop.defaultValueFn?.fn?.let { it(config) }
    }

    private fun validate(value: String?, prop: ConfigProp, config: MutableInterceptableConfig) {
        val isRequired = prop.requiredFn?.fn?.let { it(config) } == true

        if (isRequired && value == null) {
            configPropErrorList.addError(
                prop,
                "Property is required, but not set"
            )
        }

        if (value != null) {
            prop.validateFn?.let { it(value, config) }
                ?.let { errorMessage -> configPropErrorList.addError(prop, errorMessage) }
        }
    }

    private fun warnIfRequired(prop: ConfigProp, value: String?) {
        if (!value.isNullOrBlank() && prop.warnIfOverridden) {
            configPropErrorList.addWarning(
                prop,
                "Property set to 'warn if overriden' value=$value"
            )
        }

        if (value.isNullOrBlank() && prop.warnIfUnset) {
            configPropErrorList.addWarning(
                prop,
                "Property set to 'warn if unset'"
            )
        }
    }

    private fun requireOfProperty(
        configPropDefs: List<ConfigProp>,
        property: String
    ) {
        require(configPropDefs.all { it.property == property }) {
            val otherProperty = configPropDefs.first { it.property != property }.property
            "Trying to evaluate property '$property' with ConfigProp '$otherProperty'"
        }
    }
}
