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
import lombok.SneakyThrows;
import org.apache.commons.lang3.Validate;
import org.eclipse.edc.spi.monitor.Monitor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ThreadPoolTest {

    /**
     * Regression against bug where parallelity wasn't actually enabled
     */
    @Test
    @SneakyThrows
    void testParallelExecution() {
        ThreadPool threadPool = newThreadPool(2);
        var latch = new CountDownLatch(2);
        var result = new ArrayList<String>();

        var a = new BlockedRunnable(() -> {
            result.add("a");
            latch.countDown();
        });
        var b = new BlockedRunnable(() -> {
            result.add("b");
            latch.countDown();
        });

        threadPool.enqueueConnectorRefreshTask(0, a, "a");
        threadPool.enqueueConnectorRefreshTask(0, b, "b");

        b.release();
        Thread.sleep(250); // For some reason this is required
        a.release();

        Validate.isTrue(latch.await(500, TimeUnit.MILLISECONDS), "latch timed out");
        assertThat(result).containsExactly("b", "a");
    }


    @NotNull
    private ThreadPool newThreadPool(int numThreads) {
        var monitor = mock(Monitor.class);
        var brokerServerSettings = mock(BrokerServerSettings.class);
        when(brokerServerSettings.getNumThreads()).thenReturn(numThreads);
        var queue = new ThreadPoolTaskQueue();
        return new ThreadPool(queue, brokerServerSettings, monitor);
    }

    private static class BlockedRunnable implements Runnable {
        private static final Object GLOBAL_LOCK = new Object();
        private final Runnable runnable;
        private final ReentrantLock lock = new ReentrantLock();

        private BlockedRunnable(Runnable runnable) {
            this.runnable = runnable;
            lock.lock();
        }

        public void release() {
            lock.unlock();
        }

        @Override
        @SneakyThrows
        public void run() {
            var ok = lock.tryLock(10, TimeUnit.SECONDS);
            Validate.isTrue(ok, "Program is stuck!");

            // Prevent concurrency issues within the test code
            synchronized (GLOBAL_LOCK) {
                runnable.run();
            }
        }
    }
}
