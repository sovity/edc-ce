/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.extension.e2e.connector.config.api;

import de.sovity.edc.extension.e2e.connector.config.api.auth.ApiKeyAuthProvider;
import de.sovity.edc.extension.e2e.connector.config.api.auth.AuthProvider;
import de.sovity.edc.extension.e2e.connector.config.api.auth.NoneAuthProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URI;

import static de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroup.CONTROL;
import static de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroup.DEFAULT;
import static de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroup.MANAGEMENT;
import static de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroup.PROTOCOL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EdcApiConfigFactory {
    private static final String BASE_URL = "http://localhost";

    /**
     * Configures EDC API Endpoints by conventions with given port offset.
     *
     * @param firstPort        port offset
     * @param managementApiKey management API key
     * @return {@link EdcApiConfig}
     */
    public static EdcApiConfig configureApi(int firstPort, String managementApiKey) {
        var defaultApiGroup = unprotected(DEFAULT, "/api", firstPort + 1);
        var managementApiGroup = apiKeyAuth(MANAGEMENT, "/api/management", firstPort + 2, managementApiKey);
        var protocolApiGroup = unprotected(PROTOCOL, "/api/dsp", firstPort + 3);
        var unprotected = unprotected(CONTROL, "/api/control", firstPort + 4);

        return EdcApiConfig.builder()
                .defaultApiGroup(defaultApiGroup)
                .protocolApiGroup(protocolApiGroup)
                .managementApiGroup(managementApiGroup)
                .controlApiGroup(unprotected)
                .build();
    }

    public static EdcApiGroupConfig fromUri(EdcApiGroup edcApiGroup, URI uri, AuthProvider authProvider) {
        return new EdcApiGroupConfig(
                edcApiGroup,
                "%s://%s".formatted(uri.getScheme(), uri.getHost()),
                uri.getPort(),
                uri.getPath(),
                authProvider
        );
    }

    private static EdcApiGroupConfig unprotected(
            EdcApiGroup edcApiGroup,
            String path,
            int port) {
        return new EdcApiGroupConfig(
                edcApiGroup,
                BASE_URL,
                port,
                path,
                new NoneAuthProvider()
        );
    }

    private static EdcApiGroupConfig apiKeyAuth(
            EdcApiGroup apiGroup,
            String path,
            int port,
            String apiKey) {
        return new EdcApiGroupConfig(
                apiGroup,
                BASE_URL,
                port,
                path,
                new ApiKeyAuthProvider("X-Api-Key", apiKey));
    }
}
