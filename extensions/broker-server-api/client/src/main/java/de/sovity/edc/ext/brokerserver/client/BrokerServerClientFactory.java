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

package de.sovity.edc.ext.brokerserver.client;

import de.sovity.edc.ext.brokerserver.client.gen.ApiClient;
import de.sovity.edc.ext.brokerserver.client.gen.api.BrokerServerApi;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * Builds {@link BrokerServerClient}s.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrokerServerClientFactory {

    public static BrokerServerClient newClient(BrokerServerClientBuilder builder) {
        var apiClient = new ApiClient()
                .setServerIndex(null)
                .setBasePath(builder.managementApiUrl());

        if (StringUtils.isNotBlank(builder.managementApiKey())) {
            apiClient.addDefaultHeader("x-api-key", builder.managementApiKey());
        }

        return new BrokerServerClient(
                new BrokerServerApi(apiClient)
        );
    }
}
