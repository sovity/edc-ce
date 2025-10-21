/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contracts_page.services

import de.sovity.edc.ce.api.ui.model.ContractTerminationStatus
import de.sovity.edc.ce.api.ui.model.ContractsPageRequest
import de.sovity.edc.ce.api.utils.jooq.SearchUtils
import de.sovity.edc.ce.db.jooq.tables.SovityContractTermination
import de.sovity.edc.runtime.simple_di.Service
import org.jooq.Condition
import org.jooq.impl.DSL

@Service
class ContractsPageFilterService {
    fun applyFilters(
        request: ContractsPageRequest,
        fields: ContractsPageFields
    ): Condition = DSL.and(
            applySearch(request.searchText, fields),
            applyTerminationStatusFilter(request, fields.termination)
        )

    private fun applySearch(
        searchText: String?,
        fields: ContractsPageFields
    ): Condition = SearchUtils.simpleSearch(
        searchText, listOf(
            fields.agreement.AGR_ID,
            fields.agreement.ASSET_ID,
            fields.negotiation.COUNTERPARTY_ADDRESS,
            fields.creator
        )
    )

    private fun applyTerminationStatusFilter(
        request: ContractsPageRequest,
        term: SovityContractTermination
    ): Condition = request.terminationStatus?.let {
        when (it) {
            ContractTerminationStatus.ONGOING ->
                term.TERMINATED_AT.isNull

            ContractTerminationStatus.TERMINATED ->
                term.TERMINATED_AT.isNotNull
        }
    } ?: DSL.noCondition()
}
