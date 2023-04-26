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
import de.sovity.edc.client.gen.api.EnterpriseEditionApi;
import de.sovity.edc.client.gen.api.UiApi;
import de.sovity.edc.client.gen.api.UseCaseApi;
import de.sovity.edc.client.oauth2.Oauth2ClientCredentialsAuthenticator;
import de.sovity.edc.client.oauth2.Oauth2ClientCredentialsHandler;
import de.sovity.edc.client.oauth2.Oauth2ClientCredentialsInterceptor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * Builds {@link EdcClient}s.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EdcClientFactory {

    public static EdcClient newClient(EdcClientBuilder builder) {
        var apiClient = new ApiClient()
                .setServerIndex(null)
                .setBasePath(builder.managementApiUrl());

        if (StringUtils.isNotBlank(builder.managementApiKey())) {
            apiClient.addDefaultHeader("X-Api-Key", builder.managementApiKey());
        }

        if (builder.oauth2ClientCredentials() != null) {
            var handler = new Oauth2ClientCredentialsHandler(builder.oauth2ClientCredentials());
            var httpClient = apiClient.getHttpClient()
                    .newBuilder()
                    .addInterceptor(new Oauth2ClientCredentialsInterceptor(handler))
                    .authenticator(new Oauth2ClientCredentialsAuthenticator(handler))
                    .build();
            apiClient.setHttpClient(httpClient);
        }

        return new EdcClient(
                new UiApi(apiClient),
                new UseCaseApi(apiClient),
                new EnterpriseEditionApi(apiClient)
        );
    }
}
