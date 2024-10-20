/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.utils.config;

import de.sovity.edc.utils.config.model.ConfigProp;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.eclipse.edc.spi.monitor.Monitor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class ConfigService {
    private final Monitor monitor;

    /**
     * Will be evaluated in sequence to apply default config values and validate the configuration.
     */
    private final List<ConfigProp> configProps;

    /**
     * Validate config and apply defaults
     *
     * @param properties raw config
     * @return config with defaults and validation
     */
    public Map<String, String> applyDefaults(Map<String, String> properties) {
        var withDefaults = new HashMap<>(translateToDotCase(properties));
        configProps.forEach(prop -> applyDefault(withDefaults, prop));
        return withDefaults;
    }

    private void applyDefault(Map<String, String> properties, ConfigProp prop) {
        if (prop.getOnlyValidateAndDefaultIf() != null && !prop.getOnlyValidateAndDefaultIf().predicate(properties)) {
            return;
        }
        String value = prop.getRaw(properties);
        warnIfRequired(prop, value);

        if (value != null) {
            return;
        }

        if (prop.isRequired()) {
            var message = "Missing required Config Property: %s \"%s\" (%s)".formatted(
                prop.getProperty(),
                prop.getDescription(),
                prop.getCategory()
            );
            throw new IllegalStateException(message);
        }

        var defaultValue = prop.getDefaultValue();
        if (prop.getDefaultValueFn() != null) {
            defaultValue = prop.getDefaultValueFn().apply(properties);
        }
        if (defaultValue != null) {
            properties.put(prop.getProperty(), defaultValue);
        }
    }

    private void warnIfRequired(ConfigProp prop, String value) {
        if (value != null && prop.isWarnIfOverridden()) {
            monitor.warning("Property set to 'warn if overriden': %s, value=%s \"%s\" (%s)".formatted(
                prop.getProperty(),
                value,
                prop.getDescription(),
                prop.getCategory()
            ));
        }

        if ((value == null || value.isBlank()) && prop.isWarnIfUnset()) {
            monitor.warning("Property set to 'warn if unset': %s, value=%s \"%s\" (%s)".formatted(
                prop.getProperty(),
                value,
                prop.getDescription(),
                prop.getCategory()
            ));
        }
    }

    private Map<String, String> translateToDotCase(Map<String, String> properties) {
        Map<String, String> result = new HashMap<>();
        properties.forEach((key, value) -> {
            String newKey = toDotCase(key);
            if (result.containsKey(newKey)) {
                monitor.warning("Duplicate Config Property: %s: %s -> %s".formatted(key, result.get(newKey), value));
            }
            result.put(newKey, value);
        });
        return result;
    }

    @NotNull
    private String toDotCase(String key) {
        return key.toLowerCase().replace("_", ".");
    }

    /**
     * Helper method used in our test utilities
     *
     * @param propertiesInput properties
     * @param configProps will be evaluated in sequence to apply default config values and validate the configuration.
     * @return edc config properties
     */
    public static Map<String, String> applyDefaults(
        Map<ConfigProp, String> propertiesInput,
        List<ConfigProp> configProps
    ) {
        var configService = new ConfigService(new ConsoleMonitor(), configProps);
        var properties = propertiesInput.entrySet().stream()
            .collect(toMap(e -> e.getKey().getProperty(), Map.Entry::getValue));
        return configService.applyDefaults(properties);
    }
}
