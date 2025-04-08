/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui

import de.sovity.edc.ce.api.common.model.BuildInfo
import de.sovity.edc.ce.api.common.model.UiAsset
import de.sovity.edc.ce.api.common.model.UiAssetCreateRequest
import de.sovity.edc.ce.api.common.model.UiAssetEditRequest
import de.sovity.edc.ce.api.ui.model.AssetPage
import de.sovity.edc.ce.api.ui.model.ContractAgreementCard
import de.sovity.edc.ce.api.ui.model.ContractAgreementPage
import de.sovity.edc.ce.api.ui.model.ContractAgreementPageQuery
import de.sovity.edc.ce.api.ui.model.ContractDefinitionPage
import de.sovity.edc.ce.api.ui.model.ContractDefinitionRequest
import de.sovity.edc.ce.api.ui.model.ContractNegotiationRequest
import de.sovity.edc.ce.api.ui.model.ContractTerminationRequest
import de.sovity.edc.ce.api.ui.model.DashboardPage
import de.sovity.edc.ce.api.ui.model.DataOfferCreateRequest
import de.sovity.edc.ce.api.ui.model.IdAvailabilityResponse
import de.sovity.edc.ce.api.ui.model.IdResponseDto
import de.sovity.edc.ce.api.ui.model.InitiateCustomTransferRequest
import de.sovity.edc.ce.api.ui.model.InitiateTransferRequest
import de.sovity.edc.ce.api.ui.model.PolicyDefinitionCreateDto
import de.sovity.edc.ce.api.ui.model.PolicyDefinitionCreateRequest
import de.sovity.edc.ce.api.ui.model.PolicyDefinitionPage
import de.sovity.edc.ce.api.ui.model.TransferHistoryPage
import de.sovity.edc.ce.api.ui.model.UiConfig
import de.sovity.edc.ce.api.ui.model.UiContractNegotiation
import de.sovity.edc.ce.api.ui.model.UiDataOffer
import de.sovity.edc.ce.api.ui.pages.asset.AssetApiService
import de.sovity.edc.ce.api.ui.pages.catalog.CatalogApiService
import de.sovity.edc.ce.api.ui.pages.config.UiConfigApiService
import de.sovity.edc.ce.api.ui.pages.contract_agreements.ContractAgreementPageApiService
import de.sovity.edc.ce.api.ui.pages.contract_agreements.ContractAgreementTerminationApiService
import de.sovity.edc.ce.api.ui.pages.contract_agreements.ContractAgreementTransferApiService
import de.sovity.edc.ce.api.ui.pages.contract_definitions.ContractDefinitionApiService
import de.sovity.edc.ce.api.ui.pages.contract_negotiations.ContractNegotiationApiService
import de.sovity.edc.ce.api.ui.pages.dashboard.DashboardPageApiService
import de.sovity.edc.ce.api.ui.pages.dashboard.services.VersionsService
import de.sovity.edc.ce.api.ui.pages.data_offer.DataOfferPageApiService
import de.sovity.edc.ce.api.ui.pages.policy.PolicyDefinitionApiService
import de.sovity.edc.ce.api.ui.pages.transferhistory.TransferHistoryPageApiService
import de.sovity.edc.ce.api.ui.pages.transferhistory.TransferHistoryPageAssetFetcherService
import de.sovity.edc.ce.modules.db.DslContextFactory
import de.sovity.edc.runtime.simple_di.Service
import lombok.RequiredArgsConstructor
import org.jooq.DSLContext

