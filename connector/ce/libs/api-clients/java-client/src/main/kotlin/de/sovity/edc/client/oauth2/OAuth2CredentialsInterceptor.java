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
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * OkHttp Interceptor: Adds Bearer Token to requests
 */
@RequiredArgsConstructor
public class OAuth2CredentialsInterceptor implements Interceptor {
    private final OAuth2CredentialsStore credentialsStore;

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        String accessToken = credentialsStore.getAccessToken();
        Request request = OkHttpRequestUtils.withBearerToken(chain.request(), accessToken);
        return chain.proceed(request);
    }

}
