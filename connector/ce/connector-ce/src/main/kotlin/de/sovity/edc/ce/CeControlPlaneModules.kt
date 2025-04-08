/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce

import de.sovity.edc.ce.api.CeApiModule
import de.sovity.edc.ce.api.ui.model.UiConfigFeature
import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.ce.config.CeDataspace
import de.sovity.edc.ce.config.moduleIfCeDataspace
import de.sovity.edc.ce.config.withCeDataspaceChoice
import de.sovity.edc.ce.dependency_bundles.CeDependencyBundles
import de.sovity.edc.ce.modules.auth.ApiKeyAuthModule
import de.sovity.edc.ce.modules.config_utils.ConfigUtilsImpl
import de.sovity.edc.ce.modules.config_utils.ConfigUtilsModule
import de.sovity.edc.ce.modules.dataspaces.catena.CatenaDataspaceFeatures
import de.sovity.edc.ce.modules.dataspaces.sovity.SovityDataspaceFeatures
import de.sovity.edc.ce.modules.db.DbModule
import de.sovity.edc.ce.modules.messaging.contract_termination.ContractTerminationModule
import de.sovity.edc.ce.modules.messaging.messenger.SovityMessengerModule
import de.sovity.edc.ce.modules.policy_utils.always_true.AlwaysTruePolicyDefinitionModule
import de.sovity.edc.ce.modules.policy_utils.creator.SimplePolicyCreatorModule
import de.sovity.edc.runtime.config.UrlPathUtils
import de.sovity.edc.runtime.modules.RuntimeConfigProps
import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.DocumentedFn
import de.sovity.edc.runtime.modules.model.EdcModule

@Suppress("MaxLineLength")
object CeControlPlaneModules {

    fun withIntegratedDataPlane() = EdcModule(
        name = "control-plane-with-integrated-data-plane",
        documentation = "Deploys an EDC Connector with both a Control Plane and an integrated Data Plane."
    ).apply {
        withCommonCeControlPlaneSetup()
        modules(CeDataPlaneModules.integrated())
    }

    fun standalone() = EdcModule(
        name = "control-plane-standalone",
        documentation = "Deploys only a Control Plane. Requires at least one additionally deployed data plane to become a fully functioning EDC Connector."
    ).apply {
        withCommonCeControlPlaneSetup()
    }

    private fun EdcModule.withCommonCeControlPlaneSetup() {
        // Dataspace choice moved here for several reasons:
        // - clear auto-generation of "deployment kind -> dataspace kind" docs
        // - this needs to be declared before use to prevent our module system crying about multiple "conflicting"
        //   declarations for module-deciding properties
        withCeDataspaceChoice()

        dependencyBundles(CeDependencyBundles.controlPlaneBase)

        modules(
            // CE base
            base(),

            // CE UI
            EdcUiModule.withActivatedFeatures(
                category = ConfigPropCategory.OPTIONAL,
                uiFeaturesTitle = "Default CE Features",
                uiFeatures = listOf(UiConfigFeature.OPEN_SOURCE_MARKETING)
            ),

            // CE-specific features
            DbModule.forMigrationScripts("classpath:db/migration-ce")
        )

        moduleOneOf(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.SOVITY_MANAGEMENT_API_IAM_KIND,
            { warnIfUnset = true },
            ApiKeyAuthModule.instance()
        )

        moduleIfCeDataspace(
            CeDataspace.SOVITY_MOCK_IAM,
            SovityDataspaceFeatures.controlPlaneWithMockIam()
        )
        moduleIfCeDataspace(
            CeDataspace.SOVITY_DAPS,
            SovityDataspaceFeatures.controlPlaneWithDaps()
        )
        moduleIfCeDataspace(
            CeDataspace.SOVITY_DAPS_OMEJDN,
            SovityDataspaceFeatures.controlPlaneWithDapsOmejdn()
        )
        moduleIfCeDataspace(
            CeDataspace.CATENA_X,
            CatenaDataspaceFeatures.tractusControlPlane()
        )
    }

    fun base() = EdcModule(
        name = "control-plane-base",
        documentation = "Control plane default setup and sovity features"
    ).apply {
        modules(
            AlwaysTruePolicyDefinitionModule.instance(),
            CeApiModule.instance(),
            ConfigUtilsModule.instance(),
            ContractTerminationModule.instance(),
            SimplePolicyCreatorModule.instance(),
            SovityMessengerModule.instance()
        )

        property(
            ConfigPropCategory.OVERRIDES,
            RuntimeConfigProps.SOVITY_FIRST_PORT
        ) {
            defaultValue("11000")
            warnIfOverridden = true
        }

        configureManagementApi()
        configureProtocolApi()
        configureStateMachines()
        configureMetadata()
    }

