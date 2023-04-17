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

package de.sovity.edc.ext.wrapper.api.usecase;

import de.sovity.edc.ext.wrapper.api.usecase.model.AssetDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ContractAgreementDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ContractAgreementPage;
import de.sovity.edc.ext.wrapper.api.usecase.model.ContractNegotiationDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.TransferprocessDto;
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

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class ContractAgreementPageService {
    private final AssetIndex assetIndex;
    private final ContractAgreementService contractAgreementService;
    private final ContractNegotiationStore contractNegotiationStore;
    private final TransferProcessService transferProcessService;

    public ContractAgreementPage contractAgreementPage() {
        var querySpec = QuerySpec.Builder.newInstance().build();
        var contractAgreements = contractAgreementService.query(querySpec).getContent().toList();
        var negotiations = getNegotiations().stream().collect(groupingBy(it -> it.getContractAgreement().getId()));

        var contractAgreementDtos = contractAgreements.stream().map(it ->
            new ContractAgreementDto(
                    it.getId(),
                    buildAssetDto(it),
                    it.getPolicy(),
                    buildContractNegotiationDtos(it, negotiations),
                    buildTransferprocessDtos(it)
            )
        ).toList();

        return new ContractAgreementPage(contractAgreementDtos);
    }

    private List<ContractNegotiationDto> buildContractNegotiationDtos(ContractAgreement contractAgreement, Map<String, List<ContractNegotiation>> negotiations) {
        var agreementNegotiations = negotiations.getOrDefault(contractAgreement.getId(), List.of());
        return agreementNegotiations.stream()
                .map(it -> new ContractNegotiationDto(it.getId()))
                .toList();
    }

    private List<TransferprocessDto> buildTransferprocessDtos(ContractAgreement contractAgreement) {
        var transferProcesses = getTransferProcesses(contractAgreement);
        return transferProcesses.stream().map(it -> new TransferprocessDto(it.getId())).toList();
    }

    private AssetDto buildAssetDto(ContractAgreement contractAgreement) {
        var asset = getAsset(contractAgreement);
        var assetId = asset.getId();
        var properties = mapValues(asset.getProperties(), Object::toString);

        return new AssetDto(assetId, properties);
    }

    private <K, T, R> Map<K, R> mapValues(Map<K, T> map, Function<T, R> mapFn) {
        return map.entrySet().stream().collect(toMap(Map.Entry::getKey, e -> mapFn.apply(e.getValue())));
    }

    private List<ContractNegotiation> getNegotiations() {
        var querySpec = QuerySpec.Builder.newInstance().build();
        return contractNegotiationStore.queryNegotiations(querySpec).toList();
    }

    private Asset getAsset(ContractAgreement contractAgreement) {
        return assetIndex.findById(contractAgreement.getAssetId());
    }

    private List<TransferProcess> getTransferProcesses(ContractAgreement contractAgreement) {
        var querySpec = QuerySpec.Builder.newInstance().build();
        var processStream = transferProcessService.query(querySpec).getContent();
        return processStream
                .filter(transferProcess -> transferProcess.getDataRequest().getContractId().equals(contractAgreement.getId()))
                .toList();
    }
}
