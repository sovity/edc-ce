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

package de.sovity.edc.utils.config.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.edc.spi.system.configuration.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

@Setter
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigProp {
    @NotNull
    private String property;

    @NotNull
    private String category;

    @NotNull
    private String description;

    /**
     * Turns off all required / defaulting logic, if false
     */
    private ConfigPropRequiredIfFn relevantIf;

    private boolean required;
    private ConfigPropRequiredIfFn requiredIf;
    private String defaultValue;
    private ConfigPropDefaultValueFn defaultValueFn;

    private boolean warnIfOverridden;
    private boolean warnIfUnset;

    public ConfigProp also(Consumer<ConfigProp> fn) {
        fn.accept(this);
        return this;
    }

    @Nullable
    public String getRaw(Map<String, String> props) {
        return props.get(property);
    }

    public String getStringOrNull(Config config) {
        return config.getString(property, null);
    }

    public String getStringOrThrow(Config config) {
        return config.getString(property);
    }

    public String getStringOrEmpty(Config config) {
        // Default should already be handled by ConfigProp
        return config.getString(property, "");
    }

    public Boolean getBoolean(Config config) {
        // Default should already be handled by ConfigProp
        return config.getBoolean(property);
    }

    public Integer getInt(Config config) {
        // Default should already be handled by ConfigProp
        return config.getInteger(property);
    }
}
