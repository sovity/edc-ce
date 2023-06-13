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

package de.sovity.edc.ext.brokerserver.dao.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;

/**
 * Some things are easier to fetch as json into a string with JooQ.
 * In that case we need to deserialize that string  into an object of our choice afterwards.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonDeserializationUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<List<String>>> TYPE_STRING_LIST_2 = new TypeReference<>() {
    };

    @SneakyThrows
    public static List<List<String>> deserializeStringArray2(String json) {
        return OBJECT_MAPPER.readValue(json, TYPE_STRING_LIST_2);
    }
}
