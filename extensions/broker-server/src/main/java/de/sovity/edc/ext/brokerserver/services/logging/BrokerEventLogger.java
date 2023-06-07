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

package de.sovity.edc.ext.brokerserver.services.logging;

import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.BrokerEventStatus;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.BrokerEventType;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.BrokerEventLogRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;

/**
 * Updates a single connector.
 */
@RequiredArgsConstructor
public class BrokerEventLogger {

    public void logConnectorUpdateSuccess(DSLContext dsl, String connectorEndpoint, ConnectorChangeTracker changes) {
        var logEntry = logEntry(
                dsl,
                BrokerEventType.CONNECTOR_UPDATED,
                connectorEndpoint,
                changes.toString()
        );
        logEntry.insert();
    }

    public void logConnectorUpdateFailure(DSLContext dsl, String connectorEndpoint, BrokerEventErrorMessage errorMessage) {
        var logEntry = logEntry(
                dsl,
                BrokerEventType.CONNECTOR_UPDATED,
                connectorEndpoint,
                errorMessage.message()
        );
        logEntry.setEventStatus(BrokerEventStatus.ERROR);
        logEntry.setErrorStack(errorMessage.stackTraceOrNull());
        logEntry.insert();
    }

    public void logConnectorUpdateStatusChange(DSLContext dsl, String connectorEndpoint, ConnectorOnlineStatus status) {
        var logEntry = switch (status) {
            case ONLINE -> logEntry(
                    dsl,
                    BrokerEventType.CONNECTOR_STATUS_CHANGE_ONLINE,
                    connectorEndpoint,
                    "Connector is online: " + connectorEndpoint
            );
            case OFFLINE -> logEntry(
                    dsl,
                    BrokerEventType.CONNECTOR_STATUS_CHANGE_OFFLINE,
                    connectorEndpoint,
                    "Connector is offline: " + connectorEndpoint
            );
            default -> throw new IllegalArgumentException("Unknown status: " + status + " for connector: " + connectorEndpoint);
        };
        logEntry.insert();
    }

    public void logConnectorUpdateDataOfferLimitExceeded(Integer maxDataOffersPerConnector, String endpoint) {
        var logEntry = new BrokerEventLogRecord();
        logEntry.setEvent(BrokerEventType.CONNECTOR_DATA_OFFER_LIMIT_EXCEEDED);
        logEntry.setEventStatus(BrokerEventStatus.OK);
        logEntry.setConnectorEndpoint(endpoint);
        logEntry.setUserMessage("Connector has exceeded the maximum number of data offers: " + maxDataOffersPerConnector);
        logEntry.setCreatedAt(OffsetDateTime.now());
        logEntry.insert();
    }

    public void logConnectorUpdateDataOfferLimitOk(Integer maxDataOffersPerConnector, String endpoint) {
        var logEntry = new BrokerEventLogRecord();
        logEntry.setEvent(BrokerEventType.CONNECTOR_DATA_OFFER_LIMIT_OK);
        logEntry.setEventStatus(BrokerEventStatus.OK);
        logEntry.setConnectorEndpoint(endpoint);
        logEntry.setUserMessage("Connector is not exceeding maximum number of data offers limits anymore: " + maxDataOffersPerConnector);
        logEntry.setCreatedAt(OffsetDateTime.now());
        logEntry.insert();
    }

    public void logConnectorUpdateContractOfferLimitExceeded(Integer maxContractOffersPerConnector, String endpoint) {
        var logEntry = new BrokerEventLogRecord();
        logEntry.setEvent(BrokerEventType.CONNECTOR_CONTRACT_OFFER_LIMIT_EXCEEDED);
        logEntry.setEventStatus(BrokerEventStatus.OK);
        logEntry.setConnectorEndpoint(endpoint);
        logEntry.setUserMessage("Connector has exceeded maximum number of contract offers per data offer limit: " + maxContractOffersPerConnector);
        logEntry.setCreatedAt(OffsetDateTime.now());
        logEntry.insert();
    }

    public void logConnectorUpdateContractOfferLimitOk(Integer maxContractOffersPerConnector, String endpoint) {
        var logEntry = new BrokerEventLogRecord();
        logEntry.setEvent(BrokerEventType.CONNECTOR_CONTRACT_OFFER_LIMIT_OK);
        logEntry.setEventStatus(BrokerEventStatus.OK);
        logEntry.setConnectorEndpoint(endpoint);
        logEntry.setUserMessage("Connector is not exceeding maximum number of contract offers per data offer limits anymore: " + maxContractOffersPerConnector);
        logEntry.setCreatedAt(OffsetDateTime.now());
        logEntry.insert();
    }

    private BrokerEventLogRecord logEntry(DSLContext dsl, BrokerEventType eventType, String connectorEndpoint, String userMessage) {
        var logEntry = dsl.newRecord(Tables.BROKER_EVENT_LOG);
        logEntry.setEventStatus(BrokerEventStatus.OK);
        logEntry.setEvent(eventType);
        logEntry.setConnectorEndpoint(connectorEndpoint);
        logEntry.setCreatedAt(OffsetDateTime.now());
        logEntry.setUserMessage(userMessage);
        return logEntry;
    }
}
