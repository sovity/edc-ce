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
 *      sovity GmbH - init
 */

package de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonBuilderUtils {

    public static void addNonNull(JsonObjectBuilder builder, String key, String value) {
        if (value != null) {
            builder.add(key, value);
        }
    }

    public static void addNotBlank(JsonObjectBuilder builder, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            builder.add(key, value.trim());
        }
    }

    public static void addNonNull(JsonObjectBuilder builder, String key, LocalDate value) {
        if (value != null) {
            builder.add(key, value.toString());
        }
    }

    /**
     * Adds non-null non-blank trimmed items as a JSON Array
     *
     * @param builder target object
     * @param key     key
     * @param values  list of values
     */
    public static void addNotBlankStringArray(JsonObjectBuilder builder, String key, List<String> values) {
        var filteredItems = (values == null ? Stream.<String>of() : values.stream())
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(StringUtils::isNotBlank)
            .toList();

        if (CollectionUtils.isNotEmpty(filteredItems)) {
            builder.add(key, Json.createArrayBuilder(filteredItems));
        }
    }

    public static void addNonNullJsonValue(JsonObjectBuilder builder, String key, JsonValue value) {
        if (value == null || value.getValueType() == JsonValue.ValueType.NULL) {
            return;
        }

        builder.add(key, value);
    }
}
