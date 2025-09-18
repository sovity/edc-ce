/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sphinx

import de.sovity.edc.ce.api.ui.model.UiConfigFeature
import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.ce.config.CeVaultEntries
import de.sovity.edc.ce.dependency_bundles.CeDependencyBundles
import de.sovity.edc.ce.modules.dataspaces.catena.CatenaDataspaceFeatures.configureTxMembershipCredential
import de.sovity.edc.ce.modules.dataspaces.sovity.edrs.EdrApiServiceV3GenericExtension
import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.EdcModule
import org.eclipse.tractusx.edc.iam.iatp.IatpIdentityExtension

object SphinxDataspaceFeatures {

    fun sphinxControlPlane() = EdcModule(
        name = "sphinx-control-plane",
        documentation = "Enable Sphin-X Extensions and Sphin-X C2C IAM"
    ).apply {
        dependencyBundles(CeDependencyBundles.sphinxControlPlane)
        serviceExtensions(SphinxDidPolicyExtension::class.java)

        configureIatp()
        serviceExtensions(EdrApiServiceV3GenericExtension::class.java)

        // UI Features
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_EDC_UI_FEATURES_ADD_WILDCARD.withWildcardValue(UiConfigFeature.SPHINX_POLICIES.name)
        ) {
            defaultValue("true")
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.SOVITY_EDC_UI_FEATURES_ADD_WILDCARD.withWildcardValue(UiConfigFeature.SPHINX_ASSET_METADATA.name)
        ) {
            defaultValue("true")
        }
    }

    private fun EdcModule.configureIatp() {
        // This disables the identity from being extracted from the BPN / "holderIdentifier" (the default is the DID)
        excludeServiceExtensions(IatpIdentityExtension::class.java)

        vaultEntry(CeVaultEntries.STS_CLIENT_SECRET)
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.EDC_IAM_ISSUER_ID
        ) {
            required()
            documentation += "\nIn sphinx this is the Connector URI/DID"
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.EDC_IAM_STS_OAUTH_CLIENT_ID
        ) {
            required()
            documentation += "\nIn sphin-X with spherity wallets this is the first component before the `:` of the `apiKeyId`." +
                " The second component is then the API Key and must be input into the vault."
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.EDC_IAM_STS_OAUTH_TOKEN_URL
        ) {
            required()
            documentation += "\nSTS API Endpoint on the Wallet"
        }
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.EDC_IAM_TRUSTED_ISSUER_SPHINX_ID
        ) {
            required()
            documentation += "\nThis is the DID of the issuer used for this sphinx dataspace environment"
        }

        // defaults
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_PARTICIPANT_ID
        ) {
            defaultFromProp(CeConfigProps.EDC_IAM_ISSUER_ID)
            documentation += "\nIn sphinx this is the Connector URI/DID"
        }
        property(
            ConfigPropCategory.OVERRIDES,
            CeConfigProps.EDC_IAM_STS_OAUTH_CLIENT_SECRET_ALIAS
        ) {
            defaultValue(CeVaultEntries.STS_CLIENT_SECRET.key)
        }
        configureTxMembershipCredential()
    }
}
