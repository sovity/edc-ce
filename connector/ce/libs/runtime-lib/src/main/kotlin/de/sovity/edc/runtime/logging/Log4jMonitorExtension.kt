/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.logging

import org.eclipse.edc.runtime.metamodel.annotation.Extension
import org.eclipse.edc.spi.monitor.Monitor
import org.eclipse.edc.spi.system.MonitorExtension

/**
 * Monitor Extensions are the only extension loaded via the Service Locator API for our EDC
 */
@Extension("Log4J Logger Monitor")
class Log4jMonitorExtension : MonitorExtension {
    override fun getMonitor(): Monitor = Log4jMonitor()
}
