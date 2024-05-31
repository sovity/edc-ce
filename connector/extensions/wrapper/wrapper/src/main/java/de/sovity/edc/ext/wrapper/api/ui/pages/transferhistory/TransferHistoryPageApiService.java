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

package de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory;

import de.sovity.edc.ext.wrapper.api.ServiceException;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementDirection;
import de.sovity.edc.ext.wrapper.api.ui.model.TransferHistoryEntry;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static de.sovity.edc.ext.wrapper.utils.EdcDateUtils.utcMillisToOffsetDateTime;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class TransferHistoryPageApiService {

    private final AssetService assetService;
    private final ContractAgreementService contractAgreementService;
    private final ContractNegotiationStore contractNegotiationStore;
    private final TransferProcessService transferProcessService;
    private final TransferProcessStateService transferProcessStateService;

    /**
     * Fetches all Transfer History entries as {@link TransferHistoryEntry}s.
     *
     * @return {@link TransferHistoryEntry}s
     */
    @NotNull
    public List<TransferHistoryEntry> getTransferHistoryEntries() {

        var negotiationsById = getAllContractNegotiations().stream()
                .filter(negotiation -> negotiation != null)
                .filter(negotiation -> negotiation.getContractAgreement() != null)
                .collect(toMap(
                        it -> it.getContractAgreement().getId(),
                        Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(Entity::getCreatedAt))
                ));

        var agreementsById = getAllContractAgreements().stream().collect(toMap(
                ContractAgreement::getId, Function.identity()
        ));

        var assetsById = getAllAssets().stream()
                .collect(toMap(Asset::getId, Function.identity()));

        var transferProcesses = getAllTransferProcesses();

        return transferProcesses.stream().map(process -> {
            var agreement = Optional.ofNullable(agreementsById.get(process.getDataRequest().getContractId()));
            var negotiation = Optional.ofNullable(negotiationsById.get(process.getDataRequest().getContractId()));
            var asset = assetLookup(assetsById, process);
            var direction = negotiation.map(ContractNegotiation::getType).map(ContractAgreementDirection::fromType);
            var transferHistoryEntry = new TransferHistoryEntry();
            transferHistoryEntry.setAssetId(asset.getId());

            if (direction.isPresent()) {
                if (direction.get() == ContractAgreementDirection.CONSUMING) {
                    transferHistoryEntry.setAssetName(asset.getId());
                } else {
                    transferHistoryEntry.setAssetName(
                            StringUtils.isBlank((String) asset.getProperties().get(Prop.Dcterms.TITLE))
                                    ? asset.getId()
                                    : asset.getProperties().get(Prop.Dcterms.TITLE).toString()
                    );
                }
            }

            agreement.ifPresent(it -> transferHistoryEntry.setContractAgreementId(it.getId()));
            negotiation.ifPresent( it -> {
                transferHistoryEntry.setCounterPartyConnectorEndpoint(it.getCounterPartyAddress());
                transferHistoryEntry.setCounterPartyParticipantId(it.getCounterPartyId());
                transferHistoryEntry.setCreatedDate(utcMillisToOffsetDateTime(it.getCreatedAt()));
            });
            direction.ifPresent(transferHistoryEntry::setDirection);

            transferHistoryEntry.setErrorMessage(process.getErrorDetail());
            transferHistoryEntry.setLastUpdatedDate(utcMillisToOffsetDateTime(process.getUpdatedAt()));
            transferHistoryEntry.setState(transferProcessStateService.buildTransferProcessState(process.getState()));
            transferHistoryEntry.setTransferProcessId(process.getId());
            return transferHistoryEntry;
        }).sorted(Comparator.comparing(TransferHistoryEntry::getLastUpdatedDate).reversed()).toList();
    }

    private Asset assetLookup(Map<String, Asset> assetsById, TransferProcess process) {
        var assetId = process.getDataRequest().getAssetId();
        var asset = assetsById.get(assetId);
        if (asset == null) {
            return Asset.Builder.newInstance().id(assetId).build();
        }
        return asset;
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

    @NotNull
    private List<Asset> getAllAssets() {
        return assetService.query(QuerySpec.max()).orElseThrow(ServiceException::new).toList();
    }
}
