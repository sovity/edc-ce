/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_definitions;

import de.sovity.edc.ce.api.ui.model.ContractDefinitionEntry;
import de.sovity.edc.ce.api.ui.model.ContractDefinitionRequest;
import de.sovity.edc.ce.api.ui.model.IdResponseDto;
import de.sovity.edc.ce.api.utils.ServiceException;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.controlplane.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.connector.controlplane.services.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.spi.query.QuerySpec;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;


@RequiredArgsConstructor
@Service
public class ContractDefinitionApiService {
    private final ContractDefinitionService contractDefinitionService;
    private final CriterionMapper criterionMapper;
    private final ContractDefinitionBuilder contractDefinitionBuilder;

    @SneakyThrows
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
