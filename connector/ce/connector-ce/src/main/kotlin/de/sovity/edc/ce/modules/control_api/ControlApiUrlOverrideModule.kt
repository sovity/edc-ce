/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.control_api

import de.sovity.edc.runtime.modules.model.EdcModule
import org.eclipse.edc.connector.api.control.configuration.ControlApiConfigurationExtension

object ControlApiUrlOverrideModule {
    fun instance() = EdcModule(
        "control-api-url-override",
        "Overrides edc hostname to be used in control api"
    ).apply {
        excludeServiceExtensions(ControlApiConfigurationExtension::class.java)
        serviceExtensions(SovityControlApiConfigurationExtension::class.java)
    }
}
