/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui

import de.sovity.edc.ce.api.common.model.AssetListPageFilter
import de.sovity.edc.ce.api.common.model.BuildInfo
import de.sovity.edc.ce.api.common.model.UiAsset
import de.sovity.edc.ce.api.common.model.UiAssetCreateRequest
import de.sovity.edc.ce.api.common.model.UiAssetEditRequest
import de.sovity.edc.ce.api.common.model.UiInitiateTransferRequest
import de.sovity.edc.ce.api.ui.model.AssetListPage
import de.sovity.edc.ce.api.ui.model.BusinessPartnerGroupCreateSubmit
import de.sovity.edc.ce.api.ui.model.BusinessPartnerGroupEditSubmit
import de.sovity.edc.ce.api.ui.model.BusinessPartnerGroupQuery
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
import de.sovity.edc.ce.api.ui.model.VaultSecretCreateSubmit
import de.sovity.edc.ce.api.ui.model.VaultSecretEditSubmit
import de.sovity.edc.ce.api.ui.model.VaultSecretQuery
import de.sovity.edc.ce.api.ui.pages.asset.AssetApiService
import de.sovity.edc.ce.api.ui.pages.business_partner_group.BusinessPartnerGroupApiService
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
import de.sovity.edc.ce.api.ui.pages.vault_secret.VaultSecretApiService
import de.sovity.edc.ce.api.utils.ValidatorUtils
import de.sovity.edc.ce.modules.db.jooq.DslContextFactory
import de.sovity.edc.runtime.simple_di.Service

