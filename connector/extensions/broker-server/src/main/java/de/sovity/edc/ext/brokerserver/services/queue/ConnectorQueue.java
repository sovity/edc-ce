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

import de.sovity.edc.ext.brokerserver.services.refreshing.ConnectorUpdater;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class ConnectorQueue {
    private final ConnectorUpdater connectorUpdater;
    private final ThreadPool threadPool;

    /**
     * Enqueues connectors for update.
     *
     * @param endpoints connector endpoints
     * @param priority  priority from {@link ConnectorRefreshPriority}
     */
    public void addAll(Collection<String> endpoints, int priority) {
        var queuedConnectorEndpoints = threadPool.getQueuedConnectorEndpoints();
        endpoints = new ArrayList<>(endpoints);
        endpoints.removeIf(queuedConnectorEndpoints::contains);

        for (String endpoint : endpoints) {
            threadPool.enqueueConnectorRefreshTask(priority, () -> connectorUpdater.updateConnector(endpoint), endpoint);
        }
    }
}
