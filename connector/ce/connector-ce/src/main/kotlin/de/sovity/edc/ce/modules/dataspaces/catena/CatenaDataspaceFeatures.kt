/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.catena

import de.sovity.edc.ce.api.ui.model.UiConfigFeature
import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.ce.config.CeVaultEntries
import de.sovity.edc.ce.dependency_bundles.CeDependencyBundles
import de.sovity.edc.ce.modules.config_utils.ConfigUtilsImpl
import de.sovity.edc.ce.modules.dataspaces.catena.edrs.EdrApiServiceV2CatenaExtension
import de.sovity.edc.runtime.config.UrlPathUtils
import de.sovity.edc.runtime.modules.RuntimeConfigProps
import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.DocumentedFn
import de.sovity.edc.runtime.modules.model.EdcModule
import org.eclipse.edc.connector.controlplane.api.management.edr.EdrCacheApiExtension

object CatenaDataspaceFeatures {

    fun tractusControlPlane() = EdcModule(
        name = "tractus-control-plane",
        documentation = "Enable Tractus-X Extensions and Catena-X C2C IAM"
    ).apply {
        dependencyBundles(CeDependencyBundles.tractusControlPlane)
        configureIatp()
        serviceExtensions(EdrApiServiceV2CatenaExtension::class.java)
        excludeServiceExtensions(EdrCacheApiExtension::class.java)

        // UI Features
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_EDC_UI_FEATURES_ADD_WILDCARD.withWildcardValue(UiConfigFeature.CATENA_POLICIES.name)
        ) {
            defaultValue("true")
        }
    }

    fun tractusDataPlane() = EdcModule(
        name = "tractus-data-plane",
        documentation = "Enable Tractus-X Extensions and Catena-X C2C IAM"
    ).apply {
        dependencyBundles(CeDependencyBundles.tractusDataPlane)
        excludeServiceExtensions(EdrCacheApiExtension::class.java)
        configureIatp()
        configureProxyApiAndEdrs()
    }

    @Suppress("LongMethod")
    private fun EdcModule.configureIatp() {
        vaultEntry(CeVaultEntries.STS_CLIENT_SECRET)

        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.EDC_PARTICIPANT_ID
        ) {
            required()
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.EDC_IAM_ISSUER_ID
        ) {
            required()
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.TX_EDC_IAM_STS_DIM_URL
        ) {
            required()
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.EDC_IAM_STS_OAUTH_CLIENT_ID
        ) {
            required()
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.EDC_IAM_STS_OAUTH_TOKEN_URL
        ) {
            required()
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.TX_IAM_IATP_BDRS_SERVER_URL
        ) {
            required()
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.EDC_IAM_TRUSTED_ISSUER_COFINITY_ID
        ) {
            required()
        }

        // defaults
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_IAM_STS_OAUTH_CLIENT_SECRET_ALIAS
        ) {
            defaultValue("sts-client-secret")
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.TX_EDC_IAM_IATP_DEFAULT_SCOPES_GOVERNANCE_ALIAS
        ) {
            defaultValue("org.eclipse.tractusx.vc.type")
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.TX_EDC_IAM_IATP_DEFAULT_SCOPES_GOVERNANCE_TYPE
        ) {
            defaultValue("DataExchangeGovernanceCredential")
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.TX_EDC_IAM_IATP_DEFAULT_SCOPES_GOVERNANCE_OPERATION
        ) {
            defaultValue("read")
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.TX_EDC_IAM_IATP_DEFAULT_SCOPES_MEMBERSHIP_ALIAS
        ) {
            defaultValue("org.eclipse.tractusx.vc.type")
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.TX_EDC_IAM_IATP_DEFAULT_SCOPES_MEMBERSHIP_TYPE
        ) {
            defaultValue("MembershipCredential")
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.TX_EDC_IAM_IATP_DEFAULT_SCOPES_MEMBERSHIP_OPERATION
        ) {
            defaultValue("read")
        }
    }

    private fun EdcModule.configureProxyApiAndEdrs() {

        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.WEB_HTTP_PROXY_PORT
        ) {
            defaultFromPropPlus(RuntimeConfigProps.SOVITY_FIRST_PORT, 6)
            warnIfOverridden = true
        }

        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.WEB_HTTP_PROXY_PATH
        ) {
            defaultValueFn = DocumentedFn("Defaults to `[basePath/]api/proxy`") { config ->
                val basePath = CeConfigProps.SOVITY_BASE_PATH.getStringOrThrow(config)
                UrlPathUtils.urlPathJoin(basePath, "api/proxy")
            }
            warnIfOverridden = true
        }

        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.TX_EDC_DPF_CONSUMER_PROXY_PORT
        ) {
            defaultFromProp(CeConfigProps.WEB_HTTP_PROXY_PORT)
            warnIfOverridden = true
        }

        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.TX_EDC_DATAPLANE_TOKEN_REFRESH_ENDPOINT
        ) {
            defaultValueFn = DocumentedFn("Defaults to `[publicApi]/token`") { config ->
                UrlPathUtils.urlPathJoin(
                    ConfigUtilsImpl.getPublicApiUrl(config),
                    "/token"
                )
            }
            warnIfOverridden = true
        }
    }
}
