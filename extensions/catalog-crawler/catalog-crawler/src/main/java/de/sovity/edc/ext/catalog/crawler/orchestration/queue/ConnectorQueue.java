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

package de.sovity.edc.ext.catalog.crawler.orchestration.queue;

import de.sovity.edc.ext.catalog.crawler.crawling.ConnectorCrawler;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class ConnectorQueue {
    private final ConnectorCrawler connectorCrawler;
    private final ThreadPool threadPool;

    /**
     * Enqueues connectors for update.
     *
     * @param connectorRefs connectors
     * @param priority priority from {@link ConnectorRefreshPriority}
     */
    public void addAll(Collection<ConnectorRef> connectorRefs, int priority) {
        var queued = threadPool.getQueuedConnectorRefs();
        connectorRefs = new ArrayList<>(connectorRefs);
        connectorRefs.removeIf(queued::contains);

        for (var connectorRef : connectorRefs) {
            threadPool.enqueueConnectorRefreshTask(
                    priority,
                    () -> connectorCrawler.crawlConnector(connectorRef),
                    connectorRef
            );
        }
    }
}