// This class is so large so the generated API Clients can have one UiApi
@Service
@Suppress("LongParameterList")
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
    private val vaultSecretApiService: VaultSecretApiService,
    private val businessPartnerGroupApiService: BusinessPartnerGroupApiService
) : UiResource {

    override fun getDashboardPage(): DashboardPage =
        dslContextFactory.transactionResult {
            dashboardPageApiService.dashboardPage(it)
        }

    override fun assetListPage(assetListPageFilter: AssetListPageFilter): AssetListPage =
        dslContextFactory.transactionResult { dsl ->
            assetApiService.assetListPage(dsl, assetListPageFilter)
        }

    override fun assetDetailsPage(assetId: String): UiAsset =
        dslContextFactory.transactionResult { dsl ->
            assetApiService.assetDetailsPage(dsl, assetId)
        }

    override fun createAsset(uiAssetCreateRequest: UiAssetCreateRequest): IdResponseDto =
        dslContextFactory.transactionResult { dsl ->
            assetApiService.createAsset(
                dsl,
                uiAssetCreateRequest
            )
        }

    override fun editAsset(assetId: String, uiAssetEditRequest: UiAssetEditRequest): IdResponseDto =
        dslContextFactory.transactionResult { dsl ->
            assetApiService.editAsset(
                dsl,
                assetId,
                uiAssetEditRequest
            )
        }

    override fun deleteAsset(assetId: String): IdResponseDto =
        dslContextFactory.transactionResult { dsl ->
            assetApiService.deleteAsset(dsl, assetId)
        }

    override fun createVaultSecret(submitRequest: VaultSecretCreateSubmit) =
        dslContextFactory.transactionResult { dsl ->
            ValidatorUtils.validate(submitRequest)
            vaultSecretApiService.createSubmit(dsl, submitRequest)
        }

    override fun editVaultSecretPage(key: String) =
        dslContextFactory.transactionResult { dsl ->
            vaultSecretApiService.editPage(dsl, key)
        }

    override fun editVaultSecret(
        key: String,
        submitRequest: VaultSecretEditSubmit
    ) =
        dslContextFactory.transactionResult { dsl ->
            ValidatorUtils.validate(submitRequest)
            vaultSecretApiService.editSubmit(dsl, key, submitRequest)
        }

    override fun listVaultSecretsPage(vaultSecretQuery: VaultSecretQuery?) =
        dslContextFactory.transactionResult { dsl ->
            vaultSecretApiService.listPage(dsl, vaultSecretQuery)
        }

    override fun deleteVaultSecret(key: String) =
        dslContextFactory.transactionResult { dsl ->
            vaultSecretApiService.deleteSubmit(dsl, key)
        }

    override fun businessPartnerGroupListPage(businessPartnerGroupQuery: BusinessPartnerGroupQuery?) =
        dslContextFactory.transactionResult { dsl ->
            businessPartnerGroupApiService.listPage(dsl, businessPartnerGroupQuery)
        }

    override fun businessPartnerGroupEditPage(groupId: String) =
        dslContextFactory.transactionResult { dsl ->
            businessPartnerGroupApiService.editPage(dsl, groupId)
        }

    override fun businessPartnerGroupEditSubmit(
        id: String,
        submitRequest: BusinessPartnerGroupEditSubmit
    ) =
        dslContextFactory.transactionResult { dsl ->
            ValidatorUtils.validate(submitRequest)
            businessPartnerGroupApiService.editSubmit(dsl, id, submitRequest)
        }

    override fun businessPartnerGroupCreateSubmit(submitRequest: BusinessPartnerGroupCreateSubmit) =
        dslContextFactory.transactionResult { dsl ->
            ValidatorUtils.validate(submitRequest)
            businessPartnerGroupApiService.createSubmit(dsl, submitRequest)
        }

    override fun deleteBusinessPartnerGroup(id: String) =
        dslContextFactory.transactionResult { dsl ->
            businessPartnerGroupApiService.deleteSubmit(dsl, id)
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

    @Deprecated("")
    override fun getContractDefinitionPage(): ContractDefinitionPage =
        dslContextFactory.transactionResult {
            ContractDefinitionPage(
                contractDefinitionApiService.contractDefinitions
            )
        }

    @Deprecated("")
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

    override fun getCatalogPageDataOffer(
        dataOfferId: String,
        participantId: String,
        connectorEndpoint: String
    ): UiDataOffer =
        dslContextFactory.transactionResult {
            catalogApiService.fetchDataOffer(
                dataOfferId = dataOfferId,
                participantId = participantId,
                connectorEndpoint = connectorEndpoint,
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

    @Deprecated("Use [initiateTransferV2] instead")
    override fun initiateTransfer(request: InitiateTransferRequest): IdResponseDto {
        return dslContextFactory.transactionResult { dsl ->
            contractAgreementTransferApiService.initiateTransfer(
                request
            )
        }
    }

    override fun initiateTransferV2(request: UiInitiateTransferRequest): IdResponseDto {
        return dslContextFactory.transactionResult { dsl ->
            contractAgreementTransferApiService.initiateTransferV2(
                request
            )
        }
    }

    override fun initiateCustomTransfer(request: InitiateCustomTransferRequest): IdResponseDto {
        return dslContextFactory.transactionResult { dsl ->
            contractAgreementTransferApiService.initiateCustomTransfer(request)
        }
    }

    override fun terminateContractAgreement(
        contractAgreementId: String,
        contractTerminationRequest: ContractTerminationRequest
    ): IdResponseDto {
        return dslContextFactory.transactionResult { dsl ->
            ValidatorUtils.validate(contractTerminationRequest)
            contractAgreementTerminationApiService.terminate(
                dsl,
                contractAgreementId,
                contractTerminationRequest
            )
        }
    }

    override fun getTransferHistoryPage(): TransferHistoryPage {
        return dslContextFactory.transactionResult { dsl ->
            TransferHistoryPage(
                transferHistoryPageApiService.getTransferHistoryEntries(dsl)
            )
        }
    }

    override fun getTransferProcessAsset(transferProcessId: String): UiAsset {
        return dslContextFactory.transactionResult { dsl ->
            transferHistoryPageAssetFetcherService.getAssetForTransferHistoryPage(dsl, transferProcessId)
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
