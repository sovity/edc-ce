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

import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventErrorMessage;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventLogger;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.records.ConnectorRecord;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.monitor.Monitor;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
public class ConnectorUpdateFailureWriter {
    private final CrawlerEventLogger crawlerEventLogger;
    private final Monitor monitor;

    public void handleConnectorOffline(
            DSLContext dsl,
            ConnectorRef connectorRef,
            ConnectorRecord connector,
            Throwable e
    ) {
        // Log Status Change and set status to offline if necessary
        if (connector.getOnlineStatus() == ConnectorOnlineStatus.ONLINE || connector.getLastRefreshAttemptAt() == null) {
            monitor.info("Connector is offline: " + connector.getEndpointUrl(), e);
            crawlerEventLogger.logConnectorOffline(dsl, connectorRef, getFailureMessage(e));
            connector.setOnlineStatus(ConnectorOnlineStatus.OFFLINE);
        }

        connector.setLastRefreshAttemptAt(OffsetDateTime.now());
        connector.update();
    }

    public CrawlerEventErrorMessage getFailureMessage(Throwable e) {
        return CrawlerEventErrorMessage.ofStackTrace("Unexpected exception during connector update.", e);
    }
}
