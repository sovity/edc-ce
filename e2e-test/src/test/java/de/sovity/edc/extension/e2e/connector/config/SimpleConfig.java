package de.sovity.edc.extension.e2e.connector.config;

import java.util.Map;

public record SimpleConfig(String name, String value) implements EdcConfig {

    @Override
    public Map<String, String> toMap() {
        return Map.of(name, value);
    }
}
