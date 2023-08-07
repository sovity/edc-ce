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
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyDto;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.api.model.IdResponse;
import org.eclipse.edc.connector.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.query.QuerySpec;

import java.util.List;


@RequiredArgsConstructor
public class PolicyApiService {


    private final PolicyDefinitionService policyDefinitionService;
    private final PolicyMapper policyMapper;

    public List<UiPolicyDto> getPolicies() {
        var policies = getAllPolicies();

        return policies.stream().map(policy -> {
            var entry = policyMapper.buildPolicyDto(policy);
            return entry;
        }).toList();
    }

    private List<Policy> getAllPolicies() {
        var policyDefinitions = policyDefinitionService.query(QuerySpec.max()).getContent().toList();
        return policyDefinitions.stream().map(PolicyDefinition::getPolicy).toList();
    }

    @NotNull
    public IdResponse createPolicy(UiPolicyDto request) {
        var policy = policyMapper.buildPolicy(request);
        PolicyDefinition policyDefinition = PolicyDefinition.Builder.newInstance()
                .policy(policy)
                .build();
        policyDefinition = policyDefinitionService.create(policyDefinition).getContent();
        return IdResponse.Builder.newInstance().id(policyDefinition.getId()).createdAt(policyDefinition.getCreatedAt()).build();
    }


}
