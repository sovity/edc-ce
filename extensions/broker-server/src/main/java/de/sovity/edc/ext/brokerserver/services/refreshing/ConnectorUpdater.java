/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.ext.brokerserver.services.refreshing;

import de.sovity.edc.ext.brokerserver.services.BrokerEventLogger;
import lombok.RequiredArgsConstructor;

/**
 * Updates a single connector.
 */
@RequiredArgsConstructor
public class ConnectorUpdater {
    private final ConnectorSelfDescriptionFetcher connectorSelfDescriptionFetcher;
    private final ContractOfferFetcher contractOfferFetcher;
    private final BrokerEventLogger brokerEventLogger;

    /**
     * Updates single connector.
     *
     * @param connectorEndpoint connector endpoint
     */
    public void updateConnector(String connectorEndpoint) {
        // TODO implement
        throw new IllegalArgumentException("Not yet implemented");
    }
}
