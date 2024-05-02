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

package de.sovity.edc.ext.brokerserver.services.queue;

import de.sovity.edc.ext.brokerserver.dao.ConnectorQueries;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

@RequiredArgsConstructor
public class ConnectorQueueFiller {
    private final ConnectorQueue connectorQueue;
    private final ConnectorQueries connectorQueries;

    public void enqueueConnectors(DSLContext dsl, ConnectorOnlineStatus status, int priority) {
        var endpoints = connectorQueries.findConnectorsForScheduledRefresh(dsl, status);
        connectorQueue.addAll(endpoints, priority);
    }
}
