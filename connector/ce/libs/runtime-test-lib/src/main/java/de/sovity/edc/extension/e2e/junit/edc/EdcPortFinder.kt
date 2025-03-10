/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.edc

import de.sovity.edc.runtime.modules.RuntimeConfigProps
import de.sovity.edc.runtime.modules.model.ConfigPropRef

object EdcPortFinder {
    /**
     * Retries launching the EDC with different ports, if it fails due to a race condition
     *
     * Only reason this method exists because dealing with the Map of [ConfigPropRef] is annoying, because we don't
     * have access to the [ConfigPropRef]s from here.
     */
    fun <T> withAutoPortHandling(
        numPorts: Int,
        maxAttempts: Int,
        config: Map<ConfigPropRef, String>,
        cleanupFn: () -> Unit = {},
        fn: (config: Map<ConfigPropRef, String>) -> T
    ): T {
        if (config[RuntimeConfigProps.SOVITY_FIRST_PORT] != "auto") {
            // No port finding
            return fn(config)
        }

        return PortFinder.retryFindingPorts(numPorts, maxAttempts, cleanupFn) { port ->
            val configWithPort = config + (RuntimeConfigProps.SOVITY_FIRST_PORT to port.toString())
            fn(configWithPort)
        }
    }
}
