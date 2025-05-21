/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sovity.edrs

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.runtime.modules.model.ConfigPropRef
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.spi.security.Vault
import org.eclipse.edc.spi.system.configuration.Config

@Service
class EdrSecretUtils(
    private val config: Config,
    private val vault: Vault
) {
    /**
     * Many tests have failed because of those missing secrets
     *
     * Let's validate their existence
     */
    fun checkDataPlaneEdrSecrets() {
        requireSecretSet(CeConfigProps.EDC_TRANSFER_PROXY_TOKEN_SIGNER_PRIVATEKEY_ALIAS)
        requireSecretSet(CeConfigProps.EDC_TRANSFER_PROXY_TOKEN_VERIFIER_PUBLICKEY_ALIAS)
    }


    private fun requireSecretSet(property: ConfigPropRef) {
        val key = property.getStringOrNull(config)
        require(!key.isNullOrBlank()) { "Missing required property: '${property.property}' '${property.defaultDocumentation}'" }
        val secret = vault.resolveSecret(key)
        require(!secret.isNullOrBlank()) { "Missing secret: $key as defined in '${property.property}' '${property.defaultDocumentation}'" }
    }
}
