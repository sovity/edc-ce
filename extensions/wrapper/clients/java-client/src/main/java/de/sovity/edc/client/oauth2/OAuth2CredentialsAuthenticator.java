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

import lombok.RequiredArgsConstructor;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * OkHttp Authenticator: Potentially re-tries requests that failed with a 401 / 403
 * with updated access tokens.
 */
@RequiredArgsConstructor
public class OAuth2CredentialsAuthenticator implements Authenticator {
    private final OAuth2CredentialsStore credentialsStore;

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NotNull Response response) {
        // Skip if original request had no authentication
        if (!OkHttpRequestUtils.hadBearerToken(response)) {
            return null;
        }

        var token = credentialsStore.getAccessToken();
        synchronized (this) {
            // The synchronized Block prevents multiple parallel token refreshes
            // So here the token might have changed already
            var changedToken = credentialsStore.getAccessToken();

            // If the token has changed since the request was made, use the new token.
            if (!changedToken.equals(token)) {
                return OkHttpRequestUtils.withBearerToken(response.request(), changedToken);
            }

            // If the token hasn't changed, try to be the code path to refresh the token
            var updatedToken = credentialsStore.refreshAccessToken();

            // Retry the request with the new token.
            return OkHttpRequestUtils.withBearerToken(response.request(), updatedToken);
        }
    }
}
