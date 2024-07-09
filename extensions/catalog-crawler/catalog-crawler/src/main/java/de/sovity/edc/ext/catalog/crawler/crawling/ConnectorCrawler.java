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

import de.sovity.edc.ext.catalog.crawler.crawling.fetching.FetchedCatalogService;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerExecutionTimeLogger;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.ConnectorUpdateFailureWriter;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.ConnectorUpdateSuccessWriter;
import de.sovity.edc.ext.catalog.crawler.dao.config.DslContextFactory;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorQueries;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.MeasurementErrorStatus;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.StopWatch;
import org.eclipse.edc.spi.monitor.Monitor;

import java.util.concurrent.TimeUnit;

/**
 * Updates a single connector.
 */
@RequiredArgsConstructor
public class ConnectorCrawler {
    private final FetchedCatalogService fetchedCatalogService;
    private final ConnectorUpdateSuccessWriter connectorUpdateSuccessWriter;
    private final ConnectorUpdateFailureWriter connectorUpdateFailureWriter;
    private final ConnectorQueries connectorQueries;
    private final DslContextFactory dslContextFactory;
    private final Monitor monitor;
    private final CrawlerExecutionTimeLogger crawlerExecutionTimeLogger;

    /**
     * Updates single connector.
     *
     * @param connectorRef connector
     */
    public void crawlConnector(ConnectorRef connectorRef) {
        var executionTime = StopWatch.createStarted();
        var failed = false;

        try {
            monitor.info("Updating connector: " + connectorRef);

            var catalog = fetchedCatalogService.fetchCatalog(connectorRef);

            // Update connector in a single transaction
            dslContextFactory.transaction(dsl -> {
                var connectorRecord = connectorQueries.findByConnectorId(dsl, connectorRef.getConnectorId());
                connectorUpdateSuccessWriter.handleConnectorOnline(dsl, connectorRef, connectorRecord, catalog);
            });
        } catch (Exception e) {
            failed = true;
            try {
                // Update connector in a single transaction
                dslContextFactory.transaction(dsl -> {
                    var connectorRecord = connectorQueries.findByConnectorId(dsl, connectorRef.getConnectorId());
                    connectorUpdateFailureWriter.handleConnectorOffline(dsl, connectorRef, connectorRecord, e);
                });
            } catch (Exception e1) {
                e1.addSuppressed(e);
                monitor.severe("Failed updating connector as failed.", e1);
            }
        } finally {
            executionTime.stop();
            try {
                var status = failed ? MeasurementErrorStatus.ERROR : MeasurementErrorStatus.OK;
                dslContextFactory.transaction(dsl -> crawlerExecutionTimeLogger.logExecutionTime(
                        dsl,
                        connectorRef,
                        executionTime.getTime(TimeUnit.MILLISECONDS),
                        status
                ));
            } catch (Exception e) {
                monitor.severe("Failed logging connector update execution time.", e);
            }
        }
    }
}
