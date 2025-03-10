/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.auth

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.ce.dependency_bundles.CeDependencyBundles
import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.EdcModule

object ApiKeyAuthModule {
    fun instance() = EdcModule(
        name = "management-iam-api-key",
        documentation = "Legacy API Key Auth for the Management API"
    ).apply {
        dependencyBundle(CeDependencyBundles.managementApiAuthApiKey)

        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.EDC_API_AUTH_KEY
        ) {
            requiredInProd()
            defaultValueOutsideProd("ApiKeyDefaultValue")
            warnIfUnset = true
        }
    }
}
