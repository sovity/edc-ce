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
package de.sovity.edc.client.oauth2;

import de.sovity.edc.client.gen.ApiClient;
import de.sovity.edc.client.gen.ApiResponse;
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
