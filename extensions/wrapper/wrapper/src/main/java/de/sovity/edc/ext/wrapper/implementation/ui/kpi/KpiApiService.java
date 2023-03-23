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

package de.sovity.edc.ext.wrapper.implementation.ui.kpi;

import de.sovity.edc.ext.wrapper.api.ui.kpi.model.KpiResult;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.query.QuerySpec;

@RequiredArgsConstructor
public class KpiApiService {
    private final AssetIndex assetIndex;
    private final PolicyDefinitionStore policyDefinitionStore;
    private final ContractDefinitionStore contractDefinitionStore;
    private final TransferProcessStore transferProcessStore;

    public KpiResult kpiEndpoint() {
        var querySpec = QuerySpec.Builder.newInstance().build();

        var assetsCount = getAssetsCount(querySpec);
        var policiesCount = getPoliciesCount(querySpec);
        var contractDefinitionsCount = getContractDefinitionsCount(querySpec);
        var transferProcessesCount = getTransferProcessesCount(querySpec);

        return new KpiResult(assetsCount, policiesCount, contractDefinitionsCount, transferProcessesCount);
    }

    private int getTransferProcessesCount(QuerySpec querySpec) {
        var transferProcesses = transferProcessStore.findAll(querySpec).toList();
        return transferProcesses.size();
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
