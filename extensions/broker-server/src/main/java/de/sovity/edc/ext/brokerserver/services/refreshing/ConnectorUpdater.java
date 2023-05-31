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

import de.sovity.edc.ext.brokerserver.dao.queries.ConnectorQueries;
import de.sovity.edc.ext.brokerserver.db.DslContextFactory;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.ConnectorRecord;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferFetcher;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.monitor.Monitor;

/**
 * Updates a single connector.
 */
@RequiredArgsConstructor
public class ConnectorUpdater {
    private final DataOfferFetcher dataOfferFetcher;
    private final ConnectorUpdateSuccessWriter connectorUpdateSuccessWriter;
    private final ConnectorUpdateFailureWriter connectorUpdateFailureWriter;
    private final ConnectorQueries connectorQueries;
    private final DslContextFactory dslContextFactory;
    private final Monitor monitor;

    /**
     * Updates single connector.
     *
     * @param connectorEndpoint connector endpoint
     */
    public void updateConnector(String connectorEndpoint) {
        try {
            monitor.info("Updating connector: " + connectorEndpoint);
            var dataOffers = dataOfferFetcher.fetch(connectorEndpoint);

            // Update connector in a single transaction
            dslContextFactory.transaction(dsl -> {
                ConnectorRecord connectorRecord = connectorQueries.findByEndpoint(dsl, connectorEndpoint);
                connectorUpdateSuccessWriter.handleConnectorOnline(dsl, connectorRecord, dataOffers);
            });
        } catch (Exception e) {
            try {
                // Update connector in a single transaction
                dslContextFactory.transaction(dsl -> {
                    ConnectorRecord connectorRecord = connectorQueries.findByEndpoint(dsl, connectorEndpoint);
                    connectorUpdateFailureWriter.handleConnectorOffline(dsl, connectorRecord, e);
                });
            } catch (Exception e1) {
                e1.addSuppressed(e);
                monitor.severe("Failed updating connector as failed.", e1);
            }
        }
    }
}
