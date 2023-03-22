/*
 *  Copyright (c) 2022 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.client;

import de.sovity.edc.ext.wrapper.api.example.ExampleResource;
import jakarta.ws.rs.client.ClientRequestFilter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.net.URI;

/**
 * Builds {@link EdcClient}s.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EdcClientFactory {

    public static EdcClient newClient(EdcClientBuilder builder) {
        var dataManagementUrl = builder.managementApiUrl();
        var dataManagementApiKey = builder.managementApiKey();

        var exampleResource = instantiateResource(ExampleResource.class, dataManagementUrl, dataManagementApiKey);

        return new EdcClient(exampleResource);
    }

    @SneakyThrows
    private static <T> T instantiateResource(
            @NonNull Class<T> resourceClass,
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
