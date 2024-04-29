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

import de.sovity.edc.ext.brokerserver.services.config.BrokerServerSettings;
import org.eclipse.edc.spi.monitor.Monitor;

import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    private final ThreadPoolTaskQueue queue;

    private final boolean enabled;
    private final ThreadPoolExecutor threadPoolExecutor;

    public ThreadPool(ThreadPoolTaskQueue queue, BrokerServerSettings brokerServerSettings, Monitor monitor) {
        this.queue = queue;
        int numThreads = brokerServerSettings.getNumThreads();
        enabled = numThreads > 0;

        if (enabled) {
            monitor.info("Initializing ThreadPoolExecutor with %d threads.".formatted(numThreads));
            threadPoolExecutor = new ThreadPoolExecutor(
                    numThreads,
                    numThreads,
                    60,
                    TimeUnit.SECONDS,
                    queue.getAsRunnableQueue()
            );
            threadPoolExecutor.prestartAllCoreThreads();
        } else {
            monitor.info("Skipped ThreadPoolExecutor initialization.");
            threadPoolExecutor = null;
        }
    }

    public void enqueueConnectorRefreshTask(int priority, Runnable runnable, String endpoint) {
        enqueueTask(new ThreadPoolTask(priority, runnable, endpoint));
    }

    public Set<String> getQueuedConnectorEndpoints() {
        return queue.getConnectorEndpoints();
    }

    private void enqueueTask(ThreadPoolTask task) {
        if (enabled) {
            threadPoolExecutor.execute(task);
        } else {
            // Only relevant for test environment, where execution is disabled
            queue.add(task);
        }
    }
}
