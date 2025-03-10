/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_definitions;


import de.sovity.edc.ce.api.ui.model.ContractDefinitionRequest;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.controlplane.contract.spi.types.offer.ContractDefinition;


@RequiredArgsConstructor
@Service
public class ContractDefinitionBuilder {
    private final CriterionMapper criterionMapper;

    public ContractDefinition buildContractDefinition(ContractDefinitionRequest request) {
        var contractDefinitionId = request.getContractDefinitionId();
        var contractPolicyId = request.getContractPolicyId();
        var accessPolicyId = request.getAccessPolicyId();
        var assetsSelector = request.getAssetSelector();

        return ContractDefinition.Builder.newInstance()
            .id(contractDefinitionId)
            .contractPolicyId(contractPolicyId)
            .accessPolicyId(accessPolicyId)
            .assetsSelector(criterionMapper.buildCriteria(assetsSelector))
            .build();
    }
}
