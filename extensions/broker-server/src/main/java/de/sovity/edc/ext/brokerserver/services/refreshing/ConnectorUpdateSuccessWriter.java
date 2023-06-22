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
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferLimitsEnforcer;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferWriter;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedDataOffer;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.Collection;

@RequiredArgsConstructor
public class ConnectorUpdateSuccessWriter {
    private final BrokerEventLogger brokerEventLogger;
    private final DataOfferWriter dataOfferWriter;
    private final DataOfferLimitsEnforcer dataOfferLimitsEnforcer;

    public void handleConnectorOnline(
            DSLContext dsl,
            ConnectorRecord connector,
            Collection<FetchedDataOffer> dataOffers
    ) {
        var now = OffsetDateTime.now();

        // Limit data offers and log limitation if necessary
        var limitedDataOffers = dataOfferLimitsEnforcer.enforceLimits(dataOffers);
        dataOfferLimitsEnforcer.logEnforcedLimitsIfChanged(connector, limitedDataOffers);

        // Log Status Change and set status to online if necessary
        if (connector.getOnlineStatus() == ConnectorOnlineStatus.OFFLINE || connector.getLastRefreshAttemptAt() == null) {
            brokerEventLogger.logConnectorOnline(dsl, connector.getEndpoint());
            connector.setOnlineStatus(ConnectorOnlineStatus.ONLINE);
        }

        // Track changes for final log message
        var changes = new ConnectorChangeTracker();
        connector.setLastSuccessfulRefreshAt(now);
        connector.setLastRefreshAttemptAt(now);
        connector.update();

        // Log Event if changes are present
        if (!changes.isEmpty()) {
            brokerEventLogger.logConnectorUpdated(dsl, connector.getEndpoint(), changes);
        }

        // Update data offers
        dataOfferWriter.updateDataOffers(dsl, connector.getEndpoint(), limitedDataOffers.abbreviatedDataOffers(), changes);
    }
}
