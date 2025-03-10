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
package de.sovity.edc.utils;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonGenerator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

    public static JsonObject parseJsonObj(String string) {
        try (var reader = Json.createReader(new StringReader(string))) {
            return reader.readObject();
        }
    }

    public static JsonValue parseJsonValue(String string) {
        try (var reader = Json.createReader(new StringReader(string))) {
            return reader.readValue();
        }
    }

    public static String toJson(JsonValue json) {
        if (json == null) {
            return "null";
        }

        var sw = new StringWriter();
        try (var writer = Json.createWriter(sw)) {
            writer.write(json);
            return sw.toString();
        }
    }

    public static String toJsonPretty(JsonValue json) {
        if (json == null) {
            return "null";
        }

        var config = Map.of(JsonGenerator.PRETTY_PRINTING, true);

        var sw = new StringWriter();
        try (var writer = Json.createWriterFactory(config).createWriter(sw)) {
            writer.write(json);
            return sw.toString();
        }
    }

}
