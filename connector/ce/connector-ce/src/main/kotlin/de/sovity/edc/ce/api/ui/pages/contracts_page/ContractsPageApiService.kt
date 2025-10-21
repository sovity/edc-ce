/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contracts_page

import de.sovity.edc.ce.api.ui.model.ContractsPageRequest
import de.sovity.edc.ce.api.ui.model.ContractsPageResult
import de.sovity.edc.ce.api.ui.pages.contracts_page.services.ContractsPageBuilder
import de.sovity.edc.ce.api.ui.pages.contracts_page.services.ContractsPageQueryService
import de.sovity.edc.runtime.simple_di.Service
import org.jooq.DSLContext

@Service
class ContractsPageApiService(
    private val contractsPageQueryService: ContractsPageQueryService,
    private val contractsPageBuilder: ContractsPageBuilder,
) {
    fun contractsPage(
        dsl: DSLContext,
        request: ContractsPageRequest
    ): ContractsPageResult {
        val agreementPage = contractsPageQueryService.fetchContractsPage(dsl, request)
        return contractsPageBuilder.buildContractsPage(agreementPage, request.pagination)
    }
}
