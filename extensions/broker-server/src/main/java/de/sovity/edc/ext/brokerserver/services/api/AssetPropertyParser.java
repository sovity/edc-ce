/*
 *  Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.brokerserver.services.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Map;

@RequiredArgsConstructor
public class AssetPropertyParser {
    private final ObjectMapper objectMapper;

    private final TypeReference<Map<String, String>> typeToken = new TypeReference<>() {
    };

    @SneakyThrows
    public Map<String, String> parsePropertiesFromJsonString(String assetPropertiesJson) {
        return objectMapper.readValue(assetPropertiesJson, typeToken);
    }
}
