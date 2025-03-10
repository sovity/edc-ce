/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.vault.inmemory

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.EdcModule

object InMemoryVaultModule {
    fun instance() = EdcModule(
        "vault-in-memory",
        "In-memory vault that can be initialized via env vars"
    ).apply {
        serviceExtensions(InMemoryVaultExtension::class.java)
        property(
            ConfigPropCategory.IMPORTANT,
            CeConfigProps.SOVITY_VAULT_IN_MEMORY_INIT_WILDCARD
        )
    }
}
