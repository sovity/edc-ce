/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory;

import de.sovity.edc.ext.wrapper.api.common.mappers.AssetMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services.ContractNegotiationUtils;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class TransferHistoryPageAssetFetcherService {
    private final AssetService assetService;
    private final TransferProcessService transferProcessService;
    private final AssetMapper assetMapper;
    private final ContractNegotiationStore contractNegotiationStore;
    private final ContractNegotiationUtils contractNegotiationUtils;


    public UiAsset getAssetForTransferHistoryPage(String transferProcessId) {

        var transferProcessById = transferProcessService.findById(transferProcessId);
        if (transferProcessById == null) {
            throw new EdcException("Could not find transfer process with ID %s.".formatted(transferProcessId));
        }
        return getAssetFromTransferProcess(transferProcessById);
    }

    @NotNull
    private UiAsset getAssetFromTransferProcess(TransferProcess process) {
        var asset = getTransferProcessAsset(process);
        var negotiation = contractNegotiationUtils.findByContractAgreementIdOrThrow(process.getContractId());

        // Additional Asset Metadata required for UI
        return buildUiAsset(asset, negotiation);
    }

    private Asset getTransferProcessAsset(TransferProcess process) {
        var assetId = process.getDataRequest().getAssetId();
        var asset = assetService.findById(process.getDataRequest().getAssetId());
        if (asset == null) {
            asset = Asset.Builder.newInstance().id(assetId).build();
        }
        return asset;
    }

    private UiAsset buildUiAsset(Asset asset, ContractNegotiation negotiation) {
        var connectorEndpoint = contractNegotiationUtils.getProviderConnectorEndpoint(negotiation);
        var participantId = contractNegotiationUtils.getProviderParticipantId(negotiation);
        return assetMapper.buildUiAsset(asset, connectorEndpoint, participantId);
    }
}
