/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contracts_page.services

import de.sovity.edc.ce.api.common.model.PaginationRequest
import de.sovity.edc.ce.api.ui.model.ContractTerminationStatus
import de.sovity.edc.ce.api.ui.model.ContractsPageEntry
import de.sovity.edc.ce.api.ui.model.ContractsPageResult
import de.sovity.edc.ce.api.ui.pages.contract_agreements.services.getDirection
import de.sovity.edc.ce.api.utils.jooq.ListPageBuilder
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation

@Service
class ContractsPageBuilder(
    private val listPageBuilder: ListPageBuilder,
) {

    fun buildContractsPage(
        data: ContractsPageRs,
        pagination: PaginationRequest?
    ): ContractsPageResult {
        val contracts = data.contracts.map { buildContractsPageEntry(it) }
        return ContractsPageResult.builder()
            .contracts(contracts)
            .pagination(
                listPageBuilder.buildPagination(
                    contentSize = contracts.size,
                    totalItems = data.count,
                    pagination = pagination
                )
            )
            .build()
    }

    private fun buildContractsPageEntry(
        agreement: ContractsPageEntryRs
    ): ContractsPageEntry {
        val negotiationType = ContractNegotiation.Type.valueOf(agreement.negotiationType)

        return ContractsPageEntry.builder()
            .contractAgreementId(agreement.contractAgreementId)
            .direction(negotiationType.getDirection())
            .counterPartyId(agreement.negotiationCounterPartyId)
            .contractSigningDate(agreement.contractSigningDate)
            .assetId(agreement.assetId)
            .assetTitle(agreement.assetName)
            .transferProcessesCount(agreement.transferProcessCount)
            .terminationStatus(
                if (agreement.terminatedAt == null)
                    ContractTerminationStatus.ONGOING
                else ContractTerminationStatus.TERMINATED
            )
            .terminatedAt(agreement.terminatedAt)
            .build()
    }
}
