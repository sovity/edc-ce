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

package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.types.domain.DataAddress;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class EdcPropertyUtils {

    /**
     * Converts a {@code Map<String, Object>} to {@code Map<String, String>}.
     * <p>
     * Our API forsakes asset properties that are complex objects / JSON and only keeps string
     * properties.
     *
     * @param map all properties
     * @return string properties
     */
    public Map<String, String> truncateToMapOfString(Map<String, Object> map) {
        Map<String, String> result = new HashMap<>();

        if (map == null) {
            return result;
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();

            String valueString;
            if (value == null) {
                valueString = null;
            } else if (value instanceof String str) {
                valueString = str;
            } else if (value instanceof Double) {
                valueString = String.valueOf(value);
            } else if (value instanceof Integer) {
                valueString = String.valueOf(value);
            } else {
                continue;
            }

            result.put(entry.getKey(), valueString);
        }
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes", "java:S1905"})
    public Map<String, Object> toMapOfObject(Map<String, String> map) {
        if (map == null) {
            return Map.of();
        }
        return new HashMap<>((Map<String, Object>) (Map) map);
    }

    public DataAddress buildDataAddress(Map<String, String> properties) {
        return DataAddress.Builder.newInstance()
                .properties(toMapOfObject(properties))
                .build();
    }
}
