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
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDefinitionDto;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.api.model.IdResponse;
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
                .map(policyDefinition -> {
            var entry = policyMapper.buildPolicyDefinitionDto(policyDefinition);
            return entry;
        }).toList();
    }

    @NotNull
    public IdResponse createPolicy(PolicyDefinitionDto request) {
        var policyDefinition = policyMapper.buildPolicyDefinition(request);
        policyDefinition = policyDefinitionService.create(policyDefinition).getContent();
        return IdResponse.Builder.newInstance().id(policyDefinition.getId()).createdAt(policyDefinition.getCreatedAt()).build();
    }

    private List<PolicyDefinition> getAllPolicies() {
        var policyDefinitions = policyDefinitionService.query(QuerySpec.max()).getContent().toList();
        return policyDefinitions;
    }
}
