/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui;

import de.sovity.edc.ce.api.common.model.BuildInfo;
import de.sovity.edc.ce.api.common.model.UiAsset;
import de.sovity.edc.ce.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ce.api.common.model.UiAssetEditRequest;
import de.sovity.edc.ce.api.ui.model.AssetPage;
import de.sovity.edc.ce.api.ui.model.ContractAgreementCard;
import de.sovity.edc.ce.api.ui.model.ContractAgreementPage;
import de.sovity.edc.ce.api.ui.model.ContractAgreementPageQuery;
import de.sovity.edc.ce.api.ui.model.ContractDefinitionPage;
import de.sovity.edc.ce.api.ui.model.ContractDefinitionRequest;
import de.sovity.edc.ce.api.ui.model.ContractNegotiationRequest;
import de.sovity.edc.ce.api.ui.model.ContractTerminationRequest;
import de.sovity.edc.ce.api.ui.model.DashboardPage;
import de.sovity.edc.ce.api.ui.model.DataOfferCreateRequest;
import de.sovity.edc.ce.api.ui.model.IdAvailabilityResponse;
import de.sovity.edc.ce.api.ui.model.IdResponseDto;
import de.sovity.edc.ce.api.ui.model.InitiateCustomTransferRequest;
import de.sovity.edc.ce.api.ui.model.InitiateTransferRequest;
import de.sovity.edc.ce.api.ui.model.PolicyDefinitionCreateDto;
import de.sovity.edc.ce.api.ui.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.ce.api.ui.model.PolicyDefinitionPage;
import de.sovity.edc.ce.api.ui.model.TransferHistoryPage;
import de.sovity.edc.ce.api.ui.model.UiContractNegotiation;
import de.sovity.edc.ce.api.ui.model.UiDataOffer;
import de.sovity.edc.ce.api.ui.pages.asset.AssetApiService;
import de.sovity.edc.ce.api.ui.pages.catalog.CatalogApiService;
import de.sovity.edc.ce.api.ui.pages.contract_agreements.ContractAgreementPageApiService;
import de.sovity.edc.ce.api.ui.pages.contract_agreements.ContractAgreementTerminationApiService;
import de.sovity.edc.ce.api.ui.pages.contract_agreements.ContractAgreementTransferApiService;
import de.sovity.edc.ce.api.ui.pages.contract_definitions.ContractDefinitionApiService;
import de.sovity.edc.ce.api.ui.pages.contract_negotiations.ContractNegotiationApiService;
import de.sovity.edc.ce.api.ui.pages.dashboard.DashboardPageApiService;
import de.sovity.edc.ce.api.ui.pages.dashboard.services.VersionsService;
import de.sovity.edc.ce.api.ui.pages.data_offer.DataOfferPageApiService;
import de.sovity.edc.ce.api.ui.pages.policy.PolicyDefinitionApiService;
import de.sovity.edc.ce.api.ui.pages.transferhistory.TransferHistoryPageApiService;
import de.sovity.edc.ce.api.ui.pages.transferhistory.TransferHistoryPageAssetFetcherService;
import de.sovity.edc.ce.modules.db.DslContextFactory;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("java:S6539") // This class is so large so the generated API Clients can have one UiApi
@RequiredArgsConstructor
@Service
public class UiResourceImpl implements UiResource {

    private final AssetApiService assetApiService;
    private final CatalogApiService catalogApiService;
    private final ContractAgreementPageApiService contractAgreementApiService;
    private final ContractAgreementTerminationApiService contractAgreementTerminationApiService;
    private final ContractAgreementTransferApiService contractAgreementTransferApiService;
    private final ContractDefinitionApiService contractDefinitionApiService;
    private final ContractNegotiationApiService contractNegotiationApiService;
    private final DashboardPageApiService dashboardPageApiService;
    private final DataOfferPageApiService dataOfferPageApiService;
    private final DslContextFactory dslContextFactory;
    private final VersionsService versionsService;
    private final PolicyDefinitionApiService policyDefinitionApiService;
    private final TransferHistoryPageApiService transferHistoryPageApiService;
    private final TransferHistoryPageAssetFetcherService transferHistoryPageAssetFetcherService;

    @Override
    public DashboardPage getDashboardPage() {
        return dslContextFactory.transactionResult(dsl -> dashboardPageApiService.dashboardPage());
    }

    @Override
    public AssetPage getAssetPage() {
        return dslContextFactory.transactionResult(dsl -> new AssetPage(assetApiService.getAssets()));
    }

    @Override
    public IdResponseDto createAsset(UiAssetCreateRequest uiAssetCreateRequest) {
        return dslContextFactory.transactionResult(dsl -> assetApiService.createAsset(uiAssetCreateRequest));
    }

    @Override
    public IdResponseDto editAsset(String assetId, UiAssetEditRequest uiAssetEditRequest) {
        return dslContextFactory.transactionResult(dsl -> assetApiService.editAsset(assetId, uiAssetEditRequest));
    }

    @Override
    public IdResponseDto deleteAsset(String assetId) {
        return dslContextFactory.transactionResult(dsl -> assetApiService.deleteAsset(assetId));
    }

    @Override
    public PolicyDefinitionPage getPolicyDefinitionPage() {
        return dslContextFactory.transactionResult(dsl -> new PolicyDefinitionPage(policyDefinitionApiService.getPolicyDefinitions()));
    }

