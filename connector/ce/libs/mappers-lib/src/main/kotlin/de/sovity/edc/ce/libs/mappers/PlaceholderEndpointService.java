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
package de.sovity.edc.ce.libs.mappers;

import de.sovity.edc.runtime.config.ConfigUtils;
import de.sovity.edc.runtime.config.UrlPathUtils;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import okhttp3.HttpUrl;

@RequiredArgsConstructor
@Service
public class PlaceholderEndpointService {

    private final ConfigUtils configUtils;

    public static final String DUMMY_ENDPOINT_URL = "/on-demand-asset-data-source/info-message";

    public String getPlaceholderEndpointForAsset(String email, String subject) {
        return HttpUrl.parse(UrlPathUtils.urlPathJoin(configUtils.getProtocolApiUrl(), DUMMY_ENDPOINT_URL))
            .newBuilder()
            .addQueryParameter("email", email)
            .addQueryParameter("subject", subject)
            .build()
            .toString();
    }
}
