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

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;


@Getter
@RequiredArgsConstructor
public class ThreadPoolTask implements Runnable {

    public static final Comparator<ThreadPoolTask> COMPARATOR = Comparator.comparing(ThreadPoolTask::getPriority)
            .thenComparing(ThreadPoolTask::getSequence);

    /**
     * {@link java.util.concurrent.PriorityBlockingQueue} does not guarantee sequential execution, so we need to add this.
     */
    private static final AtomicLong SEQ = new AtomicLong(0);
    private final long sequence = SEQ.incrementAndGet();
    private final int priority;
    private final Runnable task;
    private final String connectorEndpoint;

    @Override
    public void run() {
        this.task.run();
    }
}
