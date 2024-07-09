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

package de.sovity.edc.ext.catalog.crawler.crawling.logging;

import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.db.jooq.Tables;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.MeasurementErrorStatus;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.MeasurementType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Updates a single connector.
 */
@RequiredArgsConstructor
public class CrawlerExecutionTimeLogger {
    public void logExecutionTime(DSLContext dsl, ConnectorRef connectorRef, long executionTimeInMs, MeasurementErrorStatus errorStatus) {
        var logEntry = dsl.newRecord(Tables.CRAWLER_EXECUTION_TIME_MEASUREMENT);
        logEntry.setId(UUID.randomUUID());
        logEntry.setEnvironment(connectorRef.getEnvironmentId());
        logEntry.setConnectorId(connectorRef.getConnectorId());
        logEntry.setDurationInMs(executionTimeInMs);
        logEntry.setType(MeasurementType.CONNECTOR_REFRESH);
        logEntry.setErrorStatus(errorStatus);
        logEntry.setCreatedAt(OffsetDateTime.now());
        logEntry.insert();
    }
}
