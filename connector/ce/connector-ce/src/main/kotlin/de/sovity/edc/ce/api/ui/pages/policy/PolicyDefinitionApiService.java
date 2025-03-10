/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.policy;


import de.sovity.edc.ce.api.common.model.UiPolicyExpression;
import de.sovity.edc.ce.api.ui.model.IdResponseDto;
import de.sovity.edc.ce.api.ui.model.PolicyDefinitionCreateDto;
import de.sovity.edc.ce.api.ui.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.ce.api.ui.model.PolicyDefinitionDto;
import de.sovity.edc.ce.api.utils.ServiceException;
import de.sovity.edc.ce.libs.mappers.LegacyPolicyMapper;
import de.sovity.edc.ce.libs.mappers.PolicyMapper;
import de.sovity.edc.runtime.simple_di.Service;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.controlplane.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.controlplane.services.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.spi.query.QuerySpec;

import java.util.Comparator;
import java.util.List;


@RequiredArgsConstructor
@Service
public class PolicyDefinitionApiService {

    private final PolicyDefinitionService policyDefinitionService;
    private final PolicyMapper policyMapper;
    private final LegacyPolicyMapper legacyPolicyMapper;

    public List<PolicyDefinitionDto> getPolicyDefinitions() {
        var policyDefinitions = getAllPolicyDefinitions();
        return policyDefinitions.stream()
            .sorted(Comparator.comparing(PolicyDefinition::getCreatedAt).reversed())
            .map(this::buildPolicyDefinitionDto)
            .toList();
    }

    @NotNull
    @Deprecated
    public IdResponseDto createPolicyDefinition(PolicyDefinitionCreateRequest request) {
        var uiPolicyExpression = legacyPolicyMapper.buildUiPolicyExpression(request.getPolicy());
        var policyDefinition = buildPolicyDefinition(request.getPolicyDefinitionId(), uiPolicyExpression);
        policyDefinition = policyDefinitionService.create(policyDefinition).orElseThrow(ServiceException::new);
        return new IdResponseDto(policyDefinition.getId());
    }

    @NotNull
    public IdResponseDto createPolicyDefinitionV2(PolicyDefinitionCreateDto request) {
        var policyDefinition = buildPolicyDefinition(request.getPolicyDefinitionId(), request.getExpression());
        policyDefinition = policyDefinitionService.create(policyDefinition).orElseThrow(ServiceException::new);
        return new IdResponseDto(policyDefinition.getId());
    }

    @NotNull
    public IdResponseDto deletePolicyDefinition(String policyDefinitionId) {
        var response = policyDefinitionService.deleteById(policyDefinitionId).orElseThrow(ServiceException::new);
        return new IdResponseDto(response.getId());
    }

    private List<PolicyDefinition> getAllPolicyDefinitions() {
        return policyDefinitionService.query(QuerySpec.max()).orElseThrow(ServiceException::new).toList();
    }

    public PolicyDefinitionDto buildPolicyDefinitionDto(PolicyDefinition policyDefinition) {
        var policy = policyMapper.buildUiPolicy(policyDefinition.getPolicy());
        return PolicyDefinitionDto.builder()
            .policyDefinitionId(policyDefinition.getId())
            .policy(policy)
            .build();
    }

    public PolicyDefinition buildPolicyDefinition(String id, UiPolicyExpression uiPolicyExpression) {
        var policy = policyMapper.buildPolicy(uiPolicyExpression);
        return PolicyDefinition.Builder.newInstance()
            .id(id)
            .policy(policy)
            .build();
    }
}
