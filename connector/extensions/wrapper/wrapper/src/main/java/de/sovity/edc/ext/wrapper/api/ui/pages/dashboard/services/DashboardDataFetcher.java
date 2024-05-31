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

package de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services;

import de.sovity.edc.ext.wrapper.api.ServiceException;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.connector.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.query.QuerySpec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
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
