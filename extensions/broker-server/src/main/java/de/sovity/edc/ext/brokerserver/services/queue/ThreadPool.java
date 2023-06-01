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

import de.sovity.edc.ext.brokerserver.BrokerServerExtension;
import org.eclipse.edc.spi.system.configuration.Config;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ThreadPool {
    private final PriorityBlockingQueue<Runnable> queue;

    public ThreadPool(Config config) {
        this.queue = new PriorityBlockingQueue<>();
        int numThreads = config.getInteger(BrokerServerExtension.NUM_THREADS, 1);
        if (numThreads > 0) {
            var threadPoolExecutor = new ThreadPoolExecutor(1, numThreads, 60, TimeUnit.SECONDS, queue);
            threadPoolExecutor.prestartAllCoreThreads();
        }
    }

    public void execute(int priority, Runnable runnable, String endpoint) {
        queue.add(new ThreadPoolTask(priority, runnable, endpoint));
    }

    public Set<String> getQueuedConnectorEndpoints() {
        var queuedRunnables = new ArrayList<>(queue);

        return queuedRunnables.stream().filter(ThreadPoolTask.class::isInstance)
                .map(ThreadPoolTask.class::cast)
                .map(ThreadPoolTask::getConnectorEndpoint)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
