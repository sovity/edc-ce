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
        var logEntry = connectorUpdateEntry(dsl, connectorEndpoint);
        logEntry.setEventStatus(getConnectorUpdateStatus(changes));
        logEntry.setUserMessage(changes.toString());
        logEntry.insert();
    }

    public void logConnectorUpdateFailure(DSLContext dsl, String connectorEndpoint, BrokerEventErrorMessage errorMessage) {
        var logEntry = connectorUpdateEntry(dsl, connectorEndpoint);
        logEntry.setEventStatus(BrokerEventStatus.ERROR);
        logEntry.setUserMessage(errorMessage.message());
        logEntry.setErrorStack(errorMessage.stackTraceOrNull());
        logEntry.insert();
    }

    public void logConnectorUpdateStatusChange(DSLContext dsl, String connectorEndpoint, ConnectorOnlineStatus status) {
        var logEntry = connectorUpdateEntry(dsl, connectorEndpoint);
        switch (status) {
            case ONLINE:
                logEntry.setUserMessage("Connector is online: " + connectorEndpoint);
                logEntry.setEvent(BrokerEventType.CONNECTOR_STATUS_CHANGE_ONLINE);
                break;
            case OFFLINE:
                logEntry.setUserMessage("Connector is offline: " + connectorEndpoint);
                logEntry.setEvent(BrokerEventType.CONNECTOR_STATUS_CHANGE_OFFLINE);
                break;
            default:
                throw new IllegalArgumentException("Unknown status: " + status + " for connector: " + connectorEndpoint);
        }
        logEntry.insert();
    }

    private BrokerEventLogRecord connectorUpdateEntry(DSLContext dsl, String connectorEndpoint) {
        var logEntry = dsl.newRecord(Tables.BROKER_EVENT_LOG);
        logEntry.setEvent(BrokerEventType.CONNECTOR_UPDATED);
        logEntry.setConnectorEndpoint(connectorEndpoint);
        logEntry.setCreatedAt(OffsetDateTime.now());
        return logEntry;
    }

    private BrokerEventStatus getConnectorUpdateStatus(ConnectorChangeTracker changes) {
        if (changes.isEmpty()) {
            return BrokerEventStatus.UNCHANGED;
        }

        return BrokerEventStatus.OK;
    }
}
