/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.libs.mappers.asset.utils;

import de.sovity.edc.runtime.simple_di.Service;
import org.eclipse.edc.spi.types.domain.DataAddress;

import java.util.HashMap;
import java.util.Map;

@Service
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
