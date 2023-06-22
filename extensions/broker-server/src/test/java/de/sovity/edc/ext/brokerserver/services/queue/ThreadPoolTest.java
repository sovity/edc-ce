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
import org.eclipse.edc.spi.monitor.Monitor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ThreadPoolTest {

    @Test
    void testParallelExecution() {
        var monitor = mock(Monitor.class);
        var brokerServerSettings = mock(BrokerServerSettings.class);
        when(brokerServerSettings.getNumThreads()).thenReturn(2);
        var threadPool = new ThreadPool(brokerServerSettings, monitor);
        var result = new ArrayList<String>();
        threadPool.enqueueConnectorRefreshTask(0, delay(100, () -> result.add("1")), "1");
        threadPool.enqueueConnectorRefreshTask(0, delay(50, () -> result.add("2")), "2");
        safeSleep(200);

        assertThat(result).containsExactly("2", "1");
    }

    Runnable delay(int delayInMs, Runnable onDone) {
        return () -> {
            safeSleep(delayInMs);
            onDone.run();
        };
    }

    @SneakyThrows
    void safeSleep(int delayInMs) {
        Thread.sleep(delayInMs);
    }
}
