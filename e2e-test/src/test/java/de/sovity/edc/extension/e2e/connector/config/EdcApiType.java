package de.sovity.edc.extension.e2e.connector.config;

public enum EdcApiType {
    DEFAULT("default"),
    PROTOCOL("protocol"),
    MANAGEMENT("management"),
    CONTROL("control");

    private final String name;

    EdcApiType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}