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
 * The table {@link LogEventRecord} table contains many types of events or task outcomes.
 * This enum is used to distinguish between them.
 */
public enum LogEventType {
    /**
     * Connector was successfully updated, and changes were incorporated
     */
    CONNECTOR_UPDATED,

    /**
     * Connector went online
     */
    CONNECTOR_STATUS_CHANGE_ONLINE,

    /**
     * Connector went offline
     */
    CONNECTOR_STATUS_CHANGE_OFFLINE,

    /**
     * Connector was "force deleted"
     */
    CONNECTOR_STATUS_CHANGE_FORCE_DELETED,

    /**
     * Contract Offer was updated
     */
    CONTRACT_OFFER_UPDATED,

    /**
     * Contract Offer was clicked
     */
    CONTRACT_OFFER_CLICK;
}
