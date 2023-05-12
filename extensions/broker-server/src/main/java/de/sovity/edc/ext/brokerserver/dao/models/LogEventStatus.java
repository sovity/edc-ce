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

/**
 * This enum allows us to differentiate errors and changeful events.
 */
public enum LogEventStatus {
    /**
     * Means that something failed.
     */
    ERROR,

    /**
     * Means that these log messages can basically be skipped.
     */
    UNCHANGED,

    /**
     * A log message that might interest the user (compared to log messages kept to calculate execution times)
     */
    UPDATED;
}
