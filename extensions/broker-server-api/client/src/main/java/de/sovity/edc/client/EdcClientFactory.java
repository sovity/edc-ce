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

import de.sovity.edc.ext.brokerserver.client.gen.ApiClient;
import de.sovity.edc.ext.brokerserver.client.gen.api.BrokerServerApi;
import de.sovity.edc.client.oauth2.OAuth2CredentialsAuthenticator;
import de.sovity.edc.client.oauth2.OAuth2CredentialsStore;
import de.sovity.edc.client.oauth2.OAuth2CredentialsInterceptor;
import de.sovity.edc.client.oauth2.OAuth2TokenFetcher;
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
            var tokenFetcher = new OAuth2TokenFetcher(builder.oauth2ClientCredentials());
            var handler = new OAuth2CredentialsStore(tokenFetcher);
            var httpClient = apiClient.getHttpClient()
                    .newBuilder()
                    .addInterceptor(new OAuth2CredentialsInterceptor(handler))
                    .authenticator(new OAuth2CredentialsAuthenticator(handler))
                    .build();
            apiClient.setHttpClient(httpClient);
        }

        return new EdcClient(
                new BrokerServerApi(apiClient)
        );
    }
}
