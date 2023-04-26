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

import com.google.api.client.auth.oauth2.ClientCredentialsTokenRequest;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.SneakyThrows;

/**
 * Holds the Access Token and coordinates it between the Interceptor and the Authenticator.
 */
public class Oauth2ClientCredentialsHandler {
    private final OAuth2ClientCredentials oAuth2Config;
    private TokenResponse tokenResponse = null;

    public Oauth2ClientCredentialsHandler(OAuth2ClientCredentials oAuth2Config) {
        this.oAuth2Config = oAuth2Config;
        this.fetchAccessTokenInternal();
    }


    public String getNewAccessToken() {
        synchronized (this) {
            fetchAccessTokenInternal();
            return tokenResponse.getAccessToken();
        }
    }

    public String getAccessToken() {
        synchronized (this) {
            if (tokenResponse == null) {
                fetchAccessTokenInternal();
            }
            return tokenResponse.getAccessToken();
        }
    }

    @SneakyThrows
    private void fetchAccessTokenInternal() {
        // If it crashes in the following, the next request won't attempt to use the old token
        tokenResponse = null;

        var tokenRequest = new ClientCredentialsTokenRequest(
                new NetHttpTransport(),
                new GsonFactory(),
                new GenericUrl(oAuth2Config.getTokenUrl())
        )
                .setClientAuthentication(new ClientParametersAuthentication(
                        oAuth2Config.getClientId(), oAuth2Config.getClientSecret()))
                .setGrantType("client_credentials");
        tokenResponse = tokenRequest.execute();
    }

}
