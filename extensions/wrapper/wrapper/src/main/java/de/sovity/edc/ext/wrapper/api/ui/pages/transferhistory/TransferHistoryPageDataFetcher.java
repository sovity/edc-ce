package de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory;/*
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

import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementDirection;
import de.sovity.edc.ext.wrapper.api.ui.model.TransferHistoryEntry;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.spi.entity.Entity;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

@RequiredArgsConstructor
public class TransferHistoryPageDataFetcher {
    private final ContractAgreementService contractAgreementService;
    private final ContractNegotiationStore contractNegotiationStore;
    private final TransferProcessService transferProcessService;

    private final AssetService assetService;

    /**
     * Fetches all contract agreements as {@link TransferHistoryEntry}s.
     *
     * @return {@link TransferHistoryEntry}s
     */
    @NotNull
    public List<TransferHistoryEntry> getTransferHistoryEntries() {

        var agreements = getAllContractAgreements();

        var negotiationsByID = getAllContractNegotiations().stream()
                .filter(it -> it.getContractAgreement() != null)
                .collect(groupingBy(
                        it -> it.getContractAgreement().getId(),
                        collectingAndThen(maxBy(Comparator.comparing(Entity::getCreatedAt)), Optional::get)
                ));


        var agreementsById = agreements.stream().collect(toMap(
                ContractAgreement::getId, Function.identity()
        ));

        var transferStream = getAllTransferProcesses().stream();
        var assetStream = getAllAssets().stream();
        var transfersList = transferStream.map(process -> {

            var agreement =
                    agreementsById.get(process.getDataRequest().getContractId());
            var negotiation = negotiationsByID.get(process.getDataRequest().getContractId());
            var asset = assetStream.filter(assets -> assets.getId().equals(process.getDataRequest().getAssetId())).findFirst().orElse(null);
            return new TransferHistoryEntry(
                    process.getId(),
                    negotiation.getCreatedAt(),
                    process.getUpdatedAt(),
                    process.getState(),
                    agreement.getId(),
                    ContractAgreementDirection.fromType(negotiation.getType()),
                    negotiation.getCounterPartyId(),
                    process.getDataRequest().getDestinationType(),
                    asset.getName(),
                    asset.getId(),
                    process.getErrorDetail());
        }).toList();
        return transfersList;

    }

    @NotNull
    private List<ContractNegotiation> getAllContractNegotiations() {
        return contractNegotiationStore.queryNegotiations(QuerySpec.max()).toList();
    }

    @NotNull
    private List<ContractAgreement> getAllContractAgreements() {
        return contractAgreementService.query(QuerySpec.max()).getContent().toList();
    }

    @NotNull
    private List<TransferProcess> getAllTransferProcesses() {
        return transferProcessService.query(QuerySpec.max()).getContent().toList();
    }

    @NotNull
    private List<Asset> getAllAssets() {
        return assetService.query(QuerySpec.max()).getContent().toList();
    }

}
