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

import de.sovity.edc.ext.wrapper.api.usecase.model.ContractAgreementDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ContractAgreementResult;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ContractAgreementApiService {
    private final AssetIndex assetIndex;
    private final ContractAgreementService contractAgreementService;
    private final ContractNegotiationStore contractNegotiationStore;
    private final TransferProcessService transferProcessService;

    public ContractAgreementResult contractAgreementEndpoint() {
        var querySpec = QuerySpec.Builder.newInstance().build();
        var contractAgreementDtos = new ArrayList<ContractAgreementDto>();
        var contractAgreements = contractAgreementService.query(querySpec).getContent().toList();

        for (var contractAgreement : contractAgreements) {
            var asset = getAsset(contractAgreement);
            var policy = getPolicy(contractAgreement);
            var negotiations = getNegotiations(contractAgreement);
            var transferProcesses = getTransferProcesses(contractAgreement);

            contractAgreementDtos.add(new ContractAgreementDto(
                    contractAgreement, List.of(asset), List.of(policy), negotiations, transferProcesses
            ));
        }

        return new ContractAgreementResult(contractAgreementDtos);
    }

    @NotNull
    private List<ContractNegotiation> getNegotiations(ContractAgreement contractAgreement) {
        var querySpec = QuerySpec.Builder.newInstance().build();
        return contractNegotiationStore
                .queryNegotiations(querySpec)
                .filter(it -> it.getContractAgreement().getId().equals(contractAgreement.getId())).toList();
    }

    private static Policy getPolicy(ContractAgreement contractAgreement) {
        return contractAgreement.getPolicy();
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
