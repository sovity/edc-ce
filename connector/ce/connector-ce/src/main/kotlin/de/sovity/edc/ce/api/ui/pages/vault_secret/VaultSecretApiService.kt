/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.vault_secret

import de.sovity.edc.ce.api.ui.model.IdResponseDto
import de.sovity.edc.ce.api.ui.model.VaultSecretCreateSubmit
import de.sovity.edc.ce.api.ui.model.VaultSecretEditPage
import de.sovity.edc.ce.api.ui.model.VaultSecretEditSubmit
import de.sovity.edc.ce.api.ui.model.VaultSecretListPageEntry
import de.sovity.edc.ce.api.ui.model.VaultSecretQuery
import de.sovity.edc.ce.api.ui.pages.vault_secret.services.VaultSecretRs
import de.sovity.edc.ce.api.utils.notFoundError
import de.sovity.edc.ce.db.jooq.Tables
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.spi.security.Vault
import org.jooq.DSLContext
import java.time.OffsetDateTime

@Service
class VaultSecretApiService(
    private val vault: Vault
) {
    fun listPage(dsl: DSLContext, vaultSecretQuery: VaultSecretQuery?) =
        VaultSecretRs.listVaultSecrets(dsl, vaultSecretQuery).map {
            VaultSecretListPageEntry(it.key, it.description, it.updatedAt)
        }

    fun createSubmit(dsl: DSLContext, submitRequest: VaultSecretCreateSubmit): IdResponseDto {
        val vs = Tables.SOVITY_VAULT_SECRET
        dsl.newRecord(vs).also {
            it.key = submitRequest.key
            it.description = submitRequest.description
            it.updatedAt = OffsetDateTime.now()
            it.insert()
        }
        vault.storeSecret(submitRequest.key, submitRequest.value)
        return IdResponseDto(submitRequest.key)
    }

    fun editPage(dsl: DSLContext, key: String) =
        VaultSecretRs.getVaultSecret(dsl, key)?.let { VaultSecretEditPage(it.key, it.description) }
            ?: notFoundError("Vault Secret for key $key not found")

    fun editSubmit(dsl: DSLContext, key: String, submitRequest: VaultSecretEditSubmit): IdResponseDto {
        val vs = Tables.SOVITY_VAULT_SECRET
        val record = dsl.fetchOne(vs, vs.KEY.eq(key))
        record?.let {
            it.description = submitRequest.description
            it.updatedAt = OffsetDateTime.now()
            it.update()
        } ?: notFoundError("Vault Secret for key $key not found")

        if (submitRequest.value != null) {
            vault.storeSecret(key, submitRequest.value)
        }

        return IdResponseDto(key)
    }

    fun deleteSubmit(dsl: DSLContext, key: String): IdResponseDto {
        val vs = Tables.SOVITY_VAULT_SECRET
        dsl.deleteFrom(vs).where(vs.KEY.eq(key)).execute()
        vault.deleteSecret(key)
        return IdResponseDto(key)
    }
}
