package de.sovity.edc.utils.jsonld;

import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonLdUtils {

    /**
     * Get the ID value of an object
     *
     * @param json json-ld
     * @return id or null
     */
    public static String id(JsonObject json) {
        if (json.containsKey("@id") && json.get("@id").getValueType() == JsonValue.ValueType.STRING) {
            return json.getString("@id");
        }
        return null;
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
            case ARRAY -> throw new IllegalStateException("unexpected array" + value);
            case OBJECT -> throw new IllegalStateException("unexpected object" + value);
            case STRING -> ((JsonString) value).getString();
            case NUMBER -> ((JsonNumber) value).bigDecimalValue().toString();
            case FALSE -> "false";
            case TRUE -> "true";
            case NULL -> null;
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
     * @param key key
     * @return string or null
     */
    public static String string(JsonObject object, String key) {
        return string(object.get(key));
    }

    /**
     * Get a list property while unwrapping values and only keeping objects.
     *
     * @param object json-ld
     * @param key key
     * @return list of values
     */
    public static List<JsonObject> listOfObjects(JsonObject object, String key) {
        return listOfObjects(object.get(key));
    }
}
