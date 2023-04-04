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
import de.sovity.edc.ext.wrapper.api.usecase.model.ContractAgreementDtoDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
public class ContractAgreementPageService {
    private final AssetIndex assetIndex;
    private final ContractAgreementService contractAgreementService;
    private final ContractNegotiationStore contractNegotiationStore;
    private final TransferProcessService transferProcessService;

    public ContractAgreementPage contractAgreementPage() {
        var querySpec = QuerySpec.Builder.newInstance().build();
        var contractAgreementDtos = new ArrayList<ContractAgreementDto>();
        var contractAgreements = contractAgreementService.query(querySpec).getContent().toList();
        var negotiations = getNegotiations().stream().collect(groupingBy(it -> it.getContractAgreement().getId()));

        for (var contractAgreement : contractAgreements) {
            var contractAgreementDtoDto = new ContractAgreementDtoDto(contractAgreement.getId());
            var assetDto = getAssetDto(contractAgreement);
            var policy = contractAgreement.getPolicy();
            var transferProcessesDtos = getTransferprocessDtos(contractAgreement);
            var contractNegotiationDtos = getContractNegotiationDtos(negotiations, contractAgreement);

            contractAgreementDtos.add(new ContractAgreementDto(
                    contractAgreementDtoDto, assetDto, policy, contractNegotiationDtos, transferProcessesDtos
            ));
        }

        return new ContractAgreementPage(contractAgreementDtos);
    }

    private static ArrayList<ContractNegotiationDto> getContractNegotiationDtos(Map<String, List<ContractNegotiation>> negotiations, ContractAgreement contractAgreement) {
        var agreementNegotiations = negotiations.getOrDefault(contractAgreement.getId(), List.of());
        var contractNegotiationDtos = new ArrayList<ContractNegotiationDto>();

        for (var agreementNegotiation : agreementNegotiations) {
            contractNegotiationDtos.add(new ContractNegotiationDto(agreementNegotiation.getId()));
        }
        return contractNegotiationDtos;
    }

    private ArrayList<TransferprocessDto> getTransferprocessDtos(ContractAgreement contractAgreement) {
        var transferProcesses = getTransferProcesses(contractAgreement);
        var transferProcessesDtos = new ArrayList<TransferprocessDto>();
        for (var transferProcess : transferProcesses) {
            transferProcessesDtos.add(new TransferprocessDto(transferProcess.getId()));
        }
        return transferProcessesDtos;
    }

    private AssetDto getAssetDto(ContractAgreement contractAgreement) {
        var assetId = getAsset(contractAgreement).getId();
        var assetDto = new AssetDto(assetId);
        return assetDto;
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
        var transferProcesses = new ArrayList<TransferProcess>();
        var processStream = transferProcessService.query(querySpec).getContent();
        processStream.forEach(transferProcess -> {
            if (transferProcess.getDataRequest().getContractId().equals(contractAgreement.getId())) {
                transferProcesses.add(transferProcess);
            }
        });
        return transferProcesses;
    }
}
