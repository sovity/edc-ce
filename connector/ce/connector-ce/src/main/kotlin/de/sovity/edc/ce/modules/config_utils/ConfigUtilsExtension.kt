/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.config_utils

import de.sovity.edc.runtime.config.ConfigUtils
import org.eclipse.edc.runtime.metamodel.annotation.Provides
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext

@Provides(ConfigUtils::class)
class ConfigUtilsExtension : ServiceExtension {
    override fun initialize(context: ServiceExtensionContext) {
        context.registerService(ConfigUtils::class.java, ConfigUtilsImpl(context.config))
    }
}
