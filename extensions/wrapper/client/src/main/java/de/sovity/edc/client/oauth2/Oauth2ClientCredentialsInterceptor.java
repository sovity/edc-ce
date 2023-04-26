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
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@RequiredArgsConstructor
public class Oauth2ClientCredentialsInterceptor implements Interceptor {
    private final Oauth2ClientCredentialsHandler oauth2ClientCredentialsHandler;

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        String accessToken = oauth2ClientCredentialsHandler.getAccessToken();
        Request request = OkHttpRequestUtils.withBearerToken(chain.request(), accessToken);
        return chain.proceed(request);
    }

}
