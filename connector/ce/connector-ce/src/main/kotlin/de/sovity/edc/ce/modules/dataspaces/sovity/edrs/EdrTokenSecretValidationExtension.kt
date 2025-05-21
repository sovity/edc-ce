/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sovity.edrs

import org.eclipse.edc.runtime.metamodel.annotation.Inject
import org.eclipse.edc.runtime.metamodel.annotation.Provides
import org.eclipse.edc.spi.security.Vault
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext

/**
 * Validates the time of negotiation / transfer initiation
 */
@Provides(EdrApiService::class)
class EdrTokenSecretValidationExtension : ServiceExtension {
    @Inject
    lateinit var vault: Vault

    lateinit var edrSecretUtils: EdrSecretUtils

    override fun initialize(context: ServiceExtensionContext) {
        edrSecretUtils = EdrSecretUtils(context.config, vault)
    }

    override fun start() {
        edrSecretUtils.checkDataPlaneEdrSecrets()
    }
}
