package de.sovity.edc.utils.catalog.utils;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.StringReader;
import java.io.StringWriter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

    public static JsonObject parseJson(String string) {
        try (var reader = Json.createReader(new StringReader(string))) {
            return reader.readObject();
        }
    }

    public static String toJson(JsonValue json) {
        var sw = new StringWriter();
        try (var writer = Json.createWriter(sw)) {
            writer.write(json);
            return sw.toString();
        }
    }

}
