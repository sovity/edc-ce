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
