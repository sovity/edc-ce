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

import de.sovity.edc.ext.catalog.crawler.orchestration.queue.ThreadPoolTask;
import de.sovity.edc.ext.catalog.crawler.orchestration.queue.ThreadPoolTaskQueue;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class ThreadPoolQueueTest {


    /**
     * Regression against bug where the queue did not act like a queue.
     */
    @Test
    void testOrdering() {
        Runnable noop = () -> {
        };

        var queue = new ThreadPoolTaskQueue();
        queue.add(new ThreadPoolTask(1, noop, "1.0"));
        queue.add(new ThreadPoolTask(2, noop, "2.0"));
        queue.add(new ThreadPoolTask(1, noop, "1.1"));
        queue.add(new ThreadPoolTask(2, noop, "2.1"));
        queue.add(new ThreadPoolTask(0, noop, "0.0"));

        var result = new ArrayList<ThreadPoolTask>();
        queue.getQueue().drainTo(result);

        Assertions.assertThat(result).extracting(ThreadPoolTask::getConnectorRef)
                .containsExactly("0.0", "1.0", "1.1", "2.0", "2.1");
    }
}
