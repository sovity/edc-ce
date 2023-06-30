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

package de.sovity.edc.client.oauth2;

import de.sovity.edc.ext.brokerserver.client.gen.ApiClient;
import de.sovity.edc.ext.brokerserver.client.gen.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;

/**
 * OAuth2 Token Response Fetcher for the "Client Credentials Grant" Flow
 */
@RequiredArgsConstructor
public class OAuth2TokenFetcher {
    private final OAuth2ClientCredentials clientCredentials;
    private final ApiClient apiClient = new ApiClient();

    /**
     * Fetch an access token for a "Client Credentials" Grant
     *
     * @return the token response including the access token
     */
    @SneakyThrows
    public OAuth2TokenResponse fetchToken() {
        var formData = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", clientCredentials.getClientId())
                .add("client_secret", clientCredentials.getClientSecret())
                .build();

        var request = new Request.Builder()
                .url(clientCredentials.getTokenUrl())
                .post(formData)
                .build();

        // Re-use the Utils for OkHttp from the OpenAPI generator
        Call call = apiClient.getHttpClient().newCall(request);
        ApiResponse<OAuth2TokenResponse> response = apiClient.execute(call, OAuth2TokenResponse.class);
        return response.getData();
    }
}
