/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.runtime

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class InitialConfigFactoryTest {

    @Test
    fun `test edc config json handling`() {
        val config = mapOf(
            "prop" to "prop-value",
            "sovity.edc.config.json" to """{
                "nested-prop": "nested-value"
            }""".trimIndent()
        )

        val result = InitialConfigFactory.initialConfigFromEnv(config)

        assertThat(result.entries).containsAllEntriesOf(
            mapOf(
                "prop" to "prop-value",
                "nested-prop" to "nested-value",
            )
        )
        assertThat(result.entries).doesNotContainKey("sovity.edc.config.json")
    }

    @Test
    fun `test env var takes precedence over nested`() {
        val config = mapOf(
            "prop" to "env-value",
            "sovity.edc.config.json" to """{
                "prop": "nested-value"
            }""".trimIndent()
        )

        val result = InitialConfigFactory.initialConfigFromEnv(config)

        assertThat(result.entries).containsAllEntriesOf(
            mapOf(
                "prop" to "env-value",
            )
        )
    }

    @Test
    fun `test edc config json handling refusing recursion`() {
        fun escape(s: String): String {
            return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\""
        }

        val config = mapOf(
            "prop" to "prop-value",
            "sovity.edc.config.json" to """{
                    "nested-prop": "nested-value",
                    "sovity.edc.config.json": ${escape("""{
                        "nested2-dotcase": "nested2-dotcase-value"
                    }""".trimIndent())}
            }""".trimIndent()
        )

        assertThatThrownBy {
            InitialConfigFactory.initialConfigFromEnv(config)
        }.hasMessage("The Config JSON in 'sovity.edc.config.json' defined a nested key 'sovity.edc.config.json', but recursion has been specifically disallowed.")
    }
}
