/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce

import de.sovity.edc.runtime.modules.runtime.InitialConfigFactory
import de.sovity.edc.runtime.modules.runtime.SovityEdcRuntime
import org.eclipse.edc.boot.system.ExtensionLoader
import org.eclipse.edc.boot.system.ServiceLocatorImpl

/**
 * Production main entry point
 */
fun main(args: Array<String>) {
    val rootModule = CeRootModule.ceRoot()
    val initialConfig = InitialConfigFactory.initialConfigFromEnv()

    // monitor is actually loaded via the Java Service Locator API in prod
    // so our module system cannot switch between monitor extensions
    val extensionLoader = ExtensionLoader(ServiceLocatorImpl())
    val monitor = extensionLoader.loadMonitor(*args)

    val runtime = SovityEdcRuntime(rootModule, initialConfig, monitor)
    Runtime.getRuntime().addShutdownHook(Thread { runtime.shutdown() })
    runtime.boot()
}
