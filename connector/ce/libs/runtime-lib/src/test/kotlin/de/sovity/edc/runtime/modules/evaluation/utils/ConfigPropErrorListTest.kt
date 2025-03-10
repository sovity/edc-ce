/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.evaluation.utils

import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.ConfigPropRef
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.eclipse.edc.spi.monitor.Monitor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions

@ExtendWith(MockitoExtension::class)
class ConfigPropErrorListTest {
    @Mock
    lateinit var monitor: Monitor

    @Test
    fun `test no errors or warnings`() {
        // arrange
        val errorList = ConfigPropErrorList(monitor)

        // act & assert
        errorList.throwAll()
        verifyNoInteractions(monitor)
    }

    @Test
    fun `test warning`() {
        // arrange
        val errorList = ConfigPropErrorList(monitor)
        val prop = prop("prop", "doc")

        // act
        errorList.addWarning(prop, "message")
        errorList.throwAll()

        // assert
        verify(monitor).warning(
            "Warnings occurred during configuration evaluation:\n" +
                "    - WARN prop: message. Documentation: doc"
        )
        verifyNoMoreInteractions(monitor)
    }

    @Test
    fun `test error`() {
        // arrange
        val errorList = ConfigPropErrorList(monitor)
        val prop = prop("prop", "doc")

        // act
        errorList.addError(prop, "message")
        assertThatThrownBy { errorList.throwAll() }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage(
                "Errors occurred during configuration evaluation:\n" +
                    "    - ERROR prop: message. Documentation: doc"
            )

        // assert
        verifyNoInteractions(monitor)
    }

    @Test
    fun `test ordering`() {
        // arrange
        val errorList = ConfigPropErrorList(monitor)
        val prop1 = prop("prop1", "doc1")
        val prop2 = prop("prop2", "doc2")

        errorList.addWarning(prop2, "warning2")
        errorList.addError(prop1, "message1")
        errorList.addWarning(prop1, "warning1")
        errorList.addError(prop2, "message2")

        // act
        assertThatThrownBy { errorList.throwAll() }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage(
                """
                Errors occurred during configuration evaluation:
                    - ERROR prop1: message1. Documentation: doc1
                    - ERROR prop2: message2. Documentation: doc2
                    - WARN prop1: warning1. Documentation: doc1
                    - WARN prop2: warning2. Documentation: doc2
                """.trimIndent()
            )
        verifyNoInteractions(monitor)
    }

    private fun prop(name: String, docs: String) = ConfigPropRef(name, docs).toConfigProp(ConfigPropCategory.UNKNOWN)
}
