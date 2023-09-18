package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@RequiredArgsConstructor
public class JsonBuilderUtils {

    protected static JsonObjectBuilder addNonNull(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, (String) value);
        }
        return builder;
    }

    protected static JsonObjectBuilder addNonNullArray(JsonObjectBuilder builder, String key, List<String> values) {
        if (CollectionUtils.isNotEmpty(values)) {
            builder.add(key, Json.createArrayBuilder(values));
        }
        return builder;
    }
}
