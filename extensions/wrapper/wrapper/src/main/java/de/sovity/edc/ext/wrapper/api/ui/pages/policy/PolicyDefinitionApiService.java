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

package de.sovity.edc.ext.wrapper.api.ui.pages.policy;


import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDefinitionDto;
import de.sovity.edc.ext.wrapper.api.ui.model.IdResponseDto;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.spi.query.QuerySpec;

import java.util.Comparator;
import java.util.List;


@RequiredArgsConstructor
public class PolicyDefinitionApiService {

    private final PolicyDefinitionService policyDefinitionService;
    private final PolicyMapper policyMapper;

    public List<PolicyDefinitionDto> getPolicies() {
        var policyDefinitions = getAllPolicies();
        return policyDefinitions.stream()
                .sorted(Comparator.comparing(PolicyDefinition::getCreatedAt).reversed())
                .map(this::buildPolicyDefinitionDto)
                .toList();
    }

    @NotNull
    public IdResponseDto createPolicy(PolicyDefinitionCreateRequest request) {
        var policyDefinition = buildPolicyDefinition(request);
        policyDefinition = policyDefinitionService.create(policyDefinition).getContent();
        return new IdResponseDto(policyDefinition.getId());
    }

    @NotNull
    public IdResponseDto deletePolicy(String policyDefinitionId) {
        var response = policyDefinitionService.deleteById(policyDefinitionId);
        return new IdResponseDto(response.getContent().getId());
    }

    private List<PolicyDefinition> getAllPolicies() {
        return policyDefinitionService.query(QuerySpec.max()).getContent().toList();
    }
    public PolicyDefinitionDto buildPolicyDefinitionDto(PolicyDefinition policyDefinition) {
        var policy = policyMapper.buildPolicyDto(policyDefinition.getPolicy());
        return PolicyDefinitionDto.builder()
                .policyDefinitionId(policyDefinition.getId())
                .uiPolicyDto(policy)
                .build();
    }

    public PolicyDefinition buildPolicyDefinition(PolicyDefinitionCreateRequest policyDefinitionDto) {
        var policy = policyMapper.buildPolicy(policyDefinitionDto.getUiPolicyDto());
        return PolicyDefinition.Builder.newInstance()
                .id(policyDefinitionDto.getPolicyDefinitionId())
                .policy(policy)
                .build();
    }
}
