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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;


@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(of = "endpoint", callSuper = false)
public class ConnectorQueueEntry implements Comparable<ConnectorQueueEntry> {
    private static final Comparator<ConnectorQueueEntry> COMPARATOR = Comparator
            .comparing(ConnectorQueueEntry::getPriority);

    @NotNull
    private final String endpoint;
    private final int priority;

    @Override
    public int compareTo(@NotNull ConnectorQueueEntry connectorQueueEntry) {
        return COMPARATOR.compare(this, connectorQueueEntry);
    }
}
