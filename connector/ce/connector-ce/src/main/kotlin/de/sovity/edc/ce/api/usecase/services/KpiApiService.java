/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services;

import de.sovity.edc.ce.api.ui.model.TransferProcessSimplifiedState;
import de.sovity.edc.ce.api.ui.pages.transferhistory.TransferProcessStateService;
import de.sovity.edc.ce.api.usecase.model.KpiResult;
import de.sovity.edc.ce.api.usecase.model.TransferProcessStatesDto;
import de.sovity.edc.ce.api.utils.ServiceException;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.controlplane.asset.spi.index.AssetIndex;
import org.eclipse.edc.connector.controlplane.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.controlplane.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.connector.controlplane.services.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.controlplane.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.eclipse.edc.spi.query.QuerySpec;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
@Service
public class KpiApiService {
    private final AssetIndex assetIndex;
    private final PolicyDefinitionStore policyDefinitionStore;
    private final ContractDefinitionStore contractDefinitionStore;
    private final TransferProcessStore transferProcessStore;
    private final ContractAgreementService contractAgreementService;
    private final TransferProcessStateService transferProcessStateService;

    public KpiResult getKpis() {
        var assetsCount = getAssetsCount();
        var policiesCount = getPoliciesCount();
        var contractDefinitionsCount = getContractDefinitionsCount();
        var contractAgreements = getContractAgreementsCount();
        var transferProcessDto = getTransferProcessesDto();

        return new KpiResult(
            assetsCount,
            policiesCount,
            contractDefinitionsCount,
            contractAgreements,
            transferProcessDto
        );
    }

    private int getContractAgreementsCount() {
        return contractAgreementService.query(QuerySpec.max()).orElseThrow(ServiceException::new).toList().size();
    }

    private TransferProcessStatesDto getTransferProcessesDto() {
        var transferProcesses = transferProcessStore.findAll(QuerySpec.max()).toList();
        return new TransferProcessStatesDto(getIncoming(transferProcesses), getOutgoing(transferProcesses));
    }

    private Map<TransferProcessSimplifiedState, Long> getIncoming(List<TransferProcess> transferProcesses) {
        return transferProcesses.stream()
            .filter(it -> it.getType() == TransferProcess.Type.CONSUMER)
            .collect(groupingBy(this::getTransferProcessStates, counting()));
    }

    private Map<TransferProcessSimplifiedState, Long> getOutgoing(List<TransferProcess> transferProcesses) {
        return transferProcesses.stream()
            .filter(it -> it.getType() == TransferProcess.Type.PROVIDER)
            .collect(groupingBy(this::getTransferProcessStates, counting()));
    }

    private TransferProcessSimplifiedState getTransferProcessStates(TransferProcess transferProcess) {
        return transferProcessStateService.getSimplifiedState(transferProcess.getState());
    }

    private int getContractDefinitionsCount() {
        var contractDefinitions = contractDefinitionStore.findAll(QuerySpec.max()).toList();
        return contractDefinitions.size();
    }

    private int getPoliciesCount() {
        var policies = policyDefinitionStore.findAll(QuerySpec.max()).toList();
        return policies.size();
    }

    private int getAssetsCount() {
        var assets = assetIndex.queryAssets(QuerySpec.max()).toList();
        return assets.size();
    }
}
