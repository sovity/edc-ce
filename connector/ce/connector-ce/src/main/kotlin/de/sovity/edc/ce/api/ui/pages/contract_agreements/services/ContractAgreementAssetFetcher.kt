/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_agreements.services

import de.sovity.edc.ce.db.jooq.Tables
import de.sovity.edc.runtime.simple_di.Service
import lombok.RequiredArgsConstructor
import org.eclipse.edc.spi.EdcException
import org.jooq.DSLContext

@RequiredArgsConstructor
@Service
class ContractAgreementAssetFetcher {
    fun fetchAssetId(dsl: DSLContext, contractId: String): String {
        val agr = Tables.EDC_CONTRACT_AGREEMENT
        return dsl.select(agr.ASSET_ID).from(agr).where(agr.AGR_ID.eq(contractId)).fetchOne(agr.ASSET_ID)
            ?: throw EdcException("Could not fetch contractAgreement for $contractId")
    }
}
