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

import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.ConnectorRecord;
import de.sovity.edc.ext.brokerserver.services.logging.BrokerEventLogger;
import de.sovity.edc.ext.brokerserver.services.logging.ConnectorChangeTracker;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferWriter;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedDataOffer;
import de.sovity.edc.ext.brokerserver.services.refreshing.selfdescription.ConnectorSelfDescription;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Objects;

@RequiredArgsConstructor
public class ConnectorUpdateSuccessWriter {
    private final BrokerEventLogger brokerEventLogger;
    private final DataOfferWriter dataOfferWriter;

    public void handleConnectorOnline(
            DSLContext dsl,
            ConnectorRecord connector,
            ConnectorSelfDescription selfDescription,
            Collection<FetchedDataOffer> dataOffers
    ) {
        // Track changes for final log message
        ConnectorChangeTracker changes = new ConnectorChangeTracker();
        updateConnector(connector, selfDescription, changes);

        // Update data offers
        dataOfferWriter.updateDataOffers(dsl, connector.getEndpoint(), dataOffers);

        // Log Event
        brokerEventLogger.logConnectorUpdateSuccess(dsl, connector.getEndpoint(), changes);
    }

    private static void updateConnector(ConnectorRecord connector, ConnectorSelfDescription selfDescription, ConnectorChangeTracker changes) {
        if (!Objects.equals(selfDescription.title(), connector.getTitle())) {
            changes.addSelfDescriptionChange("Title");
            connector.setTitle(selfDescription.title());
        }
        if (!Objects.equals(selfDescription.description(), connector.getDescription())) {
            changes.addSelfDescriptionChange("Description");
            connector.setDescription(selfDescription.description());
        }
        connector.setOnlineStatus(ConnectorOnlineStatus.ONLINE);
        connector.setLastUpdate(OffsetDateTime.now());
        connector.setOfflineSince(null);
        connector.update();
    }
}
