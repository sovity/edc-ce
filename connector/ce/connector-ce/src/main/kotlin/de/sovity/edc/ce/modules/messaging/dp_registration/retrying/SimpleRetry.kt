/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.dp_registration.retrying

import java.time.Duration

object SimpleRetry {
    fun <T> retry(
        maxAttempts: Int,
        delay: Duration,
        block: () -> T
    ): T {
        require(maxAttempts > 0) { "maxAttempts should be greater than 0" }
        var lastException: Throwable? = null
        repeat(maxAttempts) {
            try {
                return block()
            } catch (e: InterruptedException) {
                throw e
            } catch (e: Exception) {
                lastException?.let { e.addSuppressed(it) }
                lastException = e
                Thread.sleep(delay.toMillis())
            }
        }
        throw lastException!!
    }
}
