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
import de.sovity.edc.ext.brokerserver.services.BrokerEventLogger;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ConnectorUpdateSuccessWriter {
    private final BrokerEventLogger brokerEventLogger;

    public void handleConnectorOnline(
            DSLContext dsl,
            ConnectorRecord connector,
            ConnectorSelfDescription selfDescription,
            List<ContractOffer> contractOffers
    ) {
        // Track changes for final log message
        ConnectorChangeTracker changes = new ConnectorChangeTracker();
        updateConnector(connector, selfDescription, changes);

        // Update contract offers (if changed)
        // TODO

        // Log Event
        brokerEventLogger.logConnectorUpdateSuccess(connector.getEndpoint(), changes);
    }

    private static void updateConnector(ConnectorRecord connector, ConnectorSelfDescription selfDescription, ConnectorChangeTracker changes) {
        if (!Objects.equals(selfDescription.idsId(), connector.getIdsId())) {
            changes.addSelfDescriptionChange("IDS Connector ID");
            connector.setIdsId(selfDescription.idsId());
        }
        if (!Objects.equals(selfDescription.title(), connector.getTitle())) {
            changes.addSelfDescriptionChange("Title");
            connector.setIdsId(selfDescription.title());
        }
        if (!Objects.equals(selfDescription.description(), connector.getDescription())) {
            changes.addSelfDescriptionChange("Description");
            connector.setIdsId(selfDescription.description());
        }
        connector.setOnlineStatus(ConnectorOnlineStatus.ONLINE);
        connector.setLastUpdate(OffsetDateTime.now());
        connector.setOfflineSince(null);
        connector.update();
    }
}
