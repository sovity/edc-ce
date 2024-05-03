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

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true, chain = true)
public class BrokerServerClientBuilder {
    /**
     * Management API Base URL, e.g. https://my-broker.com/backend/management
     */
    private String managementApiUrl;

    /**
     * Enables EDC Management API Key authentication.
     */
    private String managementApiKey = "ApiKeyDefaultValue";

    public BrokerServerClient build() {
        return BrokerServerClientFactory.newClient(this);
    }
}
