/*
 *  Copyright (c) 2022 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.wrapper.api.ui;

import de.sovity.edc.ext.wrapper.api.common.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetEditRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.AssetPage;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementPage;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementPageQuery;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractTerminationRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractDefinitionPage;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractDefinitionRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractNegotiationRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.DashboardPage;
import de.sovity.edc.ext.wrapper.api.ui.model.IdResponseDto;
import de.sovity.edc.ext.wrapper.api.ui.model.InitiateCustomTransferRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.InitiateTransferRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.PolicyDefinitionPage;
import de.sovity.edc.ext.wrapper.api.ui.model.TransferHistoryPage;
import de.sovity.edc.ext.wrapper.api.ui.model.UiContractNegotiation;
import de.sovity.edc.ext.wrapper.api.ui.model.UiDataOffer;
import de.sovity.edc.ext.wrapper.api.ui.pages.asset.AssetApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.catalog.CatalogApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.ContractAgreementPageApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.ContractAgreementTransferApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions.ContractDefinitionApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_negotiations.ContractNegotiationApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.DashboardPageApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.policy.PolicyDefinitionApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferHistoryPageApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferHistoryPageAssetFetcherService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("java:S6539") // This class is so large so the generated API Clients can have one UiApi
@RequiredArgsConstructor
public class UiResourceImpl implements UiResource {

    private final ContractAgreementPageApiService contractAgreementApiService;
    private final ContractAgreementTransferApiService contractAgreementTransferApiService;
    private final TransferHistoryPageApiService transferHistoryPageApiService;
    private final TransferHistoryPageAssetFetcherService transferHistoryPageAssetFetcherService;
    private final AssetApiService assetApiService;
    private final PolicyDefinitionApiService policyDefinitionApiService;
    private final CatalogApiService catalogApiService;
    private final ContractDefinitionApiService contractDefinitionApiService;
    private final ContractNegotiationApiService contractNegotiationApiService;
    private final DashboardPageApiService dashboardPageApiService;

    @Override
    public DashboardPage getDashboardPage() {
        return dashboardPageApiService.dashboardPage();
    }

    @Override
    public AssetPage getAssetPage() {
        return new AssetPage(assetApiService.getAssets());
    }

    @Override
    public IdResponseDto createAsset(UiAssetCreateRequest uiAssetCreateRequest) {
        return assetApiService.createAsset(uiAssetCreateRequest);
    }

    @Override
    public IdResponseDto editAsset(String assetId, UiAssetEditRequest uiAssetEditRequest) {
        return assetApiService.editAsset(assetId, uiAssetEditRequest);
    }

    @Override
    public IdResponseDto deleteAsset(String assetId) {
        return assetApiService.deleteAsset(assetId);
    }

    @Override
    public PolicyDefinitionPage getPolicyDefinitionPage() {
        return new PolicyDefinitionPage(policyDefinitionApiService.getPolicyDefinitions());
    }

    @Override
    public IdResponseDto createPolicyDefinition(PolicyDefinitionCreateRequest policyDefinitionDtoDto) {
        return policyDefinitionApiService.createPolicyDefinition(policyDefinitionDtoDto);
    }

    @Override
    public IdResponseDto deletePolicyDefinition(String policyId) {
        return policyDefinitionApiService.deletePolicyDefinition(policyId);
    }

    @Override
    public ContractDefinitionPage getContractDefinitionPage() {
        return new ContractDefinitionPage(contractDefinitionApiService.getContractDefinitions());
    }

    @Override
    public IdResponseDto createContractDefinition(ContractDefinitionRequest contractDefinitionRequest) {
        return contractDefinitionApiService.createContractDefinition(contractDefinitionRequest);
    }

    @Override
    public IdResponseDto deleteContractDefinition(String contractDefinitionId) {
        return contractDefinitionApiService.deleteContractDefinition(contractDefinitionId);
    }

    @Override
    public List<UiDataOffer> getCatalogPageDataOffers(String connectorEndpoint) {
        return catalogApiService.fetchDataOffers(connectorEndpoint);
    }

    @Override
    public UiContractNegotiation initiateContractNegotiation(ContractNegotiationRequest contractNegotiationRequest) {
        return contractNegotiationApiService.initiateContractNegotiation(contractNegotiationRequest);
    }

    @Override
    public UiContractNegotiation getContractNegotiation(String contractNegotiationId) {
        return contractNegotiationApiService.getContractNegotiation(contractNegotiationId);
    }

    @Override
    public ContractAgreementPage getContractAgreementPage(@Nullable ContractAgreementPageQuery contractAgreementPageQuery) {
        return contractAgreementApiService.contractAgreementPage();
    }


    @Override
    public IdResponseDto initiateTransfer(InitiateTransferRequest request) {
        return contractAgreementTransferApiService.initiateTransfer(request);
    }

    @Override
    public IdResponseDto initiateCustomTransfer(InitiateCustomTransferRequest request) {
        return contractAgreementTransferApiService.initiateCustomTransfer(request);
    }

    @Override
    public IdResponseDto terminateContractAgreement(String contractAgreementId, ContractTerminationRequest contractTerminationRequest) {
        return null;
    }

    @Override
    public TransferHistoryPage getTransferHistoryPage() {
        return new TransferHistoryPage(transferHistoryPageApiService.getTransferHistoryEntries());
    }

    @Override
    public UiAsset getTransferProcessAsset(String transferProcessId) {
        return transferHistoryPageAssetFetcherService.getAssetForTransferHistoryPage(transferProcessId);
    }
}
