/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions;


import de.sovity.edc.ext.wrapper.api.ui.model.ContractDefinitionRequest;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;


@RequiredArgsConstructor
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
