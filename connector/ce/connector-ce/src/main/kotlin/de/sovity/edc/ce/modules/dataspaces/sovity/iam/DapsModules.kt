/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sovity.iam

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.ce.config.CeVaultEntries
import de.sovity.edc.ce.dependency_bundles.CeDependencyBundles
import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.EdcModule

object DapsModules {
    fun sovityDaps() = EdcModule(
        name = "c2c-iam-sovity-daps",
        documentation = "DAPS / OAuth2 Connector-to-Connector Identity and Access Management using the sovity DAPS"
    ).apply {
        dependencyBundle(CeDependencyBundles.c2cIamDapsSovity)
        setupDapsCommon()
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_OAUTH_PROVIDER_AUDIENCE
        ) {
            defaultFromProp(CeConfigProps.EDC_OAUTH_TOKEN_URL)
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_OAUTH_ENDPOINT_AUDIENCE
        ) {
            defaultValue("edc:dsp-api")
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_AGENT_IDENTITY_KEY
        ) {
            defaultValue("azp")
        }
    }

    fun sovityDapsOmejdn() = EdcModule(
        name = "c2c-iam-sovity-daps-omejdn",
        documentation = "DAPS / OAuth2 Connector-to-Connector Identity and Access Management using the deprecated Omejdn DAPS"
    ).apply {
        dependencyBundle(CeDependencyBundles.c2cIamDapsOmejdn)
        setupDapsCommon()
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_OAUTH_PROVIDER_AUDIENCE
        ) {
            defaultValue("idsc:IDS_CONNECTORS_ALL")
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_OAUTH_ENDPOINT_AUDIENCE
        ) {
            defaultValue("idsc:IDS_CONNECTORS_ALL")
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_AGENT_IDENTITY_KEY
        ) {
            defaultValue("client_id")
        }
    }

    private fun EdcModule.setupDapsCommon() {
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.EDC_PARTICIPANT_ID
        ) {
            required()
        }

        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.EDC_OAUTH_TOKEN_URL
        ) {
            required()
        }

        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.EDC_OAUTH_PROVIDER_JWKS_URL
        ) {
            required()
        }

        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.EDC_OAUTH_CLIENT_ID
        ) {
            defaultFromProp(CeConfigProps.EDC_PARTICIPANT_ID)
            documentation = "OAuth2 / DAPS: Client ID"
        }

        vaultEntry(CeVaultEntries.DAPS_CERT)
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_OAUTH_CERTIFICATE_ALIAS
        ) {
            vaultKeyNameFor(CeVaultEntries.DAPS_CERT)
        }

        vaultEntry(CeVaultEntries.DAPS_PRIV)
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_OAUTH_PRIVATE_KEY_ALIAS
        ) {
            vaultKeyNameFor(CeVaultEntries.DAPS_PRIV)
        }
    }
}
