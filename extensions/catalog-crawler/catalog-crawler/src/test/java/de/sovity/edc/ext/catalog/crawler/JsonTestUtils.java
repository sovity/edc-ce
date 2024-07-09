package de.sovity.edc.ext.catalog.crawler;

import lombok.SneakyThrows;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

public class JsonTestUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @SneakyThrows
    public static String serialize(Object obj) {
        return OBJECT_MAPPER.writeValueAsString(obj);
    }

    @SneakyThrows
    public static <T> T deserialize(String json, Class<T> clazz) {
        return OBJECT_MAPPER.readValue(json, clazz);
    }

    public static <T> T jsonCast(Object obj, Class<T> clazz) {
        return deserialize(serialize(obj), clazz);
    }
}
