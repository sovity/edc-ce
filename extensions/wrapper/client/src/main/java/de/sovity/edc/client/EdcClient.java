package de.sovity.edc.client;

import de.sovity.edc.ext.wrapper.api.example.ExampleResource;

/**
 * API Client for our EDC API Wrapper.
 *
 * @param exampleClient Example API
 */
public record EdcClient(
        ExampleResource exampleClient
) {
    public static EdcClientBuilder builder() {
        return new EdcClientBuilder();
    }
}