    private fun EdcModule.configureProtocolApi() {
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.WEB_HTTP_PROTOCOL_PATH
        ) {
            defaultValueFn = DocumentedFn("Defaults to `[base/]api/dsp`") { config ->
                val basePath = CeConfigProps.SOVITY_BASE_PATH.getStringOrThrow(config)
                UrlPathUtils.urlPathJoin(basePath, "api/dsp")
            }
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.WEB_HTTP_PROTOCOL_PORT
        ) {
            defaultFromPropPlus(RuntimeConfigProps.SOVITY_FIRST_PORT, 3)
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_DSP_CALLBACK_ADDRESS
        ) {
            defaultValueFn = DocumentedFn("Defaults to Protocol API URL") {
                ConfigUtilsImpl.getProtocolApiUrl(it)
            }
            warnIfOverridden = true
        }
    }

    private fun EdcModule.configureManagementApi() {
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.WEB_HTTP_MANAGEMENT_PATH
        ) {
            defaultValueFn = DocumentedFn("Defaults to `[base/]api/management`") { config ->
                val basePath = CeConfigProps.SOVITY_BASE_PATH.getStringOrThrow(config)
                UrlPathUtils.urlPathJoin(basePath, "api/management")
            }
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.WEB_HTTP_MANAGEMENT_PORT
        ) {
            defaultFromPropPlus(RuntimeConfigProps.SOVITY_FIRST_PORT, 2)
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_EDC_UI_MANAGEMENT_API_URL_SHOWN_IN_DASHBOARD
        ) {
            defaultValueFn = DocumentedFn("Defaults to Management API URL") {
                ConfigUtilsImpl.getManagementApiUrl(it)
            }
        }

        // CORS
        property(
            ConfigPropCategory.OPTIONAL,
            CeConfigProps.EDC_WEB_REST_CORS_ENABLED
        ) {
            defaultValueOutsideProd("true")
        }
        property(
            ConfigPropCategory.OPTIONAL,
            CeConfigProps.EDC_WEB_REST_CORS_HEADERS
        ) {
            defaultValueOutsideProd("origin,content-type,accept,authorization,X-Api-Key")
        }
        property(
            ConfigPropCategory.OPTIONAL,
            CeConfigProps.EDC_WEB_REST_CORS_ORIGINS
        ) {
            defaultValueOutsideProd("*")
        }
    }

    private fun EdcModule.configureMetadata() {
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.SOVITY_EDC_TITLE
        ) {
            defaultValue("Example Connector")
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.SOVITY_EDC_DESCRIPTION
        ) {
            defaultValue("Example Connector Description")
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.SOVITY_EDC_CURATOR_URL
        ) {
            defaultValue("https://example.com")
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.SOVITY_EDC_CURATOR_NAME
        ) {
            defaultValue("Example Company")
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.SOVITY_EDC_MAINTAINER_URL
        ) {
            defaultValue("https://sovity.de")
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.SOVITY_EDC_MAINTAINER_NAME
        ) {
            defaultValue("sovity GmbH")
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_RUNTIME_ID
        ) {
            defaultFromProp(CeConfigProps.EDC_PARTICIPANT_ID)
            warnIfOverridden = true
        }
    }

    private fun EdcModule.configureStateMachines() {
        property(
            ConfigPropCategory.OPTIONAL,
            CeConfigProps.EDC_TRANSFER_SEND_RETRY_LIMIT
        ) {
            defaultValue("1")
        }
        property(
            ConfigPropCategory.OPTIONAL,
            CeConfigProps.EDC_TRANSFER_SEND_RETRY_BASE_DELAY_MS
        ) {
            defaultValue("100")
        }
        property(
            ConfigPropCategory.OPTIONAL,
            CeConfigProps.EDC_NEGOTIATION_CONSUMER_SEND_RETRY_LIMIT
        ) {
            defaultValue("1")
        }
        property(
            ConfigPropCategory.OPTIONAL,
            CeConfigProps.EDC_NEGOTIATION_PROVIDER_SEND_RETRY_LIMIT
        ) {
            defaultValue("1")
        }
        property(
            ConfigPropCategory.OPTIONAL,
            CeConfigProps.EDC_NEGOTIATION_CONSUMER_SEND_RETRY_BASE_DELAY_MS
        ) {
            defaultValue("100")
        }
        property(
            ConfigPropCategory.OPTIONAL,
            CeConfigProps.EDC_NEGOTIATION_PROVIDER_SEND_RETRY_BASE_DELAY_MS
        ) {
            defaultValue("100")
        }
    }
}
