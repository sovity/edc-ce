package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
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
}
