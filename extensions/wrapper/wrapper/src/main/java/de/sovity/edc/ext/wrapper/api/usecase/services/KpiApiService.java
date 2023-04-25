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

package de.sovity.edc.ext.wrapper.api.usecase.services;

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
        return contractAgreementService.query(QuerySpec.max()).getContent().toList().size();
    }

    private TransferProcessStatesDto getTransferProcessesDto() {
        var transferProcesses = transferProcessStore.findAll(QuerySpec.max()).toList();
        return new TransferProcessStatesDto(getIncoming(transferProcesses), getOutgoing(transferProcesses));
    }

    private Map<TransferProcessStates, Long> getIncoming(List<TransferProcess> transferProcesses) {
        return transferProcesses.stream()
                .filter(it -> it.getType() == TransferProcess.Type.CONSUMER)
                .collect(groupingBy(it -> TransferProcessStates.from(it.getState()), counting()));
    }

    private Map<TransferProcessStates, Long> getOutgoing(List<TransferProcess> transferProcesses) {
        return transferProcesses.stream()
                .filter(it -> it.getType() == TransferProcess.Type.PROVIDER)
                .collect(groupingBy(it -> TransferProcessStates.from(it.getState()), counting()));
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
