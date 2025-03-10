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
