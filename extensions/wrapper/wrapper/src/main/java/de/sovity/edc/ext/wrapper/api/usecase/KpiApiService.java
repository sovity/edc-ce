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

import de.sovity.edc.ext.wrapper.api.usecase.model.KpiResult;
import de.sovity.edc.ext.wrapper.api.usecase.model.TransferProcessStatesDto;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcessStates;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.query.QuerySpec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
public class KpiApiService {
    private final AssetIndex assetIndex;
    private final PolicyDefinitionStore policyDefinitionStore;
    private final ContractDefinitionStore contractDefinitionStore;
    private final TransferProcessStore transferProcessStore;
    private final ContractAgreementService contractAgreementService;

    public KpiResult kpiEndpoint() {
        var querySpec = QuerySpec.Builder.newInstance().build();

        var assetsCount = getAssetsCount(querySpec);
        var policiesCount = getPoliciesCount(querySpec);
        var contractDefinitionsCount = getContractDefinitionsCount(querySpec);
        var contractAgreements = getContractAgreementsCount(querySpec);
        var transferProcessDto = getTransferProcessesDto(querySpec);

        return new KpiResult(
                assetsCount,
                policiesCount,
                contractDefinitionsCount,
                contractAgreements,
                transferProcessDto);
    }

    private int getContractAgreementsCount(QuerySpec querySpec) {
        return contractAgreementService.query(querySpec).getContent().toList().size();
    }

    private TransferProcessStatesDto getTransferProcessesDto(QuerySpec querySpec) {
        var transferProcesses = transferProcessStore.findAll(querySpec).toList();

        var outgoing = getOutgoing(transferProcesses);
        var incoming = getIncoming(transferProcesses);

        return getTransferProcessStatesDto(outgoing, incoming);
    }

    @NotNull
    private static TransferProcessStatesDto getTransferProcessStatesDto(Map<TransferProcessStates, Long> outgoing, Map<TransferProcessStates, Long> incoming) {
        var transferProcessDto = new TransferProcessStatesDto();
        transferProcessDto.setOutgoingTransferProcessCounts(outgoing);
        transferProcessDto.setIncomingTransferProcessCounts(incoming);
        return transferProcessDto;
    }

    private static Map<TransferProcessStates, Long> getIncoming(List<TransferProcess> transferProcesses) {
        return transferProcesses.stream()
                .filter(it -> it.getType() == TransferProcess.Type.CONSUMER)
                .collect(groupingBy(it -> TransferProcessStates.from(it.getState()), counting()));
    }

    private static Map<TransferProcessStates, Long> getOutgoing(List<TransferProcess> transferProcesses) {
        return transferProcesses.stream()
                .filter(it -> it.getType() == TransferProcess.Type.PROVIDER)
                .collect(groupingBy(it -> TransferProcessStates.from(it.getState()), counting()));
    }

    private int getContractDefinitionsCount(QuerySpec querySpec) {
        var contractDefinitions = contractDefinitionStore.findAll(querySpec).toList();
        return contractDefinitions.size();
    }

    private int getPoliciesCount(QuerySpec querySpec) {
        var policies = policyDefinitionStore.findAll(querySpec).toList();
        return policies.size();
    }

    private int getAssetsCount(QuerySpec querySpec) {
        var assets = assetIndex.queryAssets(querySpec).toList();
        return assets.size();
    }
}
