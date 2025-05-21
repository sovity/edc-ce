/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase

import de.sovity.edc.ce.api.common.model.CatalogQueryV2
import de.sovity.edc.ce.api.common.model.EdrDto
import de.sovity.edc.ce.api.ui.model.IdResponseDto
import de.sovity.edc.ce.api.ui.model.UiDataOffer
import de.sovity.edc.ce.api.usecase.model.CatalogQuery
import de.sovity.edc.ce.api.usecase.model.ContractNegotiationStateResult
import de.sovity.edc.ce.api.usecase.model.KpiResult
import de.sovity.edc.ce.api.usecase.model.NegotiateAllQuery
import de.sovity.edc.ce.api.usecase.model.NegotiateAllResult
import de.sovity.edc.ce.api.usecase.model.TransferProcessStateResult
import de.sovity.edc.ce.api.usecase.services.KpiApiService
import de.sovity.edc.ce.api.usecase.services.SupportedPolicyApiService
import de.sovity.edc.ce.api.usecase.services.catalog.UseCaseCatalogV1ApiService
import de.sovity.edc.ce.api.usecase.services.catalog.UseCaseCatalogV2ApiService
import de.sovity.edc.ce.api.usecase.services.negotiation.UseCaseNegotiateAllApiService
import de.sovity.edc.ce.api.usecase.services.negotiation.UseCaseNegotiationStateApiService
import de.sovity.edc.ce.api.usecase.services.transfers.UseCaseTransferStateApiService
import de.sovity.edc.ce.modules.dataspaces.sovity.edrs.EdrApiService
import de.sovity.edc.ce.modules.db.jooq.DslContextFactory
import de.sovity.edc.runtime.simple_di.Service

/**
 * Provides the endpoints for use-case specific requests.
 */
@Service
class UseCaseResourceImpl(
    private val kpiApiService: KpiApiService,
    private val supportedPolicyApiService: SupportedPolicyApiService,
    private val useCaseCatalogV1ApiService: UseCaseCatalogV1ApiService,
    private val edrApiService: EdrApiService,
    private val dslContextFactory: DslContextFactory,
    private val useCaseNegotiationStateApiService: UseCaseNegotiationStateApiService,
    private val useCaseTransferStateApiService: UseCaseTransferStateApiService,
    private val useCaseNegotiateAllApiService: UseCaseNegotiateAllApiService,
    private val catalogV2ApiService: UseCaseCatalogV2ApiService
) : UseCaseResource {

    override fun getKpis(): KpiResult =
        dslContextFactory.transactionResult { dsl ->
            kpiApiService.getKpis(dsl)
        }

    override fun getSupportedFunctions(): List<String> =
        dslContextFactory.transactionResult { dsl ->
            supportedPolicyApiService.supportedFunctions
        }

    override fun queryCatalog(catalogQuery: CatalogQuery): List<UiDataOffer> =
        dslContextFactory.transactionResult { dsl ->
            useCaseCatalogV1ApiService.fetchDataOffers(
                catalogQuery
            )
        }

    override fun queryCatalogV2(catalogQuery: CatalogQueryV2): List<UiDataOffer> =
        dslContextFactory.transactionResult { dsl ->
            catalogV2ApiService.fetchDataOffers(catalogQuery)
        }

    override fun negotiateAll(negotiateAllQuery: NegotiateAllQuery): List<NegotiateAllResult> =
        dslContextFactory.transactionResult { dsl ->
            useCaseNegotiateAllApiService.negotiateAll(dsl, negotiateAllQuery)
        }

    override fun getContractNegotiationStates(negotiationIds: List<String>): List<ContractNegotiationStateResult> =
        dslContextFactory.transactionResult { dsl ->
            useCaseNegotiationStateApiService.getContractNegotiationStates(dsl, negotiationIds)
        }

    override fun getTransferProcessStates(transferIds: List<String>): List<TransferProcessStateResult> =
        dslContextFactory.transactionResult { dsl ->
            useCaseTransferStateApiService.getTransferProcessStates(dsl, transferIds)
        }

    override fun getTransferProcessEdr(transferId: String): EdrDto =
        dslContextFactory.transactionResult { dsl ->
            edrApiService.getTransferProcessEdr(transferId)
        }

    override fun terminateTransferProcess(transferId: String): IdResponseDto =
        dslContextFactory.transactionResult { dsl ->
            edrApiService.terminateTransferProcess(transferId)
        }
}
