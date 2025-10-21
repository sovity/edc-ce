/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_detail_page.services

import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.from
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.multiset
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.toOffsetDateTimeFromUtcSeconds
import de.sovity.edc.ce.db.jooq.Tables
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.impl.DSL

@Service
class ContractDetailPageQueryService {
    fun fetchContractDetailPage(
        dsl: DSLContext,
        agreementId: String
    ): ContractDetailPageRs? {
        val agr = Tables.EDC_CONTRACT_AGREEMENT
        val term = Tables.SOVITY_CONTRACT_TERMINATION
        val neg = Tables.EDC_CONTRACT_NEGOTIATION
        val a = Tables.EDC_ASSET
        return dsl.select(
            ContractDetailPageRs::contractAgreementId from { agr.AGR_ID },
            ContractDetailPageRs::contractSigningDate from { agr.SIGNING_DATE.toOffsetDateTimeFromUtcSeconds() },
            ContractDetailPageRs::transferProcesses from { getTransferProcesses(agr.AGR_ID) },
            ContractDetailPageRs::policy from { agr.POLICY },
            ContractDetailPageRs::negotiationId from { neg.ID },
            ContractDetailPageRs::negotiationType from { neg.TYPE },
            ContractDetailPageRs::negotiationCounterPartyId from { neg.COUNTERPARTY_ID },
            ContractDetailPageRs::negotiationCounterPartyAddress from { neg.COUNTERPARTY_ADDRESS },
            ContractDetailPageRs::terminatedAt from { term.TERMINATED_AT },
            ContractDetailPageRs::terminatedBy from { term.TERMINATED_BY },
            ContractDetailPageRs::terminationReason from { term.REASON },
            ContractDetailPageRs::terminationDetail from { term.DETAIL },

            ContractDetailPageRs::assetId from { agr.ASSET_ID },
            ContractDetailPageRs::assetProperties from { a.PROPERTIES },
            ContractDetailPageRs::assetCreatedAt from { a.CREATED_AT },
            ContractDetailPageRs::assetProperties from { a.PROPERTIES },
            ContractDetailPageRs::assetPrivateProperties from { a.PRIVATE_PROPERTIES },
            ContractDetailPageRs::assetDataAddress from { a.DATA_ADDRESS },
        ).from(agr)
            .leftJoin(term).on(term.CONTRACT_AGREEMENT_ID.eq(agr.AGR_ID))
            .leftJoin(neg).on(neg.AGREEMENT_ID.eq(agr.AGR_ID))
            .leftJoin(a).on(
                DSL.and(a.ASSET_ID.eq(agr.ASSET_ID), neg.TYPE.eq(ContractNegotiation.Type.PROVIDER.toString()))
            )
            .where(
                agr.AGR_ID.eq(agreementId)
            ).fetchOneInto(ContractDetailPageRs::class.java)
    }


    private fun getTransferProcesses(
        contractId: Field<String>,
    ): Field<List<ContractDetailPageTransferProcessRs>> {
        val t = Tables.EDC_TRANSFER_PROCESS
        val query = DSL.select(
            ContractDetailPageTransferProcessRs::transferProcessId from { t.TRANSFERPROCESS_ID },
            ContractDetailPageTransferProcessRs::updatedAt from { t.UPDATED_AT },
            ContractDetailPageTransferProcessRs::state from { t.STATE },
            ContractDetailPageTransferProcessRs::errorDetail from { t.ERROR_DETAIL }
        ).from(t).where(
            t.CONTRACT_ID.eq(contractId)
        )
        return multiset(query)
    }
}
