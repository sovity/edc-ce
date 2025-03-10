/*
 * Copyright 2025 sovity GmbH
 * Copyright 2023 Fraunhofer-Institut für Software- und Systemtechnik ISST
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer ISST - contributions to the Eclipse EDC 0.2.0 migration
 */
package de.sovity.edc.ce.api.ui.pages.transferhistory;

import de.sovity.edc.ce.api.common.model.UiAsset;
import de.sovity.edc.ce.api.ui.pages.contract_agreements.services.ContractNegotiationUtils;
import de.sovity.edc.ce.libs.mappers.AssetMapper;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset;
import org.eclipse.edc.connector.controlplane.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.controlplane.services.spi.asset.AssetService;
import org.eclipse.edc.connector.controlplane.services.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.eclipse.edc.spi.EdcException;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Service
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
        var assetId = process.getAssetId();
        var asset = assetService.findById(process.getAssetId());
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
