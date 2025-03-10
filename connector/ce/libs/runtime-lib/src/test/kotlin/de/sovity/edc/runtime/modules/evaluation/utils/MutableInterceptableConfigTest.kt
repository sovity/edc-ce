/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.evaluation.utils

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.eclipse.edc.spi.EdcException
import org.eclipse.edc.spi.system.configuration.ConfigFactory
import org.eclipse.edc.spi.system.configuration.ConfigImpl
import org.junit.jupiter.api.Test


class MutableInterceptableConfigTest {
    @Test
    fun root() {
        // arrange
        val config = ConfigFactory.fromMap(mapOf("a" to "b"))
        val configMutable = MutableInterceptableConfig.root(config)
        configMutable.set("c", "d")

        // act & assert
        assertThat(config.entries).isEqualTo(mapOf("a" to "b"))
        assertThat(configMutable.entries).isEqualTo(mapOf("a" to "b", "c" to "d"))
    }

    @Test
    fun withInterceptor() {
        // arrange
        val mutable = mutableMapOf<String, String>()
        val root = MutableInterceptableConfig(mutable) {}
        val withInterceptor = root.withInterceptor {
            mutable["a"] = "b"
        }

        // act & assert
        assertThat(withInterceptor.getString("a")).isEqualTo("b")
        assertThat(mutable).isEqualTo(mapOf("a" to "b"))
    }

    @Test
    fun toImmutableConfig() {
        // arrange
        var mutable = mutableMapOf("a" to "b")
        val config = MutableInterceptableConfig(mutable) {}
        mutable["c"] = "d"

        // act
        val configCopy = config.toImmutableConfig()
        mutable["e"] = "f"

        // assert
        assertThat(configCopy.entries).isEqualTo(mapOf("a" to "b", "c" to "d"))
        assertThat(configCopy).isInstanceOf(ConfigImpl::class.java)
    }

    @Test
    fun set() {
        // arrange
        var mutable = mutableMapOf("a" to "b")
        val config = MutableInterceptableConfig(mutable) {}

        // act
        config.set("c", "d")

        // assert
        assertThat(config.entries).isEqualTo(mapOf("a" to "b", "c" to "d"))
        assertThat(config.entries).isEqualTo(mutable)
    }

    @Test
    fun getString1_exists() {
        // arrange
        var config = MutableInterceptableConfig.root()
        config = config.withInterceptor {
            if (it == "a") {
                config.set("a", "b")
            }
        }

        // act & assert
        assertThat(config.getString("a")).isEqualTo("b")
        assertThatThrownBy { config.getString("doesnotexist") }
            .isInstanceOf(EdcException::class.java)
            .hasMessage("No setting found for key doesnotexist")
    }

    @Test
    fun getString1_doesNotExist() {
        // arrange
        var called = false
        val config = MutableInterceptableConfig.root().withInterceptor {
            if (it == "a") {
                called = true
            }
        }

        // act & assert
        assertThatThrownBy { config.getString("a") }
            .isInstanceOf(EdcException::class.java)
            .hasMessage("No setting found for key a")
        assertThat(called).isTrue
    }

    @Test
    fun getString2_exists() {
        // arrange
        var config = MutableInterceptableConfig.root()
        config = config.withInterceptor {
            if (it == "a") {
                config.set("a", "b")
            }
        }

        // act & assert
        assertThat(config.getString("a", "c")).isEqualTo("b")
    }

    @Test
    fun getString2_doesNotExist() {
        // arrange
        var called = false
        val config = MutableInterceptableConfig.root().withInterceptor {
            if (it == "a") {
                called = true
            }
        }

        // act & assert
        assertThat(config.getString("a", "c")).isEqualTo("c")
        assertThat(called).isTrue
    }

    @Test
    fun getInteger1_exists() {
        // arrange
        var config = MutableInterceptableConfig.root()
        config = config.withInterceptor {
            if (it == "a") {
                config.set("a", "1")
            }
        }

        // act & assert
        assertThat(config.getInteger("a")).isEqualTo(1)
    }

