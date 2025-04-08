/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.ce.modules.config_utils.ConfigUtilsImpl
import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.DocumentedFn
import de.sovity.edc.runtime.modules.model.EdcModule

object CeApiModule {
    fun instance() = EdcModule(
        name = "sovity-ce-api",
        documentation = "sovity Community Edition EDC API Wrapper"
    ).apply {
        serviceExtensions(
            CeApiExtension::class.java
        )

        property(
            ConfigPropCategory.OPTIONAL,
            CeConfigProps.SOVITY_EDC_UI_MANAGEMENT_API_URL_SHOWN_IN_DASHBOARD
        ) {
            defaultValueFn = DocumentedFn("Management API URL") {
                ConfigUtilsImpl.getManagementApiUrl(it)
            }
        }
    }
}
