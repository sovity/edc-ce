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

import de.sovity.edc.ext.brokerserver.client.gen.api.BrokerServerApi;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * API Client for our EDC API Wrapper.
 */
@Value
@Accessors(fluent = true)
public class EdcClient {
    BrokerServerApi brokerServerApi;

    public static EdcClientBuilder builder() {
        return new EdcClientBuilder();
    }
}