    @Override
    @Deprecated
    public IdResponseDto createPolicyDefinition(PolicyDefinitionCreateRequest policyDefinitionDtoDto) {
        return dslContextFactory.transactionResult(dsl -> policyDefinitionApiService.createPolicyDefinition(policyDefinitionDtoDto));
    }

    @Override
    public IdResponseDto createPolicyDefinitionV2(PolicyDefinitionCreateDto policyDefinitionCreateDto) {
        return dslContextFactory.transactionResult(dsl -> policyDefinitionApiService.createPolicyDefinitionV2(policyDefinitionCreateDto));
    }

    @Override
    public IdResponseDto deletePolicyDefinition(String policyId) {
        return dslContextFactory.transactionResult(dsl -> policyDefinitionApiService.deletePolicyDefinition(policyId));
    }

    @Override
    public ContractDefinitionPage getContractDefinitionPage() {
        return dslContextFactory.transactionResult(dsl -> new ContractDefinitionPage(contractDefinitionApiService.getContractDefinitions()));
    }

    @Override
    public IdResponseDto createContractDefinition(ContractDefinitionRequest contractDefinitionRequest) {
        return dslContextFactory.transactionResult(dsl -> contractDefinitionApiService.createContractDefinition(contractDefinitionRequest));
    }

    @Override
    public IdResponseDto deleteContractDefinition(String contractDefinitionId) {
        return dslContextFactory.transactionResult(dsl -> contractDefinitionApiService.deleteContractDefinition(contractDefinitionId));
    }

    @Override
    public IdResponseDto createDataOffer(DataOfferCreateRequest dataOfferCreateRequest) {
        return dslContextFactory.transactionResult(dsl -> dataOfferPageApiService.createDataOffer(dsl, dataOfferCreateRequest));
    }

    @Override
    public List<UiDataOffer> getCatalogPageDataOffers(String participantId, String connectorEndpoint) {
        return dslContextFactory.transactionResult(dsl -> catalogApiService.fetchDataOffers(participantId, connectorEndpoint));
    }

    @Override
    public UiContractNegotiation initiateContractNegotiation(ContractNegotiationRequest contractNegotiationRequest) {
        return dslContextFactory.transactionResult(dsl -> contractNegotiationApiService.initiateContractNegotiation(contractNegotiationRequest));
    }

    @Override
    public UiContractNegotiation getContractNegotiation(String contractNegotiationId) {
        return dslContextFactory.transactionResult(dsl -> contractNegotiationApiService.getContractNegotiation(contractNegotiationId));
    }

    @Override
    public ContractAgreementPage getContractAgreementPage(@Nullable ContractAgreementPageQuery contractAgreementPageQuery) {
        return dslContextFactory.transactionResult(dsl ->
            contractAgreementApiService.contractAgreementPage(dsl, contractAgreementPageQuery));
    }

    @Override
    public ContractAgreementCard getContractAgreementCard(String contractAgreementId) {
        return dslContextFactory.transactionResult(dsl ->
            contractAgreementApiService.contractAgreement(dsl, contractAgreementId));
    }

    @Override
    public IdResponseDto initiateTransfer(InitiateTransferRequest request) {
        return dslContextFactory.transactionResult(dsl -> contractAgreementTransferApiService.initiateTransfer(request));
    }

    @Override
    public IdResponseDto initiateCustomTransfer(InitiateCustomTransferRequest request) {
        return dslContextFactory.transactionResult(dsl -> contractAgreementTransferApiService.initiateCustomTransfer(request));
    }

    @Override
    public IdResponseDto terminateContractAgreement(
        String contractAgreementId,
        ContractTerminationRequest contractTerminationRequest
    ) {
        return dslContextFactory.transactionResult(dsl ->
            contractAgreementTerminationApiService.terminate(dsl, contractAgreementId, contractTerminationRequest));
    }

    @Override
    public TransferHistoryPage getTransferHistoryPage() {
        return dslContextFactory.transactionResult(unused ->
            new TransferHistoryPage(transferHistoryPageApiService.getTransferHistoryEntries()));
    }

    @Override
    public UiAsset getTransferProcessAsset(String transferProcessId) {
        return dslContextFactory.transactionResult(dsl -> transferHistoryPageAssetFetcherService.getAssetForTransferHistoryPage(transferProcessId));
    }

    @Override
    public IdAvailabilityResponse isPolicyIdAvailable(String policyId) {
        return dslContextFactory.transactionResult(dsl ->
            dataOfferPageApiService.checkIfPolicyIdAvailable(dsl, policyId));
    }

    @Override
    public IdAvailabilityResponse isAssetIdAvailable(String assetId) {
        return dslContextFactory.transactionResult(dsl ->
            dataOfferPageApiService.checkIfAssetIdAvailable(dsl, assetId));
    }

    @Override
    public IdAvailabilityResponse isContractDefinitionIdAvailable(String contractDefinitionId) {
        return dslContextFactory.transactionResult(dsl ->
            dataOfferPageApiService.checkIfContractDefinitionIdAvailable(dsl, contractDefinitionId));
    }

    @Override
    public BuildInfo buildInfo() {
        return dslContextFactory.transactionResult(dsl -> versionsService.getVersions());
    }
}
