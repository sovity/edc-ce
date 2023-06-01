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


@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"priority", "connectorEndpoint"})
public class ThreadPoolTask implements Comparable<ThreadPoolTask>, Runnable {
    private final int priority;
    private final Runnable task;
    private final String connectorEndpoint;

    @Override
    public int compareTo(@NotNull ThreadPoolTask threadPoolTask) {
        return priority - threadPoolTask.priority;
    }

    @Override
    public void run() {
        this.task.run();
    }
}
