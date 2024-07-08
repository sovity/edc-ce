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

import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;

class ThreadPoolQueueTest {


    /**
     * Regression against bug where the queue did not act like a queue.
     */
    @Test
    void testOrdering() {
        Runnable noop = () -> {
        };

        var c10 = mock(ConnectorRef.class);
        var c20 = mock(ConnectorRef.class);
        var c11 = mock(ConnectorRef.class);
        var c21 = mock(ConnectorRef.class);
        var c00 = mock(ConnectorRef.class);

        var queue = new ThreadPoolTaskQueue();
        queue.add(new ThreadPoolTask(1, noop, c10));
        queue.add(new ThreadPoolTask(2, noop, c20));
        queue.add(new ThreadPoolTask(1, noop, c11));
        queue.add(new ThreadPoolTask(2, noop, c21));
        queue.add(new ThreadPoolTask(0, noop, c00));

        var result = new ArrayList<ThreadPoolTask>();
        queue.getQueue().drainTo(result);

        Assertions.assertThat(result).extracting(ThreadPoolTask::getConnectorRef)
                .containsExactly(c00, c10, c11, c20, c21);
    }
}
