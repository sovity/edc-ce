package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import de.sovity.edc.utils.JsonUtils;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonBuilderUtils {

    protected static JsonObjectBuilder addNonNull(JsonObjectBuilder builder, String key, String value) {
        if (value != null) {
            builder.add(key, value);
        }
        return builder;
    }

    protected static JsonObjectBuilder addNonNullArray(JsonObjectBuilder builder, String key, List<String> values) {
        if (CollectionUtils.isNotEmpty(values)) {
            builder.add(key, Json.createArrayBuilder(values));
        }
        return builder;
    }

    protected static JsonObjectBuilder addNonNullJsonValue(JsonObjectBuilder builder, String key, String jsonString) {
        if (jsonString == null) {
            return builder;
        }
        var value = JsonUtils.parseJsonValue(jsonString);
        if (value.getValueType() == JsonValue.ValueType.NULL) {
            return builder;
        }

        builder.add(key, value);
        return builder;
    }
}
