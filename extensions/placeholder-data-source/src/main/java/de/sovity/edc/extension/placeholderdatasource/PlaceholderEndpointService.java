/*
 * Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.extension.placeholderdatasource;

import lombok.RequiredArgsConstructor;
import okhttp3.HttpUrl;

@RequiredArgsConstructor
public class PlaceholderEndpointService {

    private final String baseUrl;

    public static final String DUMMY_ENDPOINT_URL = "/data-source/placeholder/asset";

    public String getPlaceholderEndpointForAsset(String email, String subject) {
        return HttpUrl.parse(baseUrl + DUMMY_ENDPOINT_URL)
            .newBuilder()
            .addQueryParameter("email", email)
            .addQueryParameter("subject", subject)
            .build()
            .toString();
    }
}
