/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.evaluation.utils

import de.sovity.edc.runtime.modules.model.ConfigPropCategory.IMPORTANT
import de.sovity.edc.runtime.modules.model.ConfigPropCategory.UNKNOWN
import de.sovity.edc.runtime.modules.model.ConfigPropRef
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.edc.spi.monitor.Monitor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argThat
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions

@ExtendWith(MockitoExtension::class)
class ConfigPropSafeEvaluatorTest {
    @Mock
    lateinit var monitor: Monitor

    @Mock
    lateinit var configPropErrorList: ConfigPropErrorList
    lateinit var configPropSafeEvaluator: ConfigPropSafeEvaluator
    lateinit var config: MutableInterceptableConfig

    @BeforeEach
    fun setup() {
        config = MutableInterceptableConfig.root()
        configPropSafeEvaluator = ConfigPropSafeEvaluator(
            configPropErrorList,
            monitor,
            ConfigPropEvaluator(configPropErrorList)
        )
    }


    @Test
    fun `test multiple definitions - non-conflicting by same value`() {
        // arrange
        val def1 = prop("prop").apply {
            defaultValue("value")
        }
        val def2 = prop("prop").apply {
            defaultValue("value")
        }

        // act
        val result = configPropSafeEvaluator.evaluateConfigPropSafe(config, "prop", listOf(def1, def2))

        // assert
        assertThat(result).isEqualTo("value")
        verifyNoInteractions(configPropErrorList)
    }


    @Test
    fun `test multiple definitions - non-conflicting by only one applying a default`() {
        // arrange
        val def1 = prop("prop").apply {
            defaultValue("value")
        }
        val def2 = prop("prop")

        // act
        val result = configPropSafeEvaluator.evaluateConfigPropSafe(config, "prop", listOf(def1, def2))

        // assert
        assertThat(result).isEqualTo("value")
        verifyNoInteractions(configPropErrorList)
    }


    @Test
    fun `test multiple definitions - conflicting`() {
        // arrange
        val def1 = prop("prop").apply {
            defaultValue("value")
        }
        val def2 = prop("prop").apply {
            defaultValue("notTheSameValue")
        }

        // act
        configPropSafeEvaluator.evaluateConfigPropSafe(config, "prop", listOf(def1, def2))

        // assert
        verify(configPropErrorList).addError(
            def1,
            "Config Prop 'prop' had multiple definitions in multiple modules that evaluated to different values: value, notTheSameValue"
        )
        verifyNoMoreInteractions(configPropErrorList)
    }

    @Test
    fun `test wrong property`() {
        // arrange
        val prop = prop("wrongProperty")

        // act
        configPropSafeEvaluator.evaluateConfigPropSafe(config, "prop", listOf(prop))

        // assert
        verify(configPropErrorList).addError(prop, "Trying to evaluate property 'prop' with ConfigProp 'wrongProperty'")
        verifyNoMoreInteractions(configPropErrorList)
    }


    @Test
    fun `test configPropDefs empty`() {
        // act
        configPropSafeEvaluator.evaluateConfigPropSafe(config, "prop", listOf())

        // assert
        verify(configPropErrorList).addError(
            argThat { property == "prop" },
            eq("Trying to evaluate property 'prop' with no ConfigProps")
        )
        verifyNoMoreInteractions(configPropErrorList)
    }


    @Test
    fun `test defaultValue`() {
        // arrange
        val prop = prop("prop").apply {
            defaultValue("value")
        }

        // act
        val result = configPropSafeEvaluator.evaluateConfigPropSafe(config, "prop", listOf(prop))

        // assert
        assertThat(result).isEqualTo("value")
        verifyNoInteractions(configPropErrorList)
    }


    @Test
    fun `test defaultValueFn`() {
        // arrange
        config.set("otherProp", "value")
        val prop = prop("prop").apply {
            defaultFromProp(ConfigPropRef("otherProp", "docs"))
        }

        // act
        val result = configPropSafeEvaluator.evaluateConfigPropSafe(config, "prop", listOf(prop))

        // assert
        assertThat(result).isEqualTo("value")
        verifyNoInteractions(configPropErrorList)
    }


    @Test
    fun `test warnIfUnset unset`() {
        // arrange
        val prop = prop("prop").apply {
            warnIfUnset = true
        }

        // act
        val result = configPropSafeEvaluator.evaluateConfigPropSafe(config, "prop", listOf(prop))

        // assert
        assertThat(result).isNull()
        verify(configPropErrorList).addWarning(prop, "Property set to 'warn if unset'")
        verifyNoMoreInteractions(configPropErrorList)
    }


    @Test
    fun `test warnIfUnset set`() {
        // arrange
        config.set("prop", "value")
        val prop = prop("prop").apply {
            warnIfUnset = true
        }

        // act
        val result = configPropSafeEvaluator.evaluateConfigPropSafe(config, "prop", listOf(prop))

        // assert
        assertThat(result).isEqualTo("value")
        verifyNoInteractions(configPropErrorList)
    }

    @Test
    fun `test warnIfOverridden unset`() {
        // arrange
        val prop = prop("prop").apply {
            warnIfOverridden = true
        }

        // act
        val result = configPropSafeEvaluator.evaluateConfigPropSafe(config, "prop", listOf(prop))

        // assert
        assertThat(result).isNull()
        verifyNoInteractions(configPropErrorList)
    }

    @Test
    fun `test warnIfOverridden set`() {
        // arrange
        config.set("prop", "override")
        val prop = prop("prop").apply {
            warnIfOverridden = true
        }

        // act
        val result = configPropSafeEvaluator.evaluateConfigPropSafe(config, "prop", listOf(prop))

        // assert
        assertThat(result).isEqualTo("override")
        verify(configPropErrorList).addWarning(prop, "Property set to 'warn if overriden' value=override")
        verifyNoMoreInteractions(configPropErrorList)
    }

    @Test
    fun `test not required unset`() {
        // arrange
        val prop = ConfigPropRef("prop", "docs").toConfigProp(IMPORTANT)

        // act
        val result = configPropSafeEvaluator.evaluateConfigPropSafe(config, "prop", listOf(prop))

        // assert
        assertThat(result).isNull()
        verifyNoInteractions(configPropErrorList)
    }

    private fun prop(propertyName: String) = ConfigPropRef(propertyName, "docs").toConfigProp(UNKNOWN)
}
