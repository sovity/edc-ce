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
