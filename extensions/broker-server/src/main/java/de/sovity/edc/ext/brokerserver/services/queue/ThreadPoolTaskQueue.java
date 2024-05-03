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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ThreadPoolTaskQueue {

    @Getter
    private final PriorityBlockingQueue<ThreadPoolTask> queue = new PriorityBlockingQueue<>(50, ThreadPoolTask.COMPARATOR);

    @SuppressWarnings("unchecked")
    public PriorityBlockingQueue<Runnable> getAsRunnableQueue() {
        return (PriorityBlockingQueue<Runnable>) (PriorityBlockingQueue<? extends Runnable>) queue;
    }

    public void add(ThreadPoolTask task) {
        queue.add(task);
    }

    public Set<String> getConnectorEndpoints() {
        var queuedRunnables = new ArrayList<>(queue);

        return queuedRunnables.stream()
                .map(ThreadPoolTask::getConnectorEndpoint)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
