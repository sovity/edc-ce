/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules.runtime

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import de.sovity.edc.runtime.modules.RuntimeConfigProps
import org.eclipse.edc.spi.system.configuration.Config
import org.eclipse.edc.spi.system.configuration.ConfigFactory

/**
 * Build a [Config] on EDC Boot. This ignores [org.eclipse.edc.spi.system.ConfigurationExtension]s. Which is currently
 * a weakness of our config parsing.
 */
object InitialConfigFactory {
    fun initialConfigFromEnv(overrides: Map<String, String> = emptyMap()): Config {
        // ConfigurationExtensions are ignored so config-providing extensions are disabled and would have to be
        // manually added here
        return withEdcConfigJsonHandling(
            merged(
                ConfigFactory.fromEnvironment(System.getenv()),
                ConfigFactory.fromProperties(System.getProperties()),
                ConfigFactory.fromMap(overrides)
            )
        )
    }

    private fun withEdcConfigJsonHandling(config: Config): Config {
        val jsonConfig = RuntimeConfigProps.SOVITY_EDC_CONFIG_JSON.getStringOrEmpty(config)
        if (jsonConfig.isBlank()) {
            return config
        }

        val additionalConfig = try {
            ObjectMapper().readValue(
                jsonConfig,
                object : TypeReference<Map<String, String>>() {}
            ).mapKeys { it.key.lowercase().replace('_', '.') }
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to parse JSON config: $jsonConfig", e)
        }

        val key = RuntimeConfigProps.SOVITY_EDC_CONFIG_JSON.property
        require(key !in additionalConfig) {
            "The Config JSON in '$key' defined a nested key '$key', but recursion has been specifically disallowed."
        }

        return ConfigFactory.fromMap(
            additionalConfig + config.entries - key
        )
    }


    private fun merged(vararg configs: Config): Config {
        return ConfigFactory.fromMap(
            configs.map { it.entries }.reduce { a, b -> a + b }
        )
    }
}
