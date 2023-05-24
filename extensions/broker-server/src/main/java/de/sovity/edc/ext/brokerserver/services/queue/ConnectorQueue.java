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

import de.sovity.edc.ext.brokerserver.services.ConnectorQueueEntry;

import java.util.concurrent.PriorityBlockingQueue;

public class ConnectorQueue {
    private final PriorityBlockingQueue<ConnectorQueueEntry> connectorQueueEntries = new PriorityBlockingQueue<>();

    public void add(ConnectorQueueEntry entry) {
        connectorQueueEntries.add(entry);
    }

    public ConnectorQueueEntry poll() {
        return connectorQueueEntries.poll();
    }
}
