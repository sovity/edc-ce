/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.vault.inmemory

import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.runtime.modules.model.ConfigPropRef
import de.sovity.edc.runtime.modules.model.VaultEntry

/**
 * You can use this in tests to safely provide vault secrets via config
 */
fun VaultEntry.toConfigPropRef() = ConfigPropRef(
    property = CeConfigProps.SOVITY_VAULT_IN_MEMORY_INIT_WILDCARD.property.removeSuffix("*") + key,
    defaultDocumentation = "Config key for filling the in-memory vault's key '$key'."
)
