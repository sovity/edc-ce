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

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions;

import de.sovity.edc.ext.wrapper.api.ServiceException;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractDefinitionEntry;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractDefinitionRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.IdResponseDto;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.connector.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.spi.query.QuerySpec;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;


@RequiredArgsConstructor
public class ContractDefinitionApiService {
    private final ContractDefinitionService contractDefinitionService;
    private final CriterionMapper criterionMapper;
    private final ContractDefinitionBuilder contractDefinitionBuilder;

    public List<ContractDefinitionEntry> getContractDefinitions() {
        var definitions = getAllContractDefinitions();
        return definitions.stream()
                .sorted(Comparator.comparing(ContractDefinition::getCreatedAt).reversed())
                .map(this::buildContractDefinitionEntry)
                .toList();
    }

    @NotNull
    private ContractDefinitionEntry buildContractDefinitionEntry(ContractDefinition definition) {
        var entry = new ContractDefinitionEntry();
        entry.setContractDefinitionId(definition.getId());
        entry.setAccessPolicyId(definition.getAccessPolicyId());
        entry.setContractPolicyId(definition.getContractPolicyId());
        entry.setAssetSelector(criterionMapper.buildUiCriteria(definition.getAssetsSelector()));
        return entry;
    }

    @NotNull
    public IdResponseDto createContractDefinition(ContractDefinitionRequest request) {
        var contractDefinition = contractDefinitionBuilder.buildContractDefinition(request);
        contractDefinition = contractDefinitionService.create(contractDefinition).orElseThrow(ServiceException::new);
        return new IdResponseDto(contractDefinition.getId());
    }

    @NotNull
    public IdResponseDto deleteContractDefinition(String contractDefinitionId) {
        var response = contractDefinitionService.delete(contractDefinitionId).orElseThrow(ServiceException::new);
        return new IdResponseDto(response.getId());
    }

    private List<ContractDefinition> getAllContractDefinitions() {
        return contractDefinitionService.query(QuerySpec.max()).orElseThrow(ServiceException::new).toList();
    }
}
