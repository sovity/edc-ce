/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_detail_page

import de.sovity.edc.ce.api.ui.model.ContractDetailPageResult
import de.sovity.edc.ce.api.ui.pages.contract_detail_page.services.ContractDetailPageBuilder
import de.sovity.edc.ce.api.ui.pages.contract_detail_page.services.ContractDetailPageQueryService
import de.sovity.edc.ce.api.utils.notFoundError
import de.sovity.edc.runtime.simple_di.Service
import org.jooq.DSLContext

@Service
class ContractDetailPageApiService(
    private val contractDetailPageQueryService: ContractDetailPageQueryService,
    private val contractDetailPageBuilder: ContractDetailPageBuilder,
) {
    fun contractDetailPage(dsl: DSLContext, contractAgreementId: String): ContractDetailPageResult {
        val agreement =
            contractDetailPageQueryService.fetchContractDetailPage(dsl, contractAgreementId)
                ?: notFoundError("Contract Agreement with ID $contractAgreementId not found.")
        return contractDetailPageBuilder.buildContractDetailPage(agreement)
    }
}
