/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contracts_page.services

import de.sovity.edc.ce.api.ui.model.ContractsPageRequest
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.from
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.multiset
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.toOffsetDateTimeFromUtcSeconds
import de.sovity.edc.ce.api.utils.jooq.ListPageQueryService
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.impl.DSL

@Service
class ContractsPageQueryService(
    private val listPageQueryService: ListPageQueryService,
    private val contractsPageFilterService: ContractsPageFilterService,
    private val contractsPageSortService: ContractsPageSortService
) {
    fun fetchContractsPage(
        dsl: DSLContext,
        request: ContractsPageRequest
    ): ContractsPageRs {
        val fields = ContractsPageFields()

        return dsl.select(
            ContractsPageRs::contracts from {
                getContractsPageEntries(request, fields)
            },
            ContractsPageRs::count from {
                getTotalContracts(request, fields)
            }
        ).fetchSingleInto(ContractsPageRs::class.java)
    }

    private fun getContractsPageEntries(
        request: ContractsPageRequest,
        fields: ContractsPageFields
    ): Field<List<ContractsPageEntryRs>> {
        val agr = fields.agreement
        val term = fields.termination
        val neg = fields.negotiation
        val a = fields.asset

        return multiset(
            DSL.select(
                ContractsPageEntryRs::contractAgreementId from { agr.AGR_ID },
                ContractsPageEntryRs::contractSigningDate from { agr.SIGNING_DATE.toOffsetDateTimeFromUtcSeconds() },
                ContractsPageEntryRs::transferProcessCount from { fields.transferProcessCount },
                ContractsPageEntryRs::assetId from { agr.ASSET_ID },
                ContractsPageEntryRs::assetName from { fields.assetTitle },
                ContractsPageEntryRs::negotiationType from { neg.TYPE },
                ContractsPageEntryRs::negotiationCounterPartyId from { neg.COUNTERPARTY_ID },
                ContractsPageEntryRs::terminatedAt from { term.TERMINATED_AT },
            ).from(agr)
                .leftJoin(term).on(term.CONTRACT_AGREEMENT_ID.eq(agr.AGR_ID))
                .leftJoin(neg).on(neg.AGREEMENT_ID.eq(agr.AGR_ID))
                .leftJoin(a).on(
                    DSL.and(a.ASSET_ID.eq(agr.ASSET_ID), neg.TYPE.eq(ContractNegotiation.Type.PROVIDER.toString()))
                )
                .where(contractsPageFilterService.applyFilters(request, fields))
                .orderBy(contractsPageSortService.applySort(request, fields))
                .limit(request.pagination?.pageSize)
                .offset(listPageQueryService.getOffset(request.pagination))
        )
    }


    private fun getTotalContracts(
        request: ContractsPageRequest,
        fields: ContractsPageFields
    ): Field<Int> = DSL.field(
        DSL.selectCount()
            .from(fields.agreement)
            .leftJoin(fields.termination).on(fields.termination.CONTRACT_AGREEMENT_ID.eq(fields.agreement.AGR_ID))
            .leftJoin(fields.negotiation).on(fields.negotiation.AGREEMENT_ID.eq(fields.agreement.AGR_ID))
            .leftJoin(fields.asset).on(fields.asset.ASSET_ID.eq(fields.agreement.ASSET_ID))
            .where(contractsPageFilterService.applyFilters(request, fields))
    )
}
