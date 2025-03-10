/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.dp_registration

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.runtime.config.UrlPathUtils
import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.DocumentedFn
import de.sovity.edc.runtime.modules.model.EdcModule

object DataPlaneRegistrationModule {
    fun embedded() = EdcModule(
        name = "dp-register-embedded",
        documentation = "Register embedded data plane for a full CP + DP deployments at the embedded control plane."
    ).apply {
        serviceExtensions(
            DataPlaneRegisterEmbeddedExtension::class.java
        )
    }

    @Suppress("LongMethod")
    fun standalone() = EdcModule(
        name = "dp-register-standalone",
        documentation = "Registers this stand-alone data plane at the given external control plane"
    ).apply {
        serviceExtensions(
            DataPlaneRegisterStandaloneExtension::class.java
        )

        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.SOVITY_INTERNAL_CP_FQDN
        ) {
            requiredInProd()
            defaultValueOutsideProd("localhost")
        }

        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.SOVITY_INTERNAL_CP_MANAGEMENT_API_KEY
        ) {
            required()
        }

        property(
            ConfigPropCategory.OPTIONAL,
            CeConfigProps.SOVITY_INTERNAL_CP_BASE_PATH
        ) {
            defaultValue("/")
        }

        property(
            ConfigPropCategory.OPTIONAL,
            CeConfigProps.SOVITY_INTERNAL_CP_PROTOCOL
        ) {
            defaultValue("http://")
        }

        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_INTERNAL_CP_FIRST_PORT
        ) {
            defaultValue("11000")
        }

        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_INTERNAL_CP_WEB_HTTP_CONTROL_PATH
        ) {
            defaultValueFn = DocumentedFn("Defaults to `[cpBasePath/]api/control`") { config ->
                val basePath = CeConfigProps.SOVITY_INTERNAL_CP_BASE_PATH.getStringOrThrow(config)
                UrlPathUtils.urlPathJoin(basePath, "api/control")
            }
            warnIfOverridden = true
        }

        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_INTERNAL_CP_WEB_HTTP_CONTROL_PORT
        ) {
            defaultFromPropPlus(CeConfigProps.SOVITY_INTERNAL_CP_FIRST_PORT, 4)
            warnIfOverridden = true
        }

        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_INTERNAL_CP_WEB_HTTP_MANAGEMENT_PATH
        ) {
            defaultValueFn = DocumentedFn("Defaults to `[basePath/]api/management`") { config ->
                val basePath = CeConfigProps.SOVITY_INTERNAL_CP_BASE_PATH.getStringOrThrow(config)
                UrlPathUtils.urlPathJoin(basePath, "api/management")
            }
            warnIfOverridden = true
        }

        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_INTERNAL_CP_WEB_HTTP_MANAGEMENT_PORT
        ) {
            defaultFromPropPlus(CeConfigProps.SOVITY_INTERNAL_CP_FIRST_PORT, 2)
            warnIfOverridden = true
        }

        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_INTERNAL_CP_CONTROL_API_URL
        ) {
            defaultValueFn = DocumentedFn(
                "Default is built from the `sovity.internal.cp.*` values"
            ) {
                UrlPathUtils.urlPathJoin(
                    CeConfigProps.SOVITY_INTERNAL_CP_PROTOCOL.getStringOrEmpty(it),
                    CeConfigProps.SOVITY_INTERNAL_CP_FQDN.getStringOrEmpty(it) +
                        ":" + CeConfigProps.SOVITY_INTERNAL_CP_WEB_HTTP_CONTROL_PORT.getStringOrThrow(it),
                    CeConfigProps.SOVITY_INTERNAL_CP_WEB_HTTP_CONTROL_PATH.getStringOrEmpty(it)
                )

            }
        }

        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_INTERNAL_CP_MANAGEMENT_API_URL
        ) {
            defaultValueFn = DocumentedFn(
                "Default is built from the `sovity.internal.cp.*` values"
            ) {
                UrlPathUtils.urlPathJoin(
                    CeConfigProps.SOVITY_INTERNAL_CP_PROTOCOL.getStringOrEmpty(it),
                    CeConfigProps.SOVITY_INTERNAL_CP_FQDN.getStringOrEmpty(it) +
                        ":" + CeConfigProps.SOVITY_INTERNAL_CP_WEB_HTTP_MANAGEMENT_PORT.getStringOrThrow(it),
                    CeConfigProps.SOVITY_INTERNAL_CP_WEB_HTTP_MANAGEMENT_PATH.getStringOrEmpty(it)
                )
            }
        }

        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_INTERNAL_CP_MANAGEMENT_API_KEY_HEADER
        ) {
            defaultValue("X-Api-Key")
        }
    }
}
