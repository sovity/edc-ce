/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.client;

import de.sovity.edc.client.gen.ApiClient;
import de.sovity.edc.client.gen.api.EnterpriseEditionApi;
import de.sovity.edc.client.gen.api.UiApi;
import de.sovity.edc.client.gen.api.UseCaseApi;
import de.sovity.edc.client.oauth2.OAuth2CredentialsAuthenticator;
import de.sovity.edc.client.oauth2.OAuth2CredentialsInterceptor;
import de.sovity.edc.client.oauth2.OAuth2CredentialsStore;
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

        if (builder.customConfigurer() != null) {
            builder.customConfigurer().accept(apiClient);
        }

        return new EdcClient(
            new UiApi(apiClient),
            new UseCaseApi(apiClient),
            new EnterpriseEditionApi(apiClient)
        );
    }
}
