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
import de.sovity.edc.ext.wrapper.utils.QueryUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset;
import org.eclipse.edc.connector.controlplane.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.controlplane.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.controlplane.services.spi.asset.AssetService;
import org.eclipse.edc.connector.controlplane.services.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.controlplane.services.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.eclipse.edc.spi.entity.Entity;
import org.eclipse.edc.spi.query.QuerySpec;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
            .filter(Objects::nonNull)
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
            var agreement = Optional.ofNullable(agreementsById.get(process.getContractId()));
            var negotiation = Optional.ofNullable(negotiationsById.get(process.getContractId()));
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
            negotiation.ifPresent(it -> {
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
        var assetId = process.getAssetId();
        var asset = assetsById.get(assetId);
        if (asset == null) {
            return Asset.Builder.newInstance().id(assetId).build();
        }
        return asset;
    }

    @NotNull
    private List<ContractNegotiation> getAllContractNegotiations() {
        return QueryUtils.fetchAllInBatches((offset, limit) ->
            contractNegotiationStore.queryNegotiations(
                QuerySpec.Builder.newInstance()
                    .offset(offset)
                    .limit(limit)
                    .build()
            ).toList()
        );
    }

    @NotNull
    private List<ContractAgreement> getAllContractAgreements() {
        return QueryUtils.fetchAllInBatches((offset, limit) ->
            contractAgreementService.search(QuerySpec.Builder.newInstance().offset(offset).limit(limit).build())
                .orElseThrow(ServiceException::new)
        );
    }

    @NotNull
    private List<TransferProcess> getAllTransferProcesses() {
        return QueryUtils.fetchAllInBatches((offset, limit) ->
            transferProcessService.search(
                QuerySpec.Builder.newInstance().offset(offset).limit(limit).build()
            ).orElseThrow(ServiceException::new)
        );
    }

    @NotNull
    private List<Asset> getAllAssets() {
        return QueryUtils.fetchAllInBatches((offset, limit) ->
            assetService.search(QuerySpec.Builder.newInstance().offset(offset).limit(limit).build())
                .orElseThrow(ServiceException::new)
        );
    }
}
