/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.edc

import org.eclipse.edc.spi.monitor.Monitor
import java.util.function.Supplier

/**
 * Wrapper around [Monitor] that allows us to turn off logging
 */
class MonitorWithOffSwitch(
    private var delegate: Monitor
) : Monitor {
    /**
     * Turn off logging
     *
     * EDC shutdowns are very verbose with testcontainers, so let's turn off logging for a better dev XP
     */
    fun turnOff() {
        delegate = object : Monitor {}
    }

    // the following is necessary because Kotlin's "by" does not delegate default methods

    override fun severe(supplier: Supplier<String?>?, vararg errors: Throwable?) {
        delegate.severe(supplier, *errors)
    }

    override fun severe(message: String?, vararg errors: Throwable?) {
        delegate.severe(message, *errors)
    }

    override fun severe(data: Map<String?, Any?>?) {
        delegate.severe(data)
    }

    override fun warning(supplier: Supplier<String?>?, vararg errors: Throwable?) {
        delegate.warning(supplier, *errors)
    }

    override fun warning(message: String?, vararg errors: Throwable?) {
        delegate.warning(message, *errors)
    }

    override fun info(supplier: Supplier<String?>?, vararg errors: Throwable?) {
        delegate.info(supplier, *errors)
    }

    override fun info(message: String?, vararg errors: Throwable?) {
        delegate.info(message, *errors)
    }

    override fun debug(supplier: Supplier<String?>?, vararg errors: Throwable?) {
        delegate.debug(supplier, *errors)
    }

    override fun debug(message: String?, vararg errors: Throwable?) {
        delegate.debug(message, *errors)
    }

    override fun sanitizeMessage(supplier: Supplier<String?>?): String? =
        delegate.sanitizeMessage(supplier)

    override fun withPrefix(prefix: String?): Monitor? =
        delegate.withPrefix(prefix)
}
