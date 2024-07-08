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

package de.sovity.edc.ext.catalog.crawler.crawling;

import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventLogger;
import de.sovity.edc.ext.catalog.crawler.dao.CatalogCleaner;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorQueries;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorStatusUpdater;
import de.sovity.edc.ext.catalog.crawler.orchestration.config.CrawlerConfig;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

@RequiredArgsConstructor
public class OfflineConnectorCleaner {
    private final CrawlerConfig crawlerConfig;
    private final ConnectorQueries connectorQueries;
    private final CrawlerEventLogger crawlerEventLogger;
    private final ConnectorStatusUpdater connectorStatusUpdater;
    private final CatalogCleaner catalogCleaner;

    public void cleanConnectorsIfOfflineTooLong(DSLContext dsl) {
        var killOfflineConnectorsAfter = crawlerConfig.getKillOfflineConnectorsAfter();
        var connectorsToKill = connectorQueries.findAllConnectorsForKilling(dsl, killOfflineConnectorsAfter);

        catalogCleaner.removeCatalogByConnectors(dsl, connectorsToKill);
        connectorStatusUpdater.markAsDead(dsl, connectorsToKill);

        crawlerEventLogger.addKilledDueToOfflineTooLongMessages(dsl, connectorsToKill);
    }
}
