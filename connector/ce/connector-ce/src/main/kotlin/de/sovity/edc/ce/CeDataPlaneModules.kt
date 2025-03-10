/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.ce.config.CeDataspace
import de.sovity.edc.ce.config.moduleIfCeDataspace
import de.sovity.edc.ce.config.withCeDataspaceChoice
import de.sovity.edc.ce.dependency_bundles.CeDependencyBundles
import de.sovity.edc.ce.modules.auth.DataPlaneEdrTokenConfig
import de.sovity.edc.ce.modules.config_utils.ConfigUtilsImpl
import de.sovity.edc.ce.modules.config_utils.ConfigUtilsModule
import de.sovity.edc.ce.modules.dataspaces.catena.CatenaDataspaceFeatures
import de.sovity.edc.ce.modules.db.DbModule
import de.sovity.edc.ce.modules.messaging.dp_registration.DataPlaneRegistrationModule
import de.sovity.edc.runtime.config.UrlPathUtils
import de.sovity.edc.runtime.modules.RuntimeConfigProps
import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.DocumentedFn
import de.sovity.edc.runtime.modules.model.EdcModule

@Suppress("MaxLineLength")
object CeDataPlaneModules {
    fun standalone() = EdcModule(
        name = "data-plane-standalone",
        documentation = "Deploys only a Data Plane. Depends on a control plane."
    ).apply {
        // Dataspace choice moved here for several reasons:
        // - clear auto-generation of "deployment kind -> dataspace kind" docs
        // - this needs to be declared before use to prevent our module system crying about multiple "conflicting"
        //   declarations for module-deciding properties
        withCeDataspaceChoice()

        modules(
            baseStandalone(),
            DbModule.forMigrationScripts("classpath:db/migration-ce")
        )

        withCommonCeDataplaneSetup()
    }

    fun integrated() = EdcModule(
        name = "data-plane-integrated",
        documentation = "Integrated DataPlane to be added to a Control Plane"
    ).apply {
        modules(baseIntegrated())
        withCommonCeDataplaneSetup()
    }

    private fun EdcModule.withCommonCeDataplaneSetup() {
        // Non-dataspace specific common features
        module(base())

        moduleIfCeDataspace(
            CeDataspace.CATENA_X,
            CatenaDataspaceFeatures.tractusDataPlane()
        )
    }

    fun base() = EdcModule(
        name = "data-plane-base",
        documentation = "Base data plane setup without data space variants"
    ).apply {
        dependencyBundles(
            CeDependencyBundles.dataPlaneBase,
            CeDependencyBundles.dataPlaneFeatures
        )

        modules(DataPlaneEdrTokenConfig.instance())

        configurePublicApi()
        configureTransferTypes()

        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_DATAPLANE_ENDPOINT_CONTROL_TRANSFER
        ) {
            defaultValueFn = DocumentedFn(
                "Default calculated with the internal Control API URL as base"
            ) { config ->
                UrlPathUtils.urlPathJoin(
                    ConfigUtilsImpl.getControlApiUrl(config),
                    "v1/dataflows"
                )
            }
        }
    }

    fun baseStandalone() = EdcModule(
        name = "data-plane-base-config-standalone",
        documentation = "Base standalone data plane setup without data space variants"
    ).apply {
        modules(
            ConfigUtilsModule.instance(),
            DataPlaneRegistrationModule.standalone(),
        )

        property(
            ConfigPropCategory.OVERRIDES,
            RuntimeConfigProps.SOVITY_FIRST_PORT
        ) {
            defaultValue("12000")
            warnIfOverridden = true
        }

        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_DATAPLANE_TOKEN_VALIDATION_ENDPOINT
        ) {
            defaultValueFn = DocumentedFn(
                "Default is built from the `sovity.internal.cp.*` values"
            ) {
                // Property is defaulted by DataPlaneRegistrationModule above
                val cpControlApiUrl = CeConfigProps.SOVITY_INTERNAL_CP_CONTROL_API_URL.getStringOrThrow(it)
                ConfigUtilsImpl.Endpoints.tokenValidationEndpoint(
                    cpControlApiUrl = cpControlApiUrl
                )
            }
        }
    }

    fun baseIntegrated() = EdcModule(
        name = "data-plane-base-config-integrated",
        documentation = "Base integrated data plane setup without data space variants"
    ).apply {
        modules(
            DataPlaneRegistrationModule.embedded()
        )

        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_DATAPLANE_TOKEN_VALIDATION_ENDPOINT
        ) {
            defaultValueFn = DocumentedFn("Defaults to Control API URL") {
                ConfigUtilsImpl.Endpoints.tokenValidationEndpoint(
                    cpControlApiUrl = ConfigUtilsImpl.getControlApiUrl(it)
                )
            }
        }
    }

    private fun EdcModule.configurePublicApi() {
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.WEB_HTTP_PUBLIC_PATH
        ) {
            defaultValueFn = DocumentedFn("Defaults to `[basePath/]api/public`") { config ->
                val basePath = CeConfigProps.SOVITY_BASE_PATH.getStringOrThrow(config)
                UrlPathUtils.urlPathJoin(basePath, "api/public")
            }
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.WEB_HTTP_PUBLIC_PORT
        ) {
            defaultFromPropPlus(RuntimeConfigProps.SOVITY_FIRST_PORT, 5)
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_DATAPLANE_API_PUBLIC_BASEURL
        ) {
            defaultValueFn = DocumentedFn("Defaults to Public API URL") {
                ConfigUtilsImpl.getPublicApiUrl(it)
            }
            warnIfOverridden = true
        }
    }

    private fun EdcModule.configureTransferTypes() {
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_DATAPLANE_TRANSFERTYPES
        ) {
            defaultValue("HttpData-PUSH,HttpData-PULL,AmazonS3-PUSH")
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_DATA_ADDRESS_TYPES
        ) {
            defaultValue("HttpData,HttpProxy,HttpPush,AzureStorage,AmazonS3")
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_DATAPLANE_DESTTYPES
        ) {
            defaultFromProp(CeConfigProps.SOVITY_DATA_ADDRESS_TYPES)
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_DATAPLANE_SOURCETYPES
        ) {
            defaultFromProp(CeConfigProps.SOVITY_DATA_ADDRESS_TYPES)
            warnIfOverridden = true
        }
    }
}
