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

    public static String id(JsonObject json) {
        if (json.containsKey("@id") && json.get("@id").getValueType() == JsonValue.ValueType.STRING) {
            return json.getString("@id");
        }
        return null;
    }

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

    public static List<JsonObject> arrayOfObjects(JsonValue json) {
        return array(json).stream()
                .map(JsonLdUtils::value) // unwrap @value
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .toList();
    }

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

    public static List<JsonValue> array(JsonValue json) {
        return switch (json.getValueType()) {
            case ARRAY -> json.asJsonArray();
            case FALSE, TRUE, NUMBER, STRING, OBJECT -> List.of(json);
            case NULL -> List.of();
        };
    }
}
