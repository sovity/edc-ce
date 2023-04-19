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

import de.sovity.edc.client.gen.ApiClient;
import de.sovity.edc.client.gen.api.ExampleApi;
import de.sovity.edc.client.gen.api.UiApi;
import de.sovity.edc.client.gen.api.UseCaseApi;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Builds {@link EdcClient}s.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EdcClientFactory {

    public static EdcClient newClient(EdcClientBuilder builder) {
        var apiClient = new ApiClient()
                .setServerIndex(null)
                .setBasePath(builder.managementApiUrl())
                .addDefaultHeader("x-api-key", builder.managementApiKey());

        return new EdcClient(
                new ExampleApi(apiClient),
                new UiApi(apiClient),
                new UseCaseApi(apiClient)
        );
    }
}
