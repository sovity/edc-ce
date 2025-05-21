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
import de.sovity.edc.ce.utils.TestKeypairs
import de.sovity.edc.extension.e2e.junit.IntegrationTest2xExtension
import de.sovity.edc.runtime.modules.RuntimeConfigProps
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.UUID.randomUUID

/**
 * This test is the CE because it is referenced from documentation
 */
class ApiWrapperDemoTestIamMock : ApiWrapperDemoTestBase() {
    companion object {
        @RegisterExtension
        val extension = IntegrationTest2xExtension(
            CeRootModule.ceRoot(),
            mapOf(
                RuntimeConfigProps.SOVITY_ENVIRONMENT to "UNIT_TEST",
                CeConfigProps.SOVITY_DEPLOYMENT_KIND to CeControlPlaneModules.withIntegratedDataPlane().name,
                RuntimeConfigProps.SOVITY_FIRST_PORT to "auto",

                // Mock IAM
                CeConfigProps.SOVITY_DATASPACE_KIND to CeDataspace.SOVITY_MOCK_IAM.nameKebabCase,
                CeConfigProps.EDC_PARTICIPANT_ID to "provider",

                // Management API
                CeConfigProps.SOVITY_MANAGEMENT_API_IAM_KIND to ApiKeyAuthModule.instance().name,
                CeConfigProps.EDC_API_AUTH_KEY to randomUUID().toString(),

                // EDR Keys
                CeVaultEntries.TRANSFER_PROXY_PUBLIC.toConfigPropRef() to
                    TestKeypairs.dummyEdrEncryptionKeypair.certificate,
                CeVaultEntries.TRANSFER_PROXY_PRIVATE.toConfigPropRef() to
                    TestKeypairs.dummyEdrEncryptionKeypair.privateKey,
            ),
            mapOf(
                RuntimeConfigProps.SOVITY_ENVIRONMENT to "UNIT_TEST",
                CeConfigProps.SOVITY_DEPLOYMENT_KIND to CeControlPlaneModules.withIntegratedDataPlane().name,
                RuntimeConfigProps.SOVITY_FIRST_PORT to "auto",

                // Mock IAM
                CeConfigProps.SOVITY_DATASPACE_KIND to CeDataspace.SOVITY_MOCK_IAM.nameKebabCase,
                CeConfigProps.EDC_PARTICIPANT_ID to "consumer",

                // Management API
                CeConfigProps.SOVITY_MANAGEMENT_API_IAM_KIND to ApiKeyAuthModule.instance().name,
                CeConfigProps.EDC_API_AUTH_KEY to randomUUID().toString(),

                // EDR Keys
                CeVaultEntries.TRANSFER_PROXY_PUBLIC.toConfigPropRef() to
                    TestKeypairs.dummyEdrEncryptionKeypair.certificate,
                CeVaultEntries.TRANSFER_PROXY_PRIVATE.toConfigPropRef() to
                    TestKeypairs.dummyEdrEncryptionKeypair.privateKey,
            )
        )
    }
}
