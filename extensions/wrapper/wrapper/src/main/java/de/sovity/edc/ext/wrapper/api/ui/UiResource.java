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
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementPage;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementTransferRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.TransferHistoryPage;
import de.sovity.edc.ext.wrapper.api.ui.pages.assets.AssetPageApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.ContractAgreementPageApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.ContractAgreementTransferApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferHistoryPageApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.api.model.IdResponseDto;

import java.util.List;

@Path("wrapper/ui")
@Tag(name = "UI", description = "EDC UI API Endpoints")
@RequiredArgsConstructor
public class UiResource {

    private final AssetPageApiService assetPageApiService;
    private final ContractAgreementPageApiService contractAgreementApiService;
    private final TransferHistoryPageApiService transferHistoryPageApiService;
    private final ContractAgreementTransferApiService contractAgreementTransferApiService;

    @GET
    @Path("pages/asset-page")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Collect all data for Asset Page")
    public List<AssetDto> assetPageEndpoint() {
        return assetPageApiService.getAssetsForAssetPage();
    }

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
    public IdResponseDto initiateTransfer(
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
        return transferHistoryPageApiService.transferHistoryPage();
    }
}
