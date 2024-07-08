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
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.CrawlerEventStatus;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.CrawlerEventType;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.records.CrawlerEventLogRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.UUID;

/**
 * Updates a single connector.
 */
@RequiredArgsConstructor
public class CrawlerEventLogger {

    public void logConnectorUpdated(DSLContext dsl, ConnectorRef connectorRef, ConnectorChangeTracker changes) {
        var logEntry = newLogEntry(dsl, connectorRef);
        logEntry.setEvent(CrawlerEventType.CONNECTOR_UPDATED);
        logEntry.setEventStatus(CrawlerEventStatus.OK);
        logEntry.setUserMessage(changes.toString());
        logEntry.insert();
    }

    public void logConnectorOffline(DSLContext dsl, ConnectorRef connectorRef, CrawlerEventErrorMessage errorMessage) {
        var logEntry = newLogEntry(dsl, connectorRef);
        logEntry.setEvent(CrawlerEventType.CONNECTOR_STATUS_CHANGE_OFFLINE);
        logEntry.setEventStatus(CrawlerEventStatus.ERROR);
        logEntry.setUserMessage("Connector is offline.");
        logEntry.setErrorStack(errorMessage.stackTraceOrNull());
        logEntry.insert();
    }

    public void logConnectorOnline(DSLContext dsl, ConnectorRef connectorRef) {
        var logEntry = newLogEntry(dsl, connectorRef);
        logEntry.setEvent(CrawlerEventType.CONNECTOR_STATUS_CHANGE_ONLINE);
        logEntry.setEventStatus(CrawlerEventStatus.OK);
        logEntry.setUserMessage("Connector is online.");
        logEntry.insert();
    }

    public void logConnectorUpdateDataOfferLimitExceeded(
            DSLContext dsl,
            ConnectorRef connectorRef,
            Integer maxDataOffersPerConnector
    ) {
        var logEntry = newLogEntry(dsl, connectorRef);
        logEntry.setEvent(CrawlerEventType.CONNECTOR_DATA_OFFER_LIMIT_EXCEEDED);
        logEntry.setEventStatus(CrawlerEventStatus.OK);
        logEntry.setUserMessage(
                "Connector has more than %d data offers. Exceeding data offers will be ignored.".formatted(maxDataOffersPerConnector));
        logEntry.insert();
    }

    public void logConnectorUpdateDataOfferLimitOk(DSLContext dsl, ConnectorRef connectorRef) {
        var logEntry = newLogEntry(dsl, connectorRef);
        logEntry.setEvent(CrawlerEventType.CONNECTOR_DATA_OFFER_LIMIT_OK);
        logEntry.setEventStatus(CrawlerEventStatus.OK);
        logEntry.setUserMessage("Connector is not exceeding the maximum number of data offers limit anymore.");
        logEntry.insert();
    }

    public void logConnectorUpdateContractOfferLimitExceeded(
            DSLContext dsl,
            ConnectorRef connectorRef,
            Integer maxContractOffersPerConnector
    ) {
        var logEntry = newLogEntry(dsl, connectorRef);
        logEntry.setEvent(CrawlerEventType.CONNECTOR_CONTRACT_OFFER_LIMIT_EXCEEDED);
        logEntry.setEventStatus(CrawlerEventStatus.OK);
        logEntry.setUserMessage(String.format(
                "Some data offers have more than %d contract offers. Exceeding contract offers will be ignored.: ",
                maxContractOffersPerConnector
        ));
        logEntry.insert();
    }

    public void logConnectorUpdateContractOfferLimitOk(DSLContext dsl, ConnectorRef connectorRef) {
        var logEntry = newLogEntry(dsl, connectorRef);
        logEntry.setEvent(CrawlerEventType.CONNECTOR_CONTRACT_OFFER_LIMIT_OK);
        logEntry.setEventStatus(CrawlerEventStatus.OK);
        logEntry.setUserMessage("Connector is not exceeding the maximum number of contract offers per data offer limit anymore.");
        logEntry.insert();
    }

    public void addKilledDueToOfflineTooLongMessages(DSLContext dsl, Collection<ConnectorRef> connectorRefs) {
        var logEntries = connectorRefs.stream()
                .map(connectorRef -> buildKilledDueToOfflineTooLongMessage(dsl, connectorRef))
                .toList();
        dsl.batchInsert(logEntries).execute();
    }

    private CrawlerEventLogRecord buildKilledDueToOfflineTooLongMessage(DSLContext dsl, ConnectorRef connectorRef) {
        var logEntry = newLogEntry(dsl, connectorRef);
        logEntry.setEvent(CrawlerEventType.CONNECTOR_KILLED_DUE_TO_OFFLINE_FOR_TOO_LONG);
        logEntry.setEventStatus(CrawlerEventStatus.OK);
        logEntry.setUserMessage("Connector was marked as dead for being offline too long.");
        return logEntry;
    }

    private CrawlerEventLogRecord newLogEntry(DSLContext dsl, ConnectorRef connectorRef) {
        var logEntry = dsl.newRecord(Tables.CRAWLER_EVENT_LOG);
        logEntry.setId(UUID.randomUUID());
        logEntry.setEnvironment(connectorRef.getEnvironmentId());
        logEntry.setConnectorId(connectorRef.getConnectorId());
        logEntry.setCreatedAt(OffsetDateTime.now());
        return logEntry;
    }
}
