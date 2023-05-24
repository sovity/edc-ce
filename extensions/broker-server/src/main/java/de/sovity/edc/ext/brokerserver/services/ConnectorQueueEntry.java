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

import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.Comparator;

public record ConnectorQueueEntry(String endpoint,
                                  OffsetDateTime lastUpdate,
                                  int priority) implements Comparable<ConnectorQueueEntry> {
    private static final Comparator<ConnectorQueueEntry> COMPARATOR = Comparator
            .comparing(ConnectorQueueEntry::priority)
            .thenComparing(ConnectorQueueEntry::lastUpdate);

    @Override
    public int compareTo(@NotNull ConnectorQueueEntry connectorQueueEntry) {
        return COMPARATOR.compare(this, connectorQueueEntry);
    }
}
