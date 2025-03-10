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
package de.sovity.edc.client;

import de.sovity.edc.client.gen.ApiClient;
import de.sovity.edc.client.oauth2.OAuth2ClientCredentials;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;

import java.util.function.Consumer;

@Getter
@Setter
@Accessors(fluent = true, chain = true)
public class EdcClientBuilder {
    /**
     * Management API Base URL, e.g. https://my-connector.com/control/management
     */
    private String managementApiUrl;

    /**
     * Enables EDC Management API Key authentication.
     */
    private String managementApiKey = "ApiKeyDefaultValue";

    /**
     * Enables OAuth2 "Client Credentials Flow" authentication.
     */
    private OAuth2ClientCredentials oauth2ClientCredentials;

    /**
     * Custom configurer for the {@link ApiClient} and the {@link ApiClient#getHttpClient()}/{@link ApiClient#setHttpClient(OkHttpClient)}
     * for environments with custom authentication mechanisms.
     */
    private Consumer<ApiClient> customConfigurer;


    public EdcClient build() {
        return EdcClientFactory.newClient(this);
    }
}
