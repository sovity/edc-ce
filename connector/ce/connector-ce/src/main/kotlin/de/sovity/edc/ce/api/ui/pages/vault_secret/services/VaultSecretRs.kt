/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.vault_secret.services

import de.sovity.edc.ce.api.ui.model.VaultSecretQuery
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.from
import de.sovity.edc.ce.api.utils.jooq.SearchUtils
import de.sovity.edc.ce.db.jooq.Tables
import org.jooq.DSLContext
import java.time.OffsetDateTime

data class VaultSecretRs(
    val key: String,
    val description: String,
    val updatedAt: OffsetDateTime
) {
    companion object {
        fun listVaultSecrets(dsl: DSLContext, vaultSecretQuery: VaultSecretQuery?): List<VaultSecretRs> {
            val vs = Tables.SOVITY_VAULT_SECRET
            return dsl.select(
                VaultSecretRs::key.from { vs.KEY },
                VaultSecretRs::description.from { vs.DESCRIPTION },
                VaultSecretRs::updatedAt.from { vs.UPDATED_AT }
            ).from(vs)
                .where(SearchUtils.simpleSearch(vaultSecretQuery?.searchQuery, listOf(vs.KEY, vs.DESCRIPTION)))
                .orderBy(vs.UPDATED_AT.desc())
                .limit(vaultSecretQuery?.limit)
                .fetchInto(VaultSecretRs::class.java)
        }

        fun getVaultSecret(dsl: DSLContext, key: String): VaultSecretRs? {
            val vs = Tables.SOVITY_VAULT_SECRET
            return dsl.select(
                VaultSecretRs::key.from { vs.KEY },
                VaultSecretRs::description.from { vs.DESCRIPTION },
                VaultSecretRs::updatedAt.from { vs.UPDATED_AT }
            ).from(vs).where(vs.KEY.eq(key)).fetchOneInto(VaultSecretRs::class.java)
        }
    }
}
