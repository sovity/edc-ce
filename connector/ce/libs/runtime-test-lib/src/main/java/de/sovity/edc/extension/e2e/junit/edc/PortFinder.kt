/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.edc

import de.sovity.edc.extension.e2e.connector.config.PortUtils

object PortFinder {
    fun <T> retryFindingPorts(
        numPorts: Int,
        maxAttempts: Int,
        cleanupFn: () -> Unit = {},
        fn: (port: Int) -> T
    ): T {
        require(maxAttempts > 0) { "maxAttempts must be greater than 0" }
        var last: Throwable? = null
        repeat(maxAttempts) {
            val port = PortUtils.getFreePortRange(numPorts)
            try {
                return fn(port)
            } catch (e: Exception) {
                val rootCause = getRootCause(e)
                if (rootCause.message == "Address already in use") {
                    last = e
                    return@repeat
                }
                throw e
            } finally {
                try {
                    cleanupFn()
                } catch (_: Exception) {
                    // swallow
                }
            }
        }
        throw RuntimeException(
            @Suppress("MaxLineLength")
            "Failed to find free port range after $maxAttempts attempts. The issue is that the ports are not immediately occupied after being found, giving another thread the chance to claim them.",
            last
        )
    }

    private fun getRootCause(e: Throwable): Throwable {
        var rootCause = e
        while (rootCause.cause != null) {
            rootCause = rootCause.cause!!
        }
        return rootCause
    }
}
