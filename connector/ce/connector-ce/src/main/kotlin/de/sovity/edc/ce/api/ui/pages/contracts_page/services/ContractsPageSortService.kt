/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contracts_page.services

import de.sovity.edc.ce.api.ui.model.ContractsPageRequest
import de.sovity.edc.ce.api.ui.model.ContractsPageSortProperty
import de.sovity.edc.ce.api.utils.jooq.ListPageQueryService
import de.sovity.edc.runtime.simple_di.Service
import org.jooq.SortField

@Service
class ContractsPageSortService(
    private val listPageQueryService: ListPageQueryService
) {
    fun applySort(
        request: ContractsPageRequest,
        fields: ContractsPageFields
    ): List<SortField<out Any>> = request.sortBy?.map {
        val field = when (it.field) {
            ContractsPageSortProperty.CONTRACT_NAME -> fields.agreement.ASSET_ID
            ContractsPageSortProperty.SIGNED_AT -> fields.agreement.SIGNING_DATE
            ContractsPageSortProperty.TERMINATED_AT -> fields.termination.TERMINATED_AT
            ContractsPageSortProperty.TRANSFERS -> fields.transferProcessCount
        }
        listPageQueryService.withSortDirection(field, it.direction)
    } ?: emptyList()

}
