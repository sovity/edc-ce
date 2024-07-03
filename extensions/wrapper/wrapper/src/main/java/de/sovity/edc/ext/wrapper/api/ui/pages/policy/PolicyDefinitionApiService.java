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

import de.sovity.edc.ext.wrapper.api.ServiceException;
import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import de.sovity.edc.ext.wrapper.api.common.model.*;
import de.sovity.edc.ext.wrapper.api.usecase.model.PolicyCreateRequest;
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

    // EDC model -> API model
    public List<PolicyDefinitionDto> getPolicyDefinitions() {
        var policyDefinitions = getAllPolicyDefinitions();
        return policyDefinitions.stream()
                .sorted(Comparator.comparing(PolicyDefinition::getCreatedAt).reversed())
                .map(this::buildPolicyDefinitionDto)
                .toList();
    }


    // API model -> EDC model

    //
    // the main createPolicyDefinition method
    //

    /*
     *  PolicyDefinitionCreateRequest is
     *  private String policyDefinitionId +
     *  private UiPolicyCreateRequest policy
     *
     *  UiPolicyCreateRequest is just
     *  private List<UiPolicyConstraint> constraints
     */
    @NotNull
    public IdResponseDto createPolicyDefinition(PolicyDefinitionCreateRequest request) {
        // we create a policyDefinition from the request
        // the request contains a policyDefinitionId and a policy
        // in buildPolicyDefinition we assign this Id to the policyDefinition
        var policyDefinition = buildPolicyDefinition(request);
        // we create the policyDefinition using the external policyDefinitionService
        policyDefinition = policyDefinitionService.create(policyDefinition).orElseThrow(ServiceException::new);
        // we return the Id of the created policyDefinition
        return new IdResponseDto(policyDefinition.getId());
    }

    //
    // the main buildPolicyDefinition method
    //

    /*
     *  PolicyDefinitionCreateRequest is
     *  private String policyDefinitionId +
     *  private UiPolicyCreateRequest policy
     *
     * UiPolicyCreateRequest is just
     * private List<UiPolicyConstraint> constraints
     */

    /**
     * A PolicyDefinition is a container for a Policy and a unique identifier. Policies by themselves do
     * not have and identity, they are value objects.
     * However, most connector runtimes will need to keep a set of policies as their reference or master data, which
     * requires them to be identifiable and addressable. In most cases this also means that they have a stable, unique
     * identity, potentially across systems. In such cases a Policy should be enveloped in a
     * PolicyDefinition.
     * Many external Policy formats like ODRL also require policies to have an ID.
     */
    public PolicyDefinition buildPolicyDefinition(PolicyDefinitionCreateRequest policyDefinitionDto) {
        // we receive a PolicyDefinitionCreateRequest, it's just a PolicyDefinitionId and a Policy
        // and call it policyDefinitionDto
        // we use mapper
        var policy = policyMapper.buildPolicy(policyDefinitionDto.getPolicy());
        return PolicyDefinition.Builder.newInstance()
            .id(policyDefinitionDto.getPolicyDefinitionId())
            .policy(policy)
            .build();
    }

    @NotNull
    public IdResponseDto deletePolicyDefinition(String policyDefinitionId) {
        var response = policyDefinitionService.deleteById(policyDefinitionId).orElseThrow(ServiceException::new);
        return new IdResponseDto(response.getId());
    }

    private List<PolicyDefinition> getAllPolicyDefinitions() {
        return policyDefinitionService.query(QuerySpec.max()).orElseThrow(ServiceException::new).toList();
    }

    /*
        * Builds a PolicyDefinitionDto from a PolicyDefinition
        *
        * PolicyDefinition is a complex object
        *
        * PolicyDefinitionDto is
        *    private String policyDefinitionId +
        *    private UiPolicy policy
        *
        * UiPolicy is
        *
        * private String policyJsonLd +
        * private List<UiPolicyConstraint> constraints +
        * private List<String> errors
        *
     */
    public PolicyDefinitionDto buildPolicyDefinitionDto(PolicyDefinition policyDefinition) {
        var policy = policyMapper.buildUiPolicy(policyDefinition.getPolicy());
        return PolicyDefinitionDto.builder()
                .policyDefinitionId(policyDefinition.getId())
                .policy(policy)
                .build();
    }

    public IdResponseDto createPolicyDefinition(PolicyCreateRequest policyCreateRequest) {
        var policyDefinition = buildPolicyDefinition(policyCreateRequest);
        policyDefinition = policyDefinitionService.create(policyDefinition).orElseThrow(ServiceException::new);
        return new IdResponseDto(policyDefinition.getId());
    }

    private PolicyDefinition buildPolicyDefinition(PolicyCreateRequest policyCreateRequest) {
        var permissionExpression = policyCreateRequest.getPermission().getExpression();
        var policy = policyMapper.buildPolicy(List.of(permissionExpression));
        return PolicyDefinition.Builder.newInstance()
                .id(policyCreateRequest.getPolicyDefinitionId())
                .policy(policy)
                .build();
    }

    public IdResponseDto createMultiPolicyDefinition(MultiPolicyDefinitionCreateRequest multiPolicyCreateRequest) {
        var policyDefinition = buildMultiPolicyDefinition(multiPolicyCreateRequest);
        // buildMultiPolicyDefinition processing ...

        policyDefinition = policyDefinitionService.create(policyDefinition).orElseThrow(ServiceException::new);
        return new IdResponseDto(policyDefinition.getId());
    }

    private PolicyDefinition buildMultiPolicyDefinition(MultiPolicyDefinitionCreateRequest multiPolicyCreateRequest) {
        var policy = policyMapper.buildMultiPolicy(multiPolicyCreateRequest.getPolicy());
        return PolicyDefinition.Builder.newInstance()
                .id(multiPolicyCreateRequest.getPolicyDefinitionId())
                .policy(policy)
                .build();
    }
}
