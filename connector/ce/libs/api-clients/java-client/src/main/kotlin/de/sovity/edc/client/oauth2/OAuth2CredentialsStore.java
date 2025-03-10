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

import lombok.SneakyThrows;

/**
 * Holds the Access Token and coordinates it between the Interceptor and the Authenticator.
 */
public class OAuth2CredentialsStore {
    private final OAuth2TokenFetcher tokenFetcher;
    private OAuth2TokenResponse tokenResponse = null;

    public OAuth2CredentialsStore(OAuth2TokenFetcher tokenFetcher) {
        this.tokenFetcher = tokenFetcher;
        this.fetchAccessTokenInternal();
    }

    public String getAccessToken() {
        synchronized (this) {
            if (tokenResponse == null) {
                fetchAccessTokenInternal();
            }
            return tokenResponse.getAccessToken();
        }
    }

    public String refreshAccessToken() {
        synchronized (this) {
            fetchAccessTokenInternal();
            return tokenResponse.getAccessToken();
        }
    }

    @SneakyThrows
    private void fetchAccessTokenInternal() {
        // If it crashes afterwards, the next request won't attempt to use the old token
        tokenResponse = null;
        tokenResponse = tokenFetcher.fetchToken();
    }

}
