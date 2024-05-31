package de.sovity.edc.ext.wrapper.api.common.mappers;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestUtils {
    @NotNull
    @SneakyThrows
    public static String loadResourceAsString(String name) {
        return new String(Files.readAllBytes(Paths.get(TestUtils.class.getResource(name).toURI())));
    }
}
