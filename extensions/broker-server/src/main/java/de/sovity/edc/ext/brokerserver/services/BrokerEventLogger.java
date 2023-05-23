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

package de.sovity.edc.ext.brokerserver.services;

import de.sovity.edc.ext.brokerserver.dao.models.LogEventRecord;
import de.sovity.edc.ext.brokerserver.dao.models.LogEventStatus;
import de.sovity.edc.ext.brokerserver.dao.models.LogEventType;
import de.sovity.edc.ext.brokerserver.dao.stores.LogEventStore;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.ConnectorRecord;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Updates a single connector.
 */
@RequiredArgsConstructor
public class BrokerEventLogger {
    private final LogEventStore logEventStore;

    public void logConnectorUpdate(ConnectorRecord connector, LogEventStatus outcome) {
        var logEntry = LogEventRecord.builder()
                .connectorEndpoint(connector.getEndpoint())
                .userMessage(getConnectorUpdatedMessage(outcome))
                .type(LogEventType.CONNECTOR_UPDATED)
                .createdAt(OffsetDateTime.now())
                .status(outcome)
                .build();
        this.logEventStore.save(logEntry);
    }

    private static String getConnectorUpdatedMessage(LogEventStatus outcome) {
        return switch (outcome) {
            case UNCHANGED -> "Connector is up to date";
            case UPDATED -> "Connector was updated";
            case ERROR -> "Connector update failed";
            default -> throw new IllegalArgumentException("Unknown outcome: " + outcome);
        };
    }
}
