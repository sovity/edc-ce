/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sovity.edrs

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.runtime.modules.model.DocumentedEnum
import org.eclipse.edc.runtime.metamodel.annotation.Inject
import org.eclipse.edc.runtime.metamodel.annotation.Provides
import org.eclipse.edc.spi.monitor.Monitor
import org.eclipse.edc.spi.security.Vault
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext
import org.eclipse.edc.spi.system.configuration.Config

/**
 * Validates the time of negotiation / transfer initiation
 */
@Provides(EdrApiService::class)
class EdrTokenSecretAutogenerationExtension : ServiceExtension {
    @Inject
    private lateinit var vault: Vault

    @Inject
    private lateinit var monitor: Monitor

    private lateinit var config: Config

    private lateinit var secretUtils: SecretUtils

    override fun initialize(context: ServiceExtensionContext) {
        secretUtils = SecretUtils()
        config = context.config
    }

    override fun start() {
        if (requiresTransferProxyKeysGeneration()) {
            initializeTransferProxyKey()
        }
    }

    private fun requiresTransferProxyKeysGeneration(): Boolean =
        CeConfigProps.EDC_TRANSFER_PROXY_TOKEN_SIGNER_PRIVATEKEY_ALIAS.getStringOrNull(config)
            ?.let(vault::resolveSecret) == null ||
            CeConfigProps.EDC_TRANSFER_PROXY_TOKEN_VERIFIER_PUBLICKEY_ALIAS.getStringOrNull(config)
                ?.let(vault::resolveSecret) == null

    private fun initializeTransferProxyKey() {
        val privateKeyAlias =
            CeConfigProps.EDC_TRANSFER_PROXY_TOKEN_SIGNER_PRIVATEKEY_ALIAS.getStringOrThrow(config)

        val certificateKeyAlias =
            CeConfigProps.EDC_TRANSFER_PROXY_TOKEN_VERIFIER_PUBLICKEY_ALIAS.getStringOrThrow(config)

        val participantId = CeConfigProps.EDC_PARTICIPANT_ID.getStringOrThrow(config)

        val method = DocumentedEnum.getSelectedValue<SupportedCertificateMethod>(
            CeConfigProps.SOVITY_CERTIFICATES_GENERATOR_METHOD.getStringOrThrow(config)
        )

        val keys = secretUtils.generateKeysAndCertificate(method, participantId)

        vault.storeSecret(privateKeyAlias, keys.privateKeyPem)
        vault.storeSecret(certificateKeyAlias, keys.certificatePem)

        @Suppress("MaxLineLength")
        monitor.warning("Regenerated keys: private key alias = $privateKeyAlias, certificate alias = $certificateKeyAlias")
    }
}
