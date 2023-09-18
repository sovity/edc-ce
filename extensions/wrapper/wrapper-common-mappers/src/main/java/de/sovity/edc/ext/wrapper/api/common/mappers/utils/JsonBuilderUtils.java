package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JsonBuilderUtils {

    protected static JsonObjectBuilder addNonNull(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, (String) value);
        }
        return builder;
    }

    protected static JsonObjectBuilder addNonNullArray(JsonObjectBuilder builder, String key, List<String> values) {
        if (values != null && !values.isEmpty()) {
            builder.add(key, Json.createArrayBuilder(values));
        }
        return builder;
    }

    protected static JsonObject mapToJson(Map<String, Object> map) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            builder.add(entry.getKey(),  Json.createValue((String) entry.getValue()));
        }

        return builder.build();
    }
}