    @Test
    fun getInteger1_doesNotExist() {
        // arrange
        var called = false
        val config = MutableInterceptableConfig.root().withInterceptor {
            if (it == "a") {
                called = true
            }
        }

        // act & assert
        assertThatThrownBy { config.getInteger("a") }
            .isInstanceOf(EdcException::class.java)
            .hasMessage("No setting found for key a")
        assertThat(called).isTrue
    }

    @Test
    fun getInteger1_failedParse() {
        // arrange
        val config = MutableInterceptableConfig.root()
        config.set("a", "b")

        // act & assert
        assertThatThrownBy { config.getInteger("a") }
            .isInstanceOf(EdcException::class.java)
            .hasMessage("Setting a with value b cannot be parsed to integer")
    }

    @Test
    fun getInteger2_exists() {
        // arrange
        var config = MutableInterceptableConfig.root()
        config = config.withInterceptor {
            if (it == "a") {
                config.set("a", "1")
            }
        }

        // act & assert
        assertThat(config.getInteger("a", 2)).isEqualTo(1)
    }

    @Test
    fun getInteger2_doesNotExist() {
        // arrange
        var called = false
        val config = MutableInterceptableConfig.root().withInterceptor {
            if (it == "a") {
                called = true
            }
        }

        // act & assert
        assertThat(config.getInteger("a", 2)).isEqualTo(2)
        assertThat(called).isTrue
    }

    @Test
    fun getInteger2_failedParse() {
        // arrange
        val config = MutableInterceptableConfig.root()
        config.set("a", "b")

        // act & assert
        assertThatThrownBy { config.getInteger("a", 2) }
            .isInstanceOf(EdcException::class.java)
            .hasMessage("Setting a with value b cannot be parsed to integer")
    }

    @Test
    fun getLong1_exists() {
        // arrange
        var config = MutableInterceptableConfig.root()
        config = config.withInterceptor {
            if (it == "a") {
                config.set("a", "1")
            }
        }

        // act & assert
        assertThat(config.getLong("a")).isEqualTo(1L)
    }

    @Test
    fun getLong1_doesNotExist() {
        // arrange
        var called = false
        val config = MutableInterceptableConfig.root().withInterceptor {
            if (it == "a") {
                called = true
            }
        }

        // act & assert
        assertThatThrownBy { config.getLong("a") }
            .isInstanceOf(EdcException::class.java)
            .hasMessage("No setting found for key a")
        assertThat(called).isTrue
    }

    @Test
    fun getLong1_failedParse() {
        // arrange
        val config = MutableInterceptableConfig.root()
        config.set("a", "b")

        // act & assert
        assertThatThrownBy { config.getLong("a") }
            .isInstanceOf(EdcException::class.java)
            .hasMessage("Setting a with value b cannot be parsed to long")
    }

    @Test
    fun getLong2_exists() {
        // arrange
        var config = MutableInterceptableConfig.root()
        config = config.withInterceptor {
            if (it == "a") {
                config.set("a", "1")
            }
        }

        // act & assert
        assertThat(config.getLong("a", 2L)).isEqualTo(1L)
    }

    @Test
    fun getLong2_doesNotExist() {
        // arrange
        var called = false
        val config = MutableInterceptableConfig.root().withInterceptor {
            if (it == "a") {
                called = true
            }
        }

        // act & assert
        assertThat(config.getLong("a", 2L)).isEqualTo(2L)
        assertThat(called).isTrue
    }

    @Test
    fun getLong2_failedParse() {
        // arrange
        val config = MutableInterceptableConfig.root()
        config.set("a", "b")

        // act & assert
        assertThatThrownBy { config.getLong("a", 2L) }
            .isInstanceOf(EdcException::class.java)
            .hasMessage("Setting a with value b cannot be parsed to long")
    }

    @Test
    fun getBoolean1_exists() {
        // arrange
        var config = MutableInterceptableConfig.root()
        config = config.withInterceptor {
            if (it == "a") {
                config.set("a", "true")
            }
        }

        // act & assert
        assertThat(config.getBoolean("a")).isTrue
    }

