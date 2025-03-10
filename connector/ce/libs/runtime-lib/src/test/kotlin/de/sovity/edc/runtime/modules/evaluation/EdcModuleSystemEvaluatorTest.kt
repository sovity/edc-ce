/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.evaluation

import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.ConfigPropRef
import de.sovity.edc.runtime.modules.model.EdcModule
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.eclipse.edc.spi.monitor.ConsoleMonitor
import org.eclipse.edc.spi.monitor.Monitor
import org.eclipse.edc.spi.system.configuration.ConfigFactory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class EdcModuleSystemEvaluatorTest {
    @Spy
    var monitor: Monitor = ConsoleMonitor()

    val c = ConfigPropCategory.OVERRIDES

    @Test
    fun `test empty`() {
        // arrange
        val module = EdcModule("module", "documentation")

        // act
        val result = evaluate(module, "a" to "b")

        // assert
        assertThat(result.evaluatedConfig.entries).isEqualTo(mapOf("a" to "b"))
    }

    @Test
    fun `test child`() {
        // arrange
        val module = EdcModule("module", "documentation").apply {
            val child = EdcModule("child", "documentation").apply {
                property(c, ConfigPropRef("a", "documentation")) {
                    defaultValue("b")
                }
            }
            module(child)
        }

        // act
        val result = evaluate(module)

        // assert
        assertThat(result.evaluatedConfig.entries).isEqualTo(mapOf("a" to "b"))
    }

    @Test
    fun `test oneOf no child enabled`() {
        // arrange
        val module = EdcModule("module", "documentation").apply {
            val childA = EdcModule("child-a", "documentation").apply {
                property(c, ConfigPropRef("a.prop", "documentation")) {
                    defaultValue("A")
                }
            }
            val childB = EdcModule("child-b", "documentation").apply {
                property(c, ConfigPropRef("b.prop", "documentation")) {
                    defaultValue("B")
                }
            }
            moduleOneOf(
                c,
                ConfigPropRef("child-active", "documentation"),
                { },
                childA,
                childB
            )
        }

        // act
        val result = evaluate(module)

        // assert
        assertThat(result.evaluatedConfig.entries).isEmpty()
    }

    @Test
    fun `test oneOf child a enabled`() {
        // arrange
        val module = EdcModule("module", "documentation").apply {
            val childA = EdcModule("child-a", "documentation").apply {
                property(c, ConfigPropRef("a.prop", "documentation")) {
                    defaultValue("A")
                }
            }
            val childB = EdcModule("child-b", "documentation").apply {
                property(c, ConfigPropRef("b.prop", "documentation")) {
                    defaultValue("B")
                }
            }
            moduleOneOf(
                c,
                ConfigPropRef("child-active", "documentation"),
                { },
                childA,
                childB
            )
        }

        // act
        val result = evaluate(module, "child-active" to "child-a")

        // assert
        assertThat(result.evaluatedConfig.entries).isEqualTo(mapOf("child-active" to "child-a", "a.prop" to "A"))
    }

    @Test
    fun `test oneOf unsupported option chosen`() {
        // arrange
        val module = EdcModule("module", "documentation").apply {
            val childA = EdcModule("child-a", "documentation").apply {
                property(c, ConfigPropRef("a.prop", "documentation")) {
                    defaultValue("A")
                }
            }
            val childB = EdcModule("child-b", "documentation").apply {
                property(c, ConfigPropRef("b.prop", "documentation")) {
                    defaultValue("B")
                }
            }
            moduleOneOf(
                c,
                ConfigPropRef("child-active", "documentation"),
                { },
                childA,
                childB
            )
        }
        // act & assert
        assertThatThrownBy { evaluate(module, "child-active" to "child-c") }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("ERROR child-active: Needs to be one of: \"child-a\", \"child-b\".")
    }

    @Test
    fun `test module activation property causing early evaluation`() {
        // arrange
        val module = EdcModule("module", "documentation").apply {
            property(c, ConfigPropRef("all-children-active", "enable all children by default")) {
                defaultValue("true")
            }

            val child = EdcModule("child", "documentation").apply {
                property(c, ConfigPropRef("a", "documentation")) {
                    defaultValue("b")
                }
            }
            moduleIfTrue(c, child, ConfigPropRef("child-active", "documentation")) {
                defaultFromProp(ConfigPropRef("all-children-active", ""))
            }
        }

        // act
        val result = evaluate(module)

        // assert
        assertThat(result.evaluatedConfig.entries).isEqualTo(
            mapOf(
                "all-children-active" to "true",
                "child-active" to "true",
                "a" to "b"
            )
        )
    }

    @Test
    fun `test module activation property with recursive re-use`() {
        // arrange
        val module = EdcModule("module", "documentation").apply {
            property(c, ConfigPropRef("all-children-active", "enable all children by default")) {
                defaultValue("true")
            }

            val child = EdcModule("child", "documentation").apply {
                val childOfChild = EdcModule("child-of-child", "documentation").apply {
                    property(c, ConfigPropRef("a", "documentation")) {
                        defaultValue("b")
                    }
                }

                moduleIfTrue(c, childOfChild, ConfigPropRef("child-of-child-active", "documentation")) {
                    defaultFromProp(ConfigPropRef("all-children-active", ""))
                }
            }
            moduleIfTrue(c, child, ConfigPropRef("child-active", "documentation")) {
                defaultFromProp(ConfigPropRef("all-children-active", ""))
            }
        }

        // act
        val result = evaluate(module)

        // assert
        assertThat(result.evaluatedConfig.entries).isEqualTo(
            mapOf(
                "all-children-active" to "true",
                "child-active" to "true",
                "child-of-child-active" to "true",
                "a" to "b"
            )
        )
    }

    @Test
    fun `test module activation property re-defined in child module`() {
        // arrange
        val module = EdcModule("module", "documentation").apply {
            property(c, ConfigPropRef("all-children-active", "enable all children by default")) {
                defaultValue("true")
            }

            val child = EdcModule("child", "documentation").apply {
                property(c, ConfigPropRef("all-children-active", "re-definition of all-children-active"))
            }
            moduleIfTrue(c, child, ConfigPropRef("child-active", "documentation")) {
                defaultFromProp(ConfigPropRef("all-children-active", ""))
            }
        }

        // act & assert
        assertThatThrownBy { evaluate(module) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Properties 'all-children-active' have already been visited and are now being added again.")
    }

    @Test
    fun `test multi-step evaluation ok`() {
        // arrange
        val module = EdcModule("module", "documentation").apply {
            property(c, ConfigPropRef("a", "a")) {
                defaultFromProp(ConfigPropRef("b", ""))
            }
            property(c, ConfigPropRef("b", "b")) {
                defaultFromProp(ConfigPropRef("c", ""))
            }
            property(c, ConfigPropRef("c", "c")) {
                defaultValue("value")
            }
        }

        // act
        val result = evaluate(module)

        // assert
        assertThat(result.evaluatedConfig.entries).isEqualTo(
            mapOf(
                "a" to "value",
                "b" to "value",
                "c" to "value"
            )
        )
    }

    @Test
    fun `test multi-step evaluation circular dependency`() {
        // arrange
        val module = EdcModule("module", "documentation").apply {
            property(c, ConfigPropRef("a", "a docs")) {
                defaultFromProp(ConfigPropRef("b", ""))
            }
            property(c, ConfigPropRef("b", "b docs")) {
                defaultFromProp(ConfigPropRef("c", ""))
            }
            property(c, ConfigPropRef("c", "c docs")) {
                defaultFromProp(ConfigPropRef("a", ""))
            }
        }

        // act & assert
        assertThatThrownBy { evaluate(module) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining(
                "Errors occurred during configuration evaluation:\n" +
                    "    - ERROR c: Circular dependency detected: a -> b -> c -> a. Documentation: c docs"
            )
    }

    private fun evaluate(module: EdcModule, vararg config: Pair<String, String>): EdcModuleSystemResult {
        val initialConfig = ConfigFactory.fromMap(mapOf(*config))
        return EdcModuleSystemEvaluator.evaluate(monitor, module, initialConfig)
    }
}
