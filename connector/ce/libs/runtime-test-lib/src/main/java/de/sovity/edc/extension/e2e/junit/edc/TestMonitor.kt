/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.edc

import org.eclipse.edc.spi.monitor.ConsoleMonitor
import org.eclipse.edc.spi.monitor.Monitor

object TestMonitor {
    fun createTestMonitor(runtimeName: String): MonitorWithOffSwitch {
        // disable logs when "quiet" log level is >set
        val gradleLoggingLevel = System.getProperty("org.gradle.logging.level")
        if (gradleLoggingLevel != null) {
            println("TestMonitor#createMonitor: Monitor is switched to Noop-Monitor due to org.gradle.logging.level=$gradleLoggingLevel")
            return MonitorWithOffSwitch(object : Monitor {})
        }

        val monitor = MonitorWithOffSwitch(ConsoleMonitor(runtimeName, ConsoleMonitor.Level.DEBUG, true))
        @Suppress("MaxLineLength")
        monitor.info("TestMonitor#createMonitor: Whatever monitor would have been used, we are overriding it with a ConsoleMonitor, so we can prefix the runtime")
        return monitor
    }
}
