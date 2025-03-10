/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.edc

import org.eclipse.edc.spi.EdcException
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

object TimeoutUtils {
    private val executorService = Executors.newSingleThreadExecutor()

    @Suppress("ThrowsCount")
    fun runDeferred(timeout: Duration, fn: () -> Unit) {
        val latch = CountDownLatch(1)
        val runtimeException = AtomicReference<Exception>()
        executorService.execute {
            try {
                fn()
            } catch (e: Exception) {
                runtimeException.set(e)
                throw EdcException(e)
            } finally {
                latch.countDown()
            }
        }
        if (!latch.await(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
            throw EdcException("Failed to run deferred function in $timeout", runtimeException.get())
        }
        runtimeException.get()?.let { throw it }
    }
}
