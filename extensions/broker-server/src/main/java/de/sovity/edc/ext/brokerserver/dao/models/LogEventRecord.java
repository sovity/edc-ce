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

package de.sovity.edc.ext.brokerserver.dao.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;

/**
 * Log Event Database Row that can be inserted or updated.
 * <p>
 * Many kinds of events or tasks might log into this table.
 * Logging of execution times is also supported.
 */
@Getter
@ToString
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LogEventRecord {
    /**
     * Row ID
     */
    String id;

    /**
     * Log Message Date
     */
    OffsetDateTime createdAt;

    /**
     * Log Entry Type
     */
    LogEventType type;

    /**
     * Log Entry Type
     */
    LogEventStatus status;

    /**
     * Connector reference, if applicable
     */
    String connectorEndpoint;

    /**
     * Contract Offer reference, if applicable
     */
    String contractOfferId;

    /**
     * Message to be shown in UI, if applicable
     */
    String userMessage;

    /**
     * Execution time in milliseconds, if recorded / applicable
     */
    Long executionTimeInMs;
}
