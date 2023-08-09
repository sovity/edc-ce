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

import de.sovity.edc.ext.wrapper.api.common.model.AssetDto;
import de.sovity.edc.ext.wrapper.api.ui.model.AssetPage;
import de.sovity.edc.ext.wrapper.api.ui.model.AssetCreateRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementPage;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementTransferRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.IdResponseDto;
import de.sovity.edc.ext.wrapper.api.ui.model.TransferHistoryPage;
import de.sovity.edc.ext.wrapper.api.ui.pages.asset.AssetApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.ContractAgreementPageApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.ContractAgreementTransferApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferHistoryPageApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferHistoryPageAssetFetcherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.api.model.IdResponse;

@Path("wrapper/ui")
@Tag(name = "UI", description = "EDC UI API Endpoints")
@RequiredArgsConstructor
public class UiResource {

    private final ContractAgreementPageApiService contractAgreementApiService;
    private final ContractAgreementTransferApiService contractAgreementTransferApiService;
    private final TransferHistoryPageApiService transferHistoryPageApiService;
    private final TransferHistoryPageAssetFetcherService transferHistoryPageAssetFetcherService;
    private final AssetApiService assetApiService;

    @GET
    @Path("pages/contract-agreement-page")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Collect all data for Contract Agreement Page")
    public ContractAgreementPage contractAgreementEndpoint() {
        return contractAgreementApiService.contractAgreementPage();
    }

    @POST
    @Path("pages/contract-agreement-page/transfers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Initiate a Transfer Process")
    public IdResponse initiateTransfer(
            ContractAgreementTransferRequest contractAgreementTransferRequest
    ) {
        return contractAgreementTransferApiService.initiateTransfer(
                contractAgreementTransferRequest
        );
    }

    @GET
    @Path("pages/transfer-history-page")
    @Produces(MediaType.APPLICATION_JSON)
    public TransferHistoryPage transferHistoryPageEndpoint() {
        return new TransferHistoryPage(transferHistoryPageApiService.getTransferHistoryEntries());
    }

    @GET
    @Path("pages/transfer-history-page/transfer-processes/{transferProcessId}/asset")
    @Produces(MediaType.APPLICATION_JSON)
    public AssetDto getTransferProcessAsset(@PathParam("transferProcessId") String transferProcessId) {
        return transferHistoryPageAssetFetcherService.getAssetForTransferHistoryPage(transferProcessId);
    }

    @GET
    @Path("pages/asset-page")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Collect all data for Asset Page")
    public AssetPage assetPage() {
        return new AssetPage(assetApiService.getAssets());
    }

    @POST
    @Path("pages/asset-page/assets")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Create a new Asset")
    public IdResponseDto createAsset(AssetCreateRequest assetCreateRequest) {
        return assetApiService.createAsset(assetCreateRequest);
    }

    @DELETE
    @Path("pages/asset-page/assets/{assetId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Delete an Asset")
    public IdResponseDto deleteAsset(@PathParam("assetId") String assetId) {
        return assetApiService.deleteAsset(assetId);
    }

}
