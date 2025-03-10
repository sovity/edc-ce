/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.dashboard.services;

import de.sovity.edc.ce.api.utils.ServiceException;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.controlplane.asset.spi.index.AssetIndex;
import org.eclipse.edc.connector.controlplane.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.controlplane.services.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.connector.controlplane.services.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.connector.controlplane.services.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.eclipse.edc.spi.query.QuerySpec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DashboardDataFetcher {
    private final ContractNegotiationStore contractNegotiationStore;
    private final TransferProcessService transferProcessService;
    private final AssetIndex assetIndex;
    private final PolicyDefinitionService policyDefinitionService;
    private final ContractDefinitionService contractDefinitionService;

    public int getNumberOfAssets() {
        return assetIndex.queryAssets(QuerySpec.max()).toList().size();
    }

    public int getNumberOfPolicies() {
        return Math.toIntExact(policyDefinitionService.query(QuerySpec.max())
            .orElseThrow(ServiceException::new)
            .count());
    }

    public int getNumberOfContractDefinitions() {
        return Math.toIntExact(contractDefinitionService.query(QuerySpec.max())
            .orElseThrow(ServiceException::new)
            .count());
    }

    @NotNull
    public List<ContractNegotiation> getAllContractNegotiations() {
        return contractNegotiationStore.queryNegotiations(QuerySpec.max()).toList();
    }

    @NotNull
    public List<TransferProcess> getTransferProcesses() {
        return transferProcessService.query(QuerySpec.max()).orElseThrow(ServiceException::new).toList();
    }
}
