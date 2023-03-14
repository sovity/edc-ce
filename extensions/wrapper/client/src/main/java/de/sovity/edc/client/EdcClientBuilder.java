package de.sovity.edc.client;

import de.sovity.edc.ext.wrapper.api.example.ExampleResource;
import jakarta.ws.rs.client.ClientRequestFilter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.net.URI;

public class EdcClientBuilder {
    /**
     * Instantiate new {@link EdcClient}
     *
     * @param dataManagementUrl    api url
     * @param dataManagementApiKey api key (might not be required)
     * @return new EdcClient
     */
    public static EdcClient newClient(@NonNull String dataManagementUrl, @NonNull String dataManagementApiKey) {
        var exampleResourceResource = instantiateResource(ExampleResource.class, dataManagementUrl, dataManagementApiKey);
        return new EdcClient(exampleResourceResource);
    }

    @SneakyThrows
    private static <T> T instantiateResource(
            Class<T> resourceClass,
            @NonNull String dataManagementUrl,
            @NonNull String dataManagementApiKey
    ) {
        return RestClientBuilder.newBuilder()
                .baseUri(new URI(dataManagementUrl))
                .register(buildApiKeyClientRequestFilter(dataManagementApiKey))
                .build(resourceClass);
    }

    private static ClientRequestFilter buildApiKeyClientRequestFilter(String dataManagementApiKey) {
        return requestContext -> requestContext.getHeaders().putSingle("x-api-key", dataManagementApiKey);
    }
}
