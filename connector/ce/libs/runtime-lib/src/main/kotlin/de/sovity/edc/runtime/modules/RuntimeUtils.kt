/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules

import org.eclipse.edc.spi.system.configuration.Config

object RuntimeUtils {
    fun isProduction(config: Config): Boolean =
        config.getString(RuntimeConfigProps.SOVITY_ENVIRONMENT.property)?.lowercase() == "production"
}

fun Config.getEnvironment(): String? = RuntimeConfigProps.SOVITY_ENVIRONMENT.getStringOrNull(this)

fun Config.isProduction(): Boolean = RuntimeUtils.isProduction(this)
