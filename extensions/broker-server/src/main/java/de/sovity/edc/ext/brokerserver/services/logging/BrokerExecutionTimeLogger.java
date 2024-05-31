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
import de.sovity.edc.ext.brokerserver.db.jooq.enums.MeasurementErrorStatus;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.MeasurementType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;

/**
 * Updates a single connector.
 */
@RequiredArgsConstructor
public class BrokerExecutionTimeLogger {
    public void logExecutionTime(DSLContext dsl, String connectorEndpoint, long executionTimeInMs, MeasurementErrorStatus errorStatus) {
        var logEntry = dsl.newRecord(Tables.BROKER_EXECUTION_TIME_MEASUREMENT);
        logEntry.setConnectorEndpoint(connectorEndpoint);
        logEntry.setDurationInMs(executionTimeInMs);
        logEntry.setType(MeasurementType.CONNECTOR_REFRESH);
        logEntry.setErrorStatus(errorStatus);
        logEntry.setConnectorEndpoint(connectorEndpoint);
        logEntry.setCreatedAt(OffsetDateTime.now());
        logEntry.insert();
    }
}
