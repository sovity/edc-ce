package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.utils.JsonUtils;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.helper.Validate;

import java.util.List;
import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

public class JsonAssertsUtils {

    public static void assertEqualJson(
        JsonValue actual,
        JsonValue expected
    ) {

        var actualJson = JsonUtils.toJson(actual);
        var expectedJson = JsonUtils.toJson(expected);
        try {
            assertThatJson(actualJson).isEqualTo(expectedJson);
        } catch (AssertionError e) {
            var expectedStable = JsonUtils.toJsonPretty(stabilize(expected));
            var actualStable = JsonUtils.toJsonPretty(stabilize(actual));
            System.out.println("Actual: " + actualStable);
            System.out.println("Expected: " + expectedStable);
            throw new org.opentest4j.AssertionFailedError(
                "JSONs are not equal. Click <Show Difference> by IntelliJ to see a diff",
                expectedStable,
                actualStable
            );
        }
    }

    public static void assertIsEqualExcludingPaths(
        JsonObject actual,
        JsonObject expected,
        List<String>... exclude
    ) {
        var actualModified = actual;
        var expectedModified = expected;
        for (var path : exclude) {
            actualModified = removePath(actualModified, path);
            expectedModified = removePath(expectedModified, path);
        }

        assertEqualJson(actualModified, expectedModified);
    }

    private static JsonObject removePath(JsonObject obj, List<String> path) {
        Validate.isTrue(CollectionUtils.isNotEmpty(path), "path");
        if (obj == null) {
            return null;
        }

        var key = path.get(0);
        if (!obj.containsKey(key)) {
            return obj;
        }

        if (path.size() == 1) {
            return Json.createObjectBuilder(obj).remove(key).build();
        }

        var value = obj.getJsonObject(key);
        value = removePath(value, path.subList(1, path.size()));

        return Json.createObjectBuilder(obj)
            .remove(key)
            .add(key, value)
            .build();
    }

    private static JsonValue stabilize(JsonValue value) {
        switch (value.getValueType()) {
            case ARRAY:
                var sorted = value.asJsonArray().stream()
                    .map(JsonAssertsUtils::stabilize)
                    .toList();
                return Json.createArrayBuilder(sorted).build();
            case OBJECT:
                JsonObjectBuilder builder = Json.createObjectBuilder();
                value.asJsonObject().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> builder.add(entry.getKey(), stabilize(entry.getValue())));
                return builder.build();
            default:
                return value;
        }
    }
}
