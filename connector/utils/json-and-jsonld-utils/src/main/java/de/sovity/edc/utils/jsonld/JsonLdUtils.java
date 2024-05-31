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

package de.sovity.edc.utils.jsonld;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.JsonDocument;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonLdUtils {
    private static final JsonDocument EMPTY_CONTEXT_DOCUMENT = JsonDocument.of(Json.createObjectBuilder()
            .add(Prop.CONTEXT, Json.createObjectBuilder())
            .build());

    /**
     * Compact JSON-LD, but don't compact property names to namespaces.
     *
     * @param json json-ld
     * @return compacted values
     */
    public static JsonObject tryCompact(JsonObject json) {
        try {
            return com.apicatalog.jsonld.JsonLd.compact(JsonDocument.of(json), EMPTY_CONTEXT_DOCUMENT).get();
        } catch (JsonLdError e) {
            return json;
        }
    }

    /**
     * Compact JSON-LD, but don't compact property names to namespaces.
     *
     * @param json json-ld
     * @return compacted values
     */
    public static JsonObject expandKeysOnly(JsonObject json) {
        try {
            var expanded = com.apicatalog.jsonld.JsonLd.expand(JsonDocument.of(json)).get();
            return com.apicatalog.jsonld.JsonLd.compact(JsonDocument.of(expanded), EMPTY_CONTEXT_DOCUMENT).get();
        } catch (JsonLdError e) {
            return json;
        }
    }

    public static boolean isEmptyArray(JsonValue json) {
        return list(json).isEmpty();
    }

    public static boolean isEmptyObject(JsonValue json) {
        return object(json).isEmpty();
    }


    /**
     * Get the ID value of an object
     *
     * @param json json-ld
     * @return id or null
     */
    public static String id(JsonObject json) {
        return string(json, "@id");
    }

    /**
     * Get a string property
     *
     * @param json json-ld
     * @return string value or null
     */
    public static String string(JsonValue json) {
        var value = value(json);
        if (value == null) {
            return null;
        }

        return switch (value.getValueType()) {
            case STRING -> ((JsonString) value).getString();
            case NUMBER -> ((JsonNumber) value).bigDecimalValue().toString();
            case FALSE -> "false";
            case TRUE -> "true";
            case NULL -> null;
            // We do this over throwing errors because we want to be able to handle invalid json-ld
            case ARRAY, OBJECT -> JsonUtils.toJson(value);
        };
    }

    /**
     * Get a offset date time property
     *
     * @param json json-ld
     * @return offset date time value or null
     */
    public static LocalDate localDate(JsonValue json) {
        var str = string(json);
        if (str == null) {
            return null;
        }

        try {
            return LocalDate.parse(str);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Get a boolean property
     *
     * @param json json-ld
     * @return boolean value or null
     */
    public static Boolean bool(JsonValue json) {
        var value = value(json);
        if (value == null) {
            return null;
        }

        return switch (value.getValueType()) {
            case STRING -> switch (((JsonString) value).getString().toLowerCase()) {
                case "true" -> Boolean.TRUE;
                case "false" -> Boolean.FALSE;
                default -> null;
            };
            case FALSE -> Boolean.FALSE;
            case TRUE -> Boolean.TRUE;
            case NUMBER, NULL, ARRAY, OBJECT -> null;
        };
    }

    /**
     * Get a list property.
     *
     * @param json json-ld
     * @return list of values
     */
    public static List<JsonValue> list(JsonValue json) {
        return switch (json.getValueType()) {
            case ARRAY -> json.asJsonArray();
            case FALSE, TRUE, NUMBER, STRING, OBJECT -> List.of(json);
            case NULL -> List.of();
        };
    }

    /**
     * Get a list property while unwrapping values and only keeping objects.
     *
     * @param json json-ld
     * @return list of values
     */
    public static List<JsonObject> listOfObjects(JsonValue json) {
        return list(json).stream()
                .map(JsonLdUtils::value) // unwrap @value
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .toList();
    }

    /**
     * Get the innermost @value of an object. Also removes wrappings in lists.
     *
     * @param json json-ld
     * @return innermost @value
     */
    public static JsonValue value(JsonValue json) {
        return switch (json.getValueType()) {
            case ARRAY -> {
                var array = json.asJsonArray();
                if (array.isEmpty()) {
                    yield null;
                }
                yield value(array.get(0));
            }
            case OBJECT -> {
                var object = json.asJsonObject();
                if (object.containsKey("@value")) {
                    yield value(object.get("@value"));
                } else {
                    yield object;
                }
            }
            case STRING, NUMBER, FALSE, TRUE, NULL -> json;
        };
    }

    /**
     * Get a string property
     *
     * @param object json-ld
     * @param key    key
     * @return string or null
     */
    public static String string(JsonObject object, String key) {
        JsonValue field = object.get(key);
        if (field == null) {
            return null;
        }
        return string(field);
    }

    /**
     * Get a offset date time property
     *
     * @param object json-ld
     * @param key    key
     * @return offset date time or null
     */
    public static LocalDate localDate(JsonObject object, String key) {
        JsonValue field = object.get(key);
        if (field == null) {
            return null;
        }
        return localDate(field);
    }

    /**
     * Get an object property. Defaults to an empty object for ease of use if not found.
     *
     * @param object json-ld
     * @param key    key
     * @return string or null
     */
    public static JsonObject object(JsonObject object, String key) {
        return object(object.get(key));
    }

    /**
     * Get an object property. Defaults to an empty object for ease of use if not found.
     *
     * @param field json-ld
     * @return string or null
     */
    public static JsonObject object(JsonValue field) {
        if (field == null) {
            return JsonValue.EMPTY_JSON_OBJECT;
        }

        var unwrapped = value(field);
        if (unwrapped == null || unwrapped.getValueType() != JsonValue.ValueType.OBJECT) {
            return JsonValue.EMPTY_JSON_OBJECT;
        }

        return (JsonObject) unwrapped;
    }

    /**
     * Get a list property while unwrapping values and only keeping objects.
     *
     * @param object json-ld
     * @param key    key
     * @return list of values
     */
    public static List<JsonObject> listOfObjects(JsonObject object, String key) {
        JsonValue field = object.get(key);
        if (field == null) {
            return List.of();
        }
        return listOfObjects(field);
    }

    /**
     * Get a list of strings. defaults to empty list
     *
     * @param object json-ld
     * @param key    key
     * @return string list or empty list
     */
    public static List<String> stringList(JsonObject object, String key) {
        JsonValue field = object.get(key);
        if (field == null) {
            return List.of();
        }
        return list(field).stream()
                .map(JsonLdUtils::string)
                .toList();
    }

    /**
     * Get a boolean property. defaults to null
     *
     * @param object json-ld
     * @param key    key
     * @return boolean or null
     */
    public static Boolean bool(JsonObject object, String key) {
        JsonValue field = object.get(key);
        if (field == null) {
            return null;
        }
        return bool(field);
    }
}