    @Test
    fun getBoolean1_doesNotExist() {
        // arrange
        var called = false
        val config = MutableInterceptableConfig.root().withInterceptor {
            if (it == "a") {
                called = true
            }
        }

        // act & assert
        assertThatThrownBy { config.getBoolean("a") }
            .isInstanceOf(EdcException::class.java)
            .hasMessage("No setting found for key a")
        assertThat(called).isTrue
    }

    @Test
    fun getBoolean1_failedParse() {
        // arrange
        val config = MutableInterceptableConfig.root()
        config.set("a", "b")

        // act & assert
        assertThatThrownBy { config.getBoolean("a") }
            .isInstanceOf(EdcException::class.java)
            .hasMessage("Setting a with value b cannot be parsed to boolean")
    }

    @Test
    fun getBoolean2_exists() {
        // arrange
        var config = MutableInterceptableConfig.root()
        config = config.withInterceptor {
            if (it == "a") {
                config.set("a", "true")
            }
        }

        // act & assert
        assertThat(config.getBoolean("a", false)).isTrue
    }

    @Test
    fun getBoolean2_doesNotExist() {
        // arrange
        var called = false
        val config = MutableInterceptableConfig.root().withInterceptor {
            if (it == "a") {
                called = true
            }
        }

        // act & assert
        assertThat(config.getBoolean("a", false)).isFalse
        assertThat(called).isTrue
    }

    @Test
    fun getBoolean2_failedParse() {
        // arrange
        val config = MutableInterceptableConfig.root()
        config.set("a", "b")

        // act & assert
        assertThatThrownBy { config.getBoolean("a", false) }
            .isInstanceOf(EdcException::class.java)
            .hasMessage("Setting a with value b cannot be parsed to boolean")
    }

    @Test
    fun getEntries() {
        // arrange
        val mutable = mutableMapOf("a" to "b")
        val config = MutableInterceptableConfig(mutable) {}

        // act & assert
        assertThat(config.entries).isSameAs(mutable)
    }

    @Test
    fun getConfig() {
        // arrange
        val config = MutableInterceptableConfig.root()

        // act & assert
        assertThatThrownBy { config.getConfig(null) }
            .hasMessage("Unsupported method for MutableInterceptableConfig")
    }

    @Test
    fun merge() {
        // arrange
        val config = MutableInterceptableConfig.root()

        // act & assert
        assertThatThrownBy { config.merge(ConfigFactory.empty()) }
            .hasMessage("Unsupported method for MutableInterceptableConfig")
    }

    @Test
    fun partition() {
        // arrange
        val config = MutableInterceptableConfig.root()

        // act & assert
        assertThatThrownBy { config.partition() }
            .hasMessage("Unsupported method for MutableInterceptableConfig")
    }

    @Test
    fun getRelativeEntries0() {
        // arrange
        val config = MutableInterceptableConfig.root()

        // act & assert
        assertThatThrownBy { config.relativeEntries }
            .hasMessage("Unsupported method for MutableInterceptableConfig")
    }

    @Test
    fun getRelativeEntries1() {
        // arrange
        val config = MutableInterceptableConfig.root()

        // act & assert
        assertThatThrownBy { config.getRelativeEntries("a") }
            .hasMessage("Unsupported method for MutableInterceptableConfig")
    }

    @Test
    fun currentNode() {
        // arrange
        val config = MutableInterceptableConfig.root()

        // act & assert
        assertThatThrownBy { config.currentNode() }
            .hasMessage("Unsupported method for MutableInterceptableConfig")
    }

    @Test
    fun isLeaf() {
        // arrange
        val config = MutableInterceptableConfig.root()

        // act & assert
        assertThatThrownBy { config.isLeaf() }
            .hasMessage("Unsupported method for MutableInterceptableConfig")
    }

    @Test
    fun hasKey() {
        // arrange
        val config = MutableInterceptableConfig.root()

        // act & assert
        assertThatThrownBy { config.hasKey("a") }
            .hasMessage("Unsupported method for MutableInterceptableConfig")
    }

    @Test
    fun hasPath() {
        // arrange
        val config = MutableInterceptableConfig.root()

        // act & assert
        assertThatThrownBy { config.hasPath("a") }
            .hasMessage("Unsupported method for MutableInterceptableConfig")
    }
}
