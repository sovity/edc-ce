/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

package de.sovity.edc.ce

import de.sovity.edc.ce.CeRootModule.ceRoot
import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.extension.e2e.junit.IntegrationTest2xCpDpExtension
import de.sovity.edc.extension.e2e.junit.IntegrationTest2xExtension
import de.sovity.edc.runtime.modules.model.ConfigPropRef
import de.sovity.edc.runtime.modules.model.EdcModule

object IntegrationTest2xSetupsCe {

    @JvmStatic
    @JvmOverloads
    fun ceSovityIamMock(
        rootModule: EdcModule = ceRoot(),
        debugConfig: Map<ConfigPropRef, String> = emptyMap()
    ) = IntegrationTest2xExtension(
        rootModule,
        ConfigPresetsCe.sovityIamMock("provider") + debugConfig,
        ConfigPresetsCe.sovityIamMock("consumer") + debugConfig
    )

    @JvmStatic
    @JvmOverloads
    fun ceSovityIamMockCpDp(
        rootModule: EdcModule = ceRoot(),
        debugConfig: Map<ConfigPropRef, String> = emptyMap()
    ) = IntegrationTest2xCpDpExtension(
        rootModule = rootModule,
        providerControlPlaneConfig = ConfigPresetsCe.sovityIamMockCp("provider") + mapOf(
            CeConfigProps.SOVITY_BASE_PATH to "/control",
        ) + debugConfig,
        providerDataPlaneConfig = { cpConfig, cpConfigUtils ->
            ConfigPresetsCe.sovityIamMockDp(cpConfig, cpConfigUtils) + mapOf(
                CeConfigProps.SOVITY_BASE_PATH to "/data",
                CeConfigProps.SOVITY_INTERNAL_CP_BASE_PATH to
                    CeConfigProps.SOVITY_BASE_PATH.getStringOrThrow(cpConfig),
            ) + debugConfig
        },
        consumerControlPlaneConfig = ConfigPresetsCe.sovityIamMockCp("consumer") + mapOf(
            CeConfigProps.SOVITY_BASE_PATH to "/control",
        ) + debugConfig,
        consumerDataPlaneConfig = { cpConfig, cpConfigUtils ->
            ConfigPresetsCe.sovityIamMockDp(cpConfig, cpConfigUtils) + mapOf(
                CeConfigProps.SOVITY_BASE_PATH to "/data",
                CeConfigProps.SOVITY_INTERNAL_CP_BASE_PATH to
                    CeConfigProps.SOVITY_BASE_PATH.getStringOrThrow(cpConfig),
            ) + debugConfig
        },
    )

    @JvmStatic
    @JvmOverloads
    fun ceSphinxViaEnv(
        rootModule: EdcModule = ceRoot(),
        debugConfig: Map<ConfigPropRef, String> = emptyMap()
    ): IntegrationTest2xExtension {
        fun env(name: String): String {
            val value = System.getenv(name) ?: ""
            require (System.getenv("CI_SPHINX_ENABLED") != "true" || value.isNotBlank()) {
                "Missing environment variable $name"
            }
            return value
        }

        return IntegrationTest2xExtension(
            rootModule = rootModule,
            providerConfig = ConfigPresetsCe.sphinxConnector(
                uri = env("CI_SPHINX_PROVIDER_URI"),
                apiKeyId = env("CI_SPHINX_PROVIDER_API_KEY_ID"),
                tokenUrl = env("CI_SPHINX_PROVIDER_TOKEN_URL"),
                issuerDid = env("CI_SPHINX_PROVIDER_ISSUER_DID"),
            ) + debugConfig,
            consumerConfig = ConfigPresetsCe.sphinxConnector(
                uri = env("CI_SPHINX_CONSUMER_URI"),
                apiKeyId = env("CI_SPHINX_CONSUMER_API_KEY_ID"),
                tokenUrl = env("CI_SPHINX_CONSUMER_TOKEN_URL"),
                issuerDid = env("CI_SPHINX_CONSUMER_ISSUER_DID"),
            ) + debugConfig,
        )
    }
}
