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
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.query.QuerySpec;

@RequiredArgsConstructor
public class KpiApiService {
    private final AssetIndex assetIndex;
    private final PolicyDefinitionStore policyDefinitionStore;
    private final ContractDefinitionStore contractDefinitionStore;
    private final TransferProcessStore transferProcessStore;
    private final ContractAgreementService contractAgreementService;

    private int incomingDataCount;
    private int outgoingDataCount;

    public KpiResult kpiEndpoint() {
        var querySpec = QuerySpec.Builder.newInstance().build();

        var assetsCount = getAssetsCount(querySpec);
        var policiesCount = getPoliciesCount(querySpec);
        var contractDefinitionsCount = getContractDefinitionsCount(querySpec);
        var contractAgreements = getContractAgreementsCount(querySpec);
        getTransferProcessesCount(querySpec);

        return new KpiResult(
                assetsCount,
                policiesCount,
                contractDefinitionsCount,
                contractAgreements,
                incomingDataCount,
                outgoingDataCount);
    }

    private int getContractAgreementsCount(QuerySpec querySpec) {
        return contractAgreementService.query(querySpec).getContent().toList().size();
    }

    private void getTransferProcessesCount(QuerySpec querySpec) {
        incomingDataCount = 0;
        outgoingDataCount = 0;

        var transferProcesses = transferProcessStore.findAll(querySpec).toList();

        for (var transferProcess : transferProcesses) {
            var type = transferProcess.getType();
            switch (type) {
                case PROVIDER -> outgoingDataCount++;
                case CONSUMER -> incomingDataCount++;
            }
        }
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
