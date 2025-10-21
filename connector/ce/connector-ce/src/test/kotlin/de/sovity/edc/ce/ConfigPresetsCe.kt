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
import de.sovity.edc.ce.config.CeVaultEntries
import de.sovity.edc.ce.modules.auth.ApiKeyAuthModule
import de.sovity.edc.ce.modules.vault.inmemory.toConfigPropRef
import de.sovity.edc.runtime.modules.RuntimeConfigProps
import de.sovity.edc.runtime.modules.model.ConfigPropRef
import org.eclipse.edc.spi.system.configuration.Config
import java.util.UUID.randomUUID

object ConfigPresetsCe {
    @JvmOverloads
    @JvmStatic
    fun sovityIamMock(
        participantId: String = "edc",
        overrides: Map<ConfigPropRef, String> = emptyMap(),
    ) = defaultControlPlane("control-plane-with-integrated-data-plane") + mapOf(
        // Mock IAM
        CeConfigProps.SOVITY_DATASPACE_KIND to CeDataspace.SOVITY_MOCK_IAM.nameKebabCase,
        CeConfigProps.EDC_PARTICIPANT_ID to participantId,
    ) + overrides

    @JvmStatic
    fun sovityIamMockCp(
        participantId: String = "edc-control-plane",
        overrides: Map<ConfigPropRef, String> = emptyMap(),
    ) = defaultControlPlane("control-plane-standalone") + mapOf(
        // Mock IAM
        CeConfigProps.SOVITY_DATASPACE_KIND to CeDataspace.SOVITY_MOCK_IAM.nameKebabCase,
        CeConfigProps.EDC_PARTICIPANT_ID to participantId,
    ) + overrides

    @JvmStatic
    fun sovityIamMockDp(
        cpConfig: Config,
        overrides: Map<ConfigPropRef, String> = emptyMap(),
    ) = defaultDataPlane(cpConfig) + mapOf(
        // Mock IAM
        CeConfigProps.SOVITY_DATASPACE_KIND to CeDataspace.SOVITY_MOCK_IAM.nameKebabCase,
    ) + overrides

    fun sphinxConnector(
        uri: String,
        apiKeyId: String,
        tokenUrl: String,
        issuerDid: String
    ): Map<ConfigPropRef, String> =
        defaultControlPlane("control-plane-with-integrated-data-plane") + mapOf(
            // sphin-X
            CeConfigProps.SOVITY_DATASPACE_KIND to CeDataspace.SPHIN_X.nameKebabCase,
            CeConfigProps.EDC_PARTICIPANT_ID to uri,
            CeConfigProps.EDC_IAM_ISSUER_ID to uri,
            CeConfigProps.EDC_IAM_STS_OAUTH_TOKEN_URL to tokenUrl,
            CeConfigProps.EDC_IAM_TRUSTED_ISSUER_SPHINX_ID to issuerDid,
            CeConfigProps.EDC_IAM_STS_OAUTH_CLIENT_ID to apiKeyId.split(":").first(),
            CeVaultEntries.STS_CLIENT_SECRET.toConfigPropRef() to apiKeyId.split(":").last(),
        )


    fun defaultControlPlane(
        deploymentKind: String,
    ): Map<ConfigPropRef, String> = mapOf(
        RuntimeConfigProps.SOVITY_ENVIRONMENT to "UNIT_TEST",
        CeConfigProps.SOVITY_DEPLOYMENT_KIND to deploymentKind,
        RuntimeConfigProps.SOVITY_FIRST_PORT to "auto",

        // Management API
        CeConfigProps.SOVITY_MANAGEMENT_API_IAM_KIND to ApiKeyAuthModule.instance().name,
        CeConfigProps.EDC_API_AUTH_KEY to randomUUID().toString(),
    )

    private fun defaultDataPlane(
        cpConfig: Config
    ): Map<ConfigPropRef, String> = mapOf(
        RuntimeConfigProps.SOVITY_ENVIRONMENT to "UNIT_TEST",
        CeConfigProps.SOVITY_DEPLOYMENT_KIND to CeDataPlaneModules.standalone().name,
        RuntimeConfigProps.SOVITY_FIRST_PORT to "auto",

        // Management API
        CeConfigProps.SOVITY_MANAGEMENT_API_IAM_KIND to ApiKeyAuthModule.instance().name,
        CeConfigProps.EDC_API_AUTH_KEY to randomUUID().toString(),

        // Data Plane Registration
        CeConfigProps.SOVITY_INTERNAL_CP_FIRST_PORT to RuntimeConfigProps.SOVITY_FIRST_PORT.getStringOrThrow(cpConfig),
    )
}
