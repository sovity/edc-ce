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

package de.sovity.edc.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.client.gen.model.UiPolicyConstraint;
import de.sovity.edc.client.gen.model.UiPolicyCreateRequest;
import de.sovity.edc.client.gen.model.UiPolicyLiteral;
import de.sovity.edc.ext.wrapper.api.common.mappers.OperatorMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.AtomicConstraintMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.ConstraintExtractor;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.LiteralMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.PolicyValidator;
import de.sovity.edc.ext.wrapper.api.common.model.OperatorDto;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyLiteralType;
import org.eclipse.edc.connector.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.query.QuerySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static de.sovity.edc.client.PolicyTestUtils.buildPolicyConstraints;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ApiTest
@ExtendWith(EdcExtension.class)
class PolicyDefinitionApiServiceTest {
    private static final String DATA_SINK = "http://my-data-sink/api/stuff";
    private static final String COUNTER_PARTY_ADDRESS = "http://some-other-connector/api/v1/ids/data";
    ObjectMapper jsonLdObjectMapper;
    ConstraintExtractor constraintExtractor;
    AtomicConstraintMapper atomicConstraintMapper;
    PolicyValidator policyValidator;
    LiteralMapper literalMapper;
    OperatorMapper operatorMapper;
    PolicyMapper policyMapper;

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
        jsonLdObjectMapper = new ObjectMapper();
        policyValidator = new PolicyValidator();
        literalMapper = new LiteralMapper(jsonLdObjectMapper);
        operatorMapper = new OperatorMapper();
        atomicConstraintMapper = new AtomicConstraintMapper(literalMapper, operatorMapper);
        constraintExtractor = new ConstraintExtractor(policyValidator, atomicConstraintMapper);
        policyMapper = new PolicyMapper(jsonLdObjectMapper, constraintExtractor, atomicConstraintMapper);
    }

    @Test
    void policyDefinitionPage(PolicyDefinitionService policyDefinitionService) {
        // arrange
        var client = TestUtils.edcClient();
        var constraint = de.sovity.edc.client.gen.model.UiPolicyConstraint.builder()
                .left("left-1")
                .operator(UiPolicyConstraint.OperatorEnum.EQ)
                .right(UiPolicyLiteral.builder()
                        .type(UiPolicyLiteral.TypeEnum.STRING)
                        .value("right-1")
                        .build())
                .build();

        createPolicyDefinition(policyDefinitionService, List.of(constraint));

        // act
        var response = client.uiApi().policyPage();

        // assert
        var policyDefinitions = response.getPolicies();
        assertThat(policyDefinitions).hasSize(1);
        var policyDefinition = policyDefinitions.get(0);
        assertThat(policyDefinition.getUiPolicyDto().getConstraints()).hasSize(1);

        var constraintEntry = policyDefinition.getUiPolicyDto().getConstraints().get(0);
        assertThat(constraintEntry.getLeft()).isEqualTo("left-1");
        assertThat(constraintEntry.getOperator()).isEqualTo(UiPolicyConstraint.OperatorEnum.EQ);
        assertThat(constraintEntry.getRight().getType()).isEqualTo(UiPolicyLiteral.TypeEnum.STRING);
    }

    @Test
    void PolicyDefinitionPageSorting(PolicyDefinitionService policyDefinitionService) {
        // arrange
        var client = TestUtils.edcClient();
        var constraint1 = de.sovity.edc.client.gen.model.UiPolicyConstraint.builder()
                .left("left-1")
                .operator(UiPolicyConstraint.OperatorEnum.EQ)
                .right(UiPolicyLiteral.builder()
                        .type(UiPolicyLiteral.TypeEnum.STRING)
                        .value("right-1")
                        .build())
                .build();
        var constraint2 = de.sovity.edc.client.gen.model.UiPolicyConstraint.builder()
                .left("left-2")
                .operator(UiPolicyConstraint.OperatorEnum.EQ)
                .right(UiPolicyLiteral.builder()
                        .type(UiPolicyLiteral.TypeEnum.STRING)
                        .value("right-2")
                        .build())
                .build();
        var constraint3 = de.sovity.edc.client.gen.model.UiPolicyConstraint.builder()
                .left("left-3")
                .operator(UiPolicyConstraint.OperatorEnum.EQ)
                .right(UiPolicyLiteral.builder()
                        .type(UiPolicyLiteral.TypeEnum.STRING)
                        .value("right-3")
                        .build())
                .build();

        createPolicyDefinition(policyDefinitionService, List.of(constraint1), 1628956802000L);
        createPolicyDefinition(policyDefinitionService, List.of(constraint3), 1628956800000L);
        createPolicyDefinition(policyDefinitionService, List.of(constraint2), 1628956801000L);

        // act
        var result = client.uiApi().policyPage();

        // assert
        assertThat(result.getPolicies())
                .extracting(policyDefinition -> policyDefinition.getUiPolicyDto().getConstraints().get(0).getLeft())
                .containsExactly("left-1", "left-2", "left-3");
    }

    @Test
    void testPolicyDefinitionCreation(PolicyDefinitionService policyDefinitionService) {
        /// arrange
        var client = TestUtils.edcClient();
        var constraint = de.sovity.edc.client.gen.model.UiPolicyConstraint.builder()
                .left("left-1")
                .operator(UiPolicyConstraint.OperatorEnum.EQ)
                .right(UiPolicyLiteral.builder()
                        .type(UiPolicyLiteral.TypeEnum.STRING)
                        .value("right-1")
                        .build())
                .build();

        var policyRequest = UiPolicyCreateRequest.builder()
                .constraints(List.of(constraint))
                .build();
        var policyDefinition = PolicyDefinitionCreateRequest.builder()
                .uiPolicyDto(policyRequest)
                .build();

        // act
        var response = client.uiApi().createPolicyDefinition(policyDefinition);

        // assert
        assertThat(response).isNotNull();
        var policyDefinitions = policyDefinitionService.query(QuerySpec.max()).getContent().toList();
        assertThat(policyDefinitions).hasSize(1);
        var policyDefinitionEntry = policyDefinitions.get(0);
        var policyDto = policyMapper.buildPolicyDto(policyDefinitionEntry.getPolicy());
        assertThat(policyDto.getConstraints()).hasSize(1);

        var constraintEntry = policyDto.getConstraints().get(0);
        assertThat(constraintEntry.getLeft()).isEqualTo("left-1");
        assertThat(constraintEntry.getOperator()).isEqualTo(OperatorDto.EQ);
        assertThat(constraintEntry.getRight().getType()).isEqualTo(UiPolicyLiteralType.STRING);
    }

    @Test
    void testDeletePolicyDefinition(PolicyDefinitionService policyDefinitionService) {
        // arrange
        var client = TestUtils.edcClient();
        var constraint = de.sovity.edc.client.gen.model.UiPolicyConstraint.builder()
                .left("left-1")
                .operator(UiPolicyConstraint.OperatorEnum.EQ)
                .right(UiPolicyLiteral.builder()
                        .type(UiPolicyLiteral.TypeEnum.STRING)
                        .value("right-1")
                        .build())
                .build();
        createPolicyDefinition(policyDefinitionService, List.of(constraint));
        assertThat(policyDefinitionService.query(QuerySpec.max()).getContent().toList()).hasSize(1);
        var policyDefinition = policyDefinitionService.query(QuerySpec.max()).getContent().toList().get(0);


        // act
        var response = client.uiApi().deletePolicyDefinition(policyDefinition.getId());

        // assert
        assertThat(response.getId()).isEqualTo(policyDefinition.getId());
        assertThat(policyDefinitionService.query(QuerySpec.max()).getContent()).isEmpty();
    }

    private void createPolicyDefinition(PolicyDefinitionService policyDefinitionService,
                                        List<UiPolicyConstraint> constraints,
                                        long createdAt) {
        var generatedUiPolicyDto = UiPolicyCreateRequest.builder()
                .constraints(constraints)
                .build();
        var uiPolicyDto = de.sovity.edc.ext.wrapper.api.common.model.UiPolicyCreateRequest.builder()
                .constraints(buildPolicyConstraints(generatedUiPolicyDto.getConstraints()))
                .build();
        var policyDefinition = PolicyDefinition.Builder.newInstance()
                .policy(policyMapper.buildPolicy(uiPolicyDto))
                .createdAt(createdAt)
                .build();
        policyDefinitionService.create(policyDefinition);
    }

    private void createPolicyDefinition(PolicyDefinitionService policyDefinitionService, List<UiPolicyConstraint> constraints) {
        var generatedUiPolicyDto = UiPolicyCreateRequest.builder()
                .constraints(constraints)
                .build();
        var uiPolicyDto = de.sovity.edc.ext.wrapper.api.common.model.UiPolicyCreateRequest.builder()
                .constraints(buildPolicyConstraints(generatedUiPolicyDto.getConstraints()))
                .build();
        var policyDefinition = PolicyDefinition.Builder.newInstance()
                .policy(policyMapper.buildPolicy(uiPolicyDto))
                .build();
        policyDefinitionService.create(policyDefinition);
    }
}
