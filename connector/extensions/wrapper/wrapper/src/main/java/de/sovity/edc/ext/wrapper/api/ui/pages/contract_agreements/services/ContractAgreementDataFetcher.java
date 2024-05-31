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

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services;

import de.sovity.edc.ext.wrapper.api.ServiceException;
import de.sovity.edc.ext.wrapper.utils.MapUtils;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
public class ContractAgreementDataFetcher {
    private final ContractAgreementService contractAgreementService;
    private final ContractNegotiationStore contractNegotiationStore;
    private final TransferProcessService transferProcessService;
    private final AssetIndex assetIndex;

    /**
     * Fetches all contract agreements as {@link ContractAgreementData}s.
     *
     * @return {@link ContractAgreementData}s
     */
    @NotNull
    public List<ContractAgreementData> getContractAgreements() {
        var agreements = getAllContractAgreements();
        var assets = MapUtils.associateBy(getAllAssets(), Asset::getId);

        var negotiations = getAllContractNegotiations().stream()
                .filter(it -> it.getContractAgreement() != null)
                .collect(groupingBy(it -> it.getContractAgreement().getId()));

        var transfers = getAllTransferProcesses().stream()
                .collect(groupingBy(it -> it.getDataRequest().getContractId()));

        // A ContractAgreement has multiple ContractNegotiations when doing a loopback consumption
        return agreements.stream()
                .flatMap(agreement -> negotiations.getOrDefault(agreement.getId(), List.of()).stream()
                        .map(negotiation -> {
                            var asset = getAsset(agreement, negotiation, assets);
                            var contractTransfers = transfers.getOrDefault(agreement.getId(), List.of());
                            return new ContractAgreementData(agreement, negotiation, asset, contractTransfers);
                        }))
                .toList();
    }

    private Asset getAsset(ContractAgreement agreement, ContractNegotiation negotiation, Map<String, Asset> assets) {
        var assetId = agreement.getAssetId();

        if (negotiation.getType() == ContractNegotiation.Type.CONSUMER) {
            return dummyAsset(assetId);
        }

        var asset = assets.get(assetId);
        return asset == null ? dummyAsset(assetId) : asset;
    }

    private Asset dummyAsset(String assetId) {
        return Asset.Builder.newInstance().id(assetId).build();
    }

    private List<Asset> getAllAssets() {
        return assetIndex.queryAssets(QuerySpec.max()).toList();
    }

    @NotNull
    private List<ContractNegotiation> getAllContractNegotiations() {
        return contractNegotiationStore.queryNegotiations(QuerySpec.max()).toList();
    }

    @NotNull
    private List<ContractAgreement> getAllContractAgreements() {
        return contractAgreementService.query(QuerySpec.max()).orElseThrow(ServiceException::new).toList();
    }

    @NotNull
    private List<TransferProcess> getAllTransferProcesses() {
        return transferProcessService.query(QuerySpec.max()).orElseThrow(ServiceException::new).toList();
    }
}
