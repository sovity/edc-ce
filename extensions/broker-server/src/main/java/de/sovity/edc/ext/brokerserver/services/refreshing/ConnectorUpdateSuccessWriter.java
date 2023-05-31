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
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.Collection;

@RequiredArgsConstructor
public class ConnectorUpdateSuccessWriter {
    private final BrokerEventLogger brokerEventLogger;
    private final DataOfferWriter dataOfferWriter;

    public void handleConnectorOnline(
            DSLContext dsl,
            ConnectorRecord connector,
            Collection<FetchedDataOffer> dataOffers
    ) {
        OffsetDateTime now = OffsetDateTime.now();

        // Track changes for final log message
        ConnectorChangeTracker changes = new ConnectorChangeTracker();
        connector.setOnlineStatus(ConnectorOnlineStatus.ONLINE);
        connector.setLastSuccessfulRefreshAt(now);
        connector.setLastRefreshAttemptAt(now);
        connector.update();

        // Update data offers
        dataOfferWriter.updateDataOffers(dsl, connector.getEndpoint(), dataOffers);

        // Log Event
        brokerEventLogger.logConnectorUpdateSuccess(dsl, connector.getEndpoint(), changes);
    }

}