// This class is so large so the generated API Clients can have one UiApi
@RequiredArgsConstructor
@Service
class UiResourceImpl(
    private val assetApiService: AssetApiService,
    private val catalogApiService: CatalogApiService,
    private val contractAgreementApiService: ContractAgreementPageApiService,
    private val contractAgreementTerminationApiService: ContractAgreementTerminationApiService,
    private val contractAgreementTransferApiService: ContractAgreementTransferApiService,
    private val contractDefinitionApiService: ContractDefinitionApiService,
    private val contractNegotiationApiService: ContractNegotiationApiService,
    private val dashboardPageApiService: DashboardPageApiService,
    private val dataOfferPageApiService: DataOfferPageApiService,
    private val dslContextFactory: DslContextFactory,
    private val uiConfigApiService: UiConfigApiService,
    private val versionsService: VersionsService,
    private val policyDefinitionApiService: PolicyDefinitionApiService,
    private val transferHistoryPageApiService: TransferHistoryPageApiService,
    private val transferHistoryPageAssetFetcherService: TransferHistoryPageAssetFetcherService,
) : UiResource {

    override fun getDashboardPage(): DashboardPage =
        dslContextFactory.transactionResult {
            dashboardPageApiService.dashboardPage()
        }

    override fun getAssetPage(): AssetPage =
        dslContextFactory.transactionResult {
            AssetPage(
                assetApiService.assets
            )
        }

    override fun createAsset(uiAssetCreateRequest: UiAssetCreateRequest): IdResponseDto =
        dslContextFactory.transactionResult {
            assetApiService.createAsset(
                uiAssetCreateRequest
            )
        }

    override fun editAsset(assetId: String, uiAssetEditRequest: UiAssetEditRequest): IdResponseDto =
        dslContextFactory.transactionResult {
            assetApiService.editAsset(
                assetId,
                uiAssetEditRequest
            )
        }

    override fun deleteAsset(assetId: String): IdResponseDto =
        dslContextFactory.transactionResult {
            assetApiService.deleteAsset(assetId)
        }

    override fun getPolicyDefinitionPage(): PolicyDefinitionPage =
        dslContextFactory.transactionResult {
            PolicyDefinitionPage(
                policyDefinitionApiService.policyDefinitions
            )
        }

    @Deprecated("Use [createPolicyDefinitionV2] instead")
    override fun createPolicyDefinition(policyDefinitionDtoDto: PolicyDefinitionCreateRequest): IdResponseDto =
        dslContextFactory.transactionResult {
            policyDefinitionApiService.createPolicyDefinition(
                policyDefinitionDtoDto
            )
        }

    override fun createPolicyDefinitionV2(policyDefinitionCreateDto: PolicyDefinitionCreateDto): IdResponseDto =
        dslContextFactory.transactionResult {
            policyDefinitionApiService.createPolicyDefinitionV2(
                policyDefinitionCreateDto
            )
        }

    override fun deletePolicyDefinition(policyId: String): IdResponseDto =
        dslContextFactory.transactionResult {
            policyDefinitionApiService.deletePolicyDefinition(
                policyId
            )
        }

    override fun getContractDefinitionPage(): ContractDefinitionPage =
        dslContextFactory.transactionResult {
            ContractDefinitionPage(
                contractDefinitionApiService.contractDefinitions
            )
        }

    override fun createContractDefinition(contractDefinitionRequest: ContractDefinitionRequest): IdResponseDto =
        dslContextFactory.transactionResult {
            contractDefinitionApiService.createContractDefinition(
                contractDefinitionRequest
            )
        }

    override fun deleteContractDefinition(contractDefinitionId: String): IdResponseDto =
        dslContextFactory.transactionResult {
            contractDefinitionApiService.deleteContractDefinition(
                contractDefinitionId
            )
        }

    override fun createDataOffer(dataOfferCreateRequest: DataOfferCreateRequest): IdResponseDto =
        dslContextFactory.transactionResult { dsl ->
            dataOfferPageApiService.createDataOffer(
                dsl,
                dataOfferCreateRequest
            )
        }

    override fun getCatalogPageDataOffers(participantId: String, connectorEndpoint: String): List<UiDataOffer> =
        dslContextFactory.transactionResult {
            catalogApiService.fetchDataOffers(
                participantId,
                connectorEndpoint
            )
        }

    override fun initiateContractNegotiation(
        contractNegotiationRequest: ContractNegotiationRequest
    ): UiContractNegotiation =
        dslContextFactory.transactionResult {
            contractNegotiationApiService.initiateContractNegotiation(
                contractNegotiationRequest
            )
        }

    override fun getContractNegotiation(contractNegotiationId: String): UiContractNegotiation =
        dslContextFactory.transactionResult {
            contractNegotiationApiService.getContractNegotiation(
                contractNegotiationId
            )
        }

    override fun getContractAgreementPage(
        contractAgreementPageQuery: ContractAgreementPageQuery?
    ): ContractAgreementPage =
        dslContextFactory.transactionResult { dsl ->
            contractAgreementApiService.contractAgreementPage(
                dsl,
                contractAgreementPageQuery
            )
        }

    override fun getContractAgreementCard(contractAgreementId: String): ContractAgreementCard =
        dslContextFactory.transactionResult { dsl ->
            contractAgreementApiService.contractAgreement(
                dsl,
                contractAgreementId
            )
        }

    override fun initiateTransfer(request: InitiateTransferRequest): IdResponseDto {
        return dslContextFactory.transactionResult { dsl ->
            contractAgreementTransferApiService.initiateTransfer(
                request
            )
        }
    }

    override fun initiateCustomTransfer(request: InitiateCustomTransferRequest): IdResponseDto {
        return dslContextFactory.transactionResult { dsl ->
            contractAgreementTransferApiService.initiateCustomTransfer(
                request
            )
        }
    }

    override fun terminateContractAgreement(
        contractAgreementId: String,
        contractTerminationRequest: ContractTerminationRequest
    ): IdResponseDto {
        return dslContextFactory.transactionResult { dsl ->
            contractAgreementTerminationApiService.terminate(
                dsl,
                contractAgreementId,
                contractTerminationRequest
            )
        }
    }

    override fun getTransferHistoryPage(): TransferHistoryPage {
        return dslContextFactory.transactionResult { unused: DSLContext? ->
            TransferHistoryPage(
                transferHistoryPageApiService.transferHistoryEntries
            )
        }
    }

    override fun getTransferProcessAsset(transferProcessId: String): UiAsset {
        return dslContextFactory.transactionResult { dsl ->
            transferHistoryPageAssetFetcherService.getAssetForTransferHistoryPage(
                transferProcessId
            )
        }
    }

    override fun isPolicyIdAvailable(policyId: String): IdAvailabilityResponse {
        return dslContextFactory.transactionResult { dsl ->
            dataOfferPageApiService.checkIfPolicyIdAvailable(
                dsl,
                policyId
            )
        }
    }

    override fun isAssetIdAvailable(assetId: String): IdAvailabilityResponse {
        return dslContextFactory.transactionResult { dsl ->
            dataOfferPageApiService.checkIfAssetIdAvailable(
                dsl,
                assetId
            )
        }
    }

    override fun isContractDefinitionIdAvailable(contractDefinitionId: String): IdAvailabilityResponse {
        return dslContextFactory.transactionResult { dsl ->
            dataOfferPageApiService.checkIfContractDefinitionIdAvailable(
                dsl,
                contractDefinitionId
            )
        }
    }

    override fun buildInfo(): BuildInfo =
        versionsService.versions

    override fun uiConfig(): UiConfig =
        uiConfigApiService.uiConfig()
}
