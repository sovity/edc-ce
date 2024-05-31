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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import okhttp3.Request;
import okhttp3.Response;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OkHttpRequestUtils {
    public static boolean hadBearerToken(@NonNull Response response) {
        String header = response.request().header("Authorization");
        return header != null && header.startsWith("Bearer");
    }

    @NonNull
    public static Request withBearerToken(@NonNull Request request, @NonNull String accessToken) {
        return request.newBuilder()
                .removeHeader("Authorization")
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }
}

