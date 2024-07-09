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

package de.sovity.edc.ext.catalog.crawler.crawling.writing;

import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedCatalog;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.ConnectorChangeTracker;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventLogger;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.records.ConnectorRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
public class ConnectorUpdateSuccessWriter {
    private final CrawlerEventLogger crawlerEventLogger;
    private final ConnectorUpdateCatalogWriter connectorUpdateCatalogWriter;
    private final DataOfferLimitsEnforcer dataOfferLimitsEnforcer;

    public void handleConnectorOnline(
            DSLContext dsl,
            ConnectorRef connectorRef,
            ConnectorRecord connector,
            FetchedCatalog catalog
    ) {
        // Limit data offers and log limitation if necessary
        var limitedDataOffers = dataOfferLimitsEnforcer.enforceLimits(catalog.getDataOffers());
        dataOfferLimitsEnforcer.logEnforcedLimitsIfChanged(dsl, connectorRef, connector, limitedDataOffers);

        // Log Status Change and set status to online if necessary
        if (connector.getOnlineStatus() != ConnectorOnlineStatus.ONLINE || connector.getLastRefreshAttemptAt() == null) {
            crawlerEventLogger.logConnectorOnline(dsl, connectorRef);
            connector.setOnlineStatus(ConnectorOnlineStatus.ONLINE);
        }

        // Track changes for final log message
        var changes = new ConnectorChangeTracker();
        var now = OffsetDateTime.now();
        connector.setLastSuccessfulRefreshAt(now);
        connector.setLastRefreshAttemptAt(now);
        connector.update();

        // Update data offers
        connectorUpdateCatalogWriter.updateDataOffers(
                dsl,
                connectorRef,
                limitedDataOffers.abbreviatedDataOffers(),
                changes
        );

        // Log event if changes are present
        if (!changes.isEmpty()) {
            crawlerEventLogger.logConnectorUpdated(dsl, connectorRef, changes);
        }
    }

}
