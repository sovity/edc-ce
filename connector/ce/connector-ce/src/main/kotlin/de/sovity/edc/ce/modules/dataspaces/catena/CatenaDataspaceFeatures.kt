/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.catena

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.ce.config.CeVaultEntries
import de.sovity.edc.ce.dependency_bundles.CeDependencyBundles
import de.sovity.edc.runtime.config.UrlPathUtils
import de.sovity.edc.runtime.modules.RuntimeConfigProps
import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.DocumentedFn
import de.sovity.edc.runtime.modules.model.EdcModule

object CatenaDataspaceFeatures {

    fun tractusControlPlane() = EdcModule(
        name = "tractus-control-plane",
        documentation = "Enable Tractus-X Extensions and Catena-X C2C IAM"
    ).apply {
        dependencyBundles(CeDependencyBundles.tractusControlPlane)
        configureIatp()
    }

    fun tractusDataPlane() = EdcModule(
        name = "tractus-data-plane",
        documentation = "Enable Tractus-X Extensions and Catena-X C2C IAM"
    ).apply {
        dependencyBundles(CeDependencyBundles.tractusDataPlane)
        configureIatp()
        configureProxyApi()
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
            CeConfigProps.EDC_IAM_STS_DIM_URL
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
            CeConfigProps.EDC_IAM_IATP_DEFAULT_SCOPES_GOVERNANCE_ALIAS
        ) {
            defaultValue("org.eclipse.tractusx.vc.type")
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_IAM_IATP_DEFAULT_SCOPES_GOVERNANCE_TYPE
        ) {
            defaultValue("DataExchangeGovernanceCredential")
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_IAM_IATP_DEFAULT_SCOPES_GOVERNANCE_OPERATION
        ) {
            defaultValue("read")
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_IAM_IATP_DEFAULT_SCOPES_MEMBERSHIP_ALIAS
        ) {
            defaultValue("org.eclipse.tractusx.vc.type")
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_IAM_IATP_DEFAULT_SCOPES_MEMBERSHIP_TYPE
        ) {
            defaultValue("MembershipCredential")
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_IAM_IATP_DEFAULT_SCOPES_MEMBERSHIP_OPERATION
        ) {
            defaultValue("read")
        }
    }

    private fun EdcModule.configureProxyApi() {
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.WEB_HTTP_CONSUMER_API_PATH
        ) {
            defaultValueFn = DocumentedFn("Defaults to `[basePath/]api/proxy`") { config ->
                val basePath = CeConfigProps.SOVITY_BASE_PATH.getStringOrThrow(config)
                UrlPathUtils.urlPathJoin(basePath, "api/proxy")
            }
            warnIfOverridden = true
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.TX_DPF_CONSUMER_PROXY_PORT
        ) {
            defaultFromPropPlus(RuntimeConfigProps.SOVITY_FIRST_PORT, 6)
            warnIfOverridden = true
        }
    }
}
