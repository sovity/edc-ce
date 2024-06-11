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

package de.sovity.edc.ext.wrapper.api.ui.pages.policy;


import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.GenericPolicyCreateRequest;
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.client.gen.model.PolicyDefinitionDto;
import de.sovity.edc.client.gen.model.UiPolicyConstraint;
import de.sovity.edc.client.gen.model.PolicyElement;
import de.sovity.edc.client.gen.model.UiPolicyCreateRequest;
import de.sovity.edc.client.gen.model.UiPolicyLiteral;
import de.sovity.edc.client.gen.model.UiPolicyLiteralType;
import de.sovity.edc.ext.wrapper.TestUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.entity.Entity;
import org.eclipse.edc.spi.query.QuerySpec;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static de.sovity.edc.client.gen.model.UiPolicyConstraintType.AND;
import static de.sovity.edc.client.gen.model.UiPolicyConstraintType.ATOMIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ApiTest
@ExtendWith(EdcExtension.class)
class PolicyDefinitionApiServiceTest {
    EdcClient client;

    UiPolicyConstraint constraint = UiPolicyConstraint.builder()
            .left("a")
            .operator(OperatorDto.EQ)
            .right(UiPolicyLiteral.builder()
                    .type(UiPolicyLiteralType.STRING)
                    .value("b")
                    .build())
            .build();

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
        client = TestUtils.edcClient();
    }

    @Test
    void getPolicyList() {
        // arrange
        createPolicyDefinition("my-policy-def-1");

        // act
        var response = client.uiApi().getPolicyDefinitionPage();

        // assert
        var policyDefinitions = response.getPolicies();
        assertThat(policyDefinitions).hasSize(2);
        var policyDefinition = policyDefinitions.stream()
                .filter(it -> it.getPolicyDefinitionId().equals("my-policy-def-1"))
                .findFirst().get();
        assertThat(policyDefinition.getPolicyDefinitionId()).isEqualTo("my-policy-def-1");
        assertThat(policyDefinition.getPolicy().getConstraints()).hasSize(1);

        var constraintEntry = policyDefinition.getPolicy().getConstraints().get(0);
        assertThat(constraintEntry).usingRecursiveComparison().isEqualTo(constraint);
    }

    @Test
    void createTraceXPolicy() {
        // arrange
        var policyId = UUID.randomUUID().toString();
        var membershipElement = buildAtomicElement("Membership", OperatorDto.EQ, "active");
        var purposeElement = buildAtomicElement("PURPOSE", OperatorDto.EQ, "ID 3.1 Trace");
        var andElement = new PolicyElement()
                .constraintType(AND)
                .constraintElements(List.of(membershipElement, purposeElement));
        var createRequest = new GenericPolicyCreateRequest(policyId, List.of(andElement));

        // act
        var response = client.uiApi().createGenericPolicyDefinition(createRequest);

        // assert
        assertThat(response.getId()).isEqualTo(policyId);
        var policyById = getPolicyById(policyId);
        assertThat(policyById).isPresent();
        var policyDefinitionDto = policyById.get();
        assertEquals(policyId, policyDefinitionDto.getPolicyDefinitionId());
        var permission = getPermissionJsonObject(policyDefinitionDto.getPolicy().getPolicyJsonLd());
        var action = permission.get(Prop.Odrl.ACTION);
        assertEquals(Prop.Odrl.USE, action.asJsonObject().getString(Prop.Odrl.TYPE));

        var permissionConstraints = permission.get(Prop.Odrl.CONSTRAINT).asJsonArray();
        assertThat(permissionConstraints).hasSize(1);
        var andConstraints = permissionConstraints.get(0).asJsonObject().get(Prop.Odrl.AND).asJsonArray();
        assertThat(andConstraints).hasSize(2);

        var membershipConstraint = andConstraints.get(0).asJsonObject();
        var purposeConstraint = andConstraints.get(1).asJsonObject();
        assertAtomicConstraint(membershipConstraint, "Membership", "active");
        assertAtomicConstraint(purposeConstraint, "PURPOSE", "ID 3.1 Trace");
    }

    private static JsonObject getPermissionJsonObject(String policyJsonLdString) {
        var jsonReader = Json.createReader(new StringReader(policyJsonLdString));
        var jsonObject = jsonReader.readObject();
        var permissionList = jsonObject.get(Prop.Odrl.PERMISSION);
        return permissionList.asJsonArray().get(0).asJsonObject();
    }

    private void assertAtomicConstraint(JsonObject atomicConstraint, String left, String right) {
        var leftOperand = atomicConstraint.getJsonObject(Prop.Odrl.LEFT_OPERAND);
        assertEquals(left, leftOperand.getString("@value"));
        var rightOperand = atomicConstraint.getJsonObject(Prop.Odrl.RIGHT_OPERAND);
        assertEquals(right, rightOperand.getString("@value"));
    }

    private PolicyElement buildAtomicElement(
            String left,
            OperatorDto operator,
            String right) {
        var atomicConstraint = new UiPolicyConstraint()
                .left(left)
                .operator(operator)
                .right(new UiPolicyLiteral()
                        .type(UiPolicyLiteralType.STRING)
                        .value(right));
        return new PolicyElement()
                .constraintType(ATOMIC)
                .atomicConstraint(atomicConstraint);
    }

    @NotNull
    private Optional<PolicyDefinitionDto> getPolicyById(String policyId) {
        var policyDefinitionsResponse = client.uiApi().getPolicyDefinitionPage();
        return policyDefinitionsResponse.getPolicies().stream()
                .filter(policy -> policy.getPolicyDefinitionId().equals(policyId))
                .findFirst();
    }

    @Test
    void test_sorting(PolicyDefinitionService policyDefinitionService) {
        // arrange
        createPolicyDefinition(policyDefinitionService, "my-policy-def-2", 1628956802000L);
        createPolicyDefinition(policyDefinitionService, "my-policy-def-0", 1628956800000L);
        createPolicyDefinition(policyDefinitionService, "my-policy-def-1", 1628956801000L);

        // act
        var result = client.uiApi().getPolicyDefinitionPage();

        // assert
        assertThat(result.getPolicies())
                .extracting(PolicyDefinitionDto::getPolicyDefinitionId)
                .containsExactly("always-true", "my-policy-def-2", "my-policy-def-1", "my-policy" +
                        "-def-0");
    }

    @Test
    void test_delete(PolicyDefinitionService policyDefinitionService) {
        // arrange
        createPolicyDefinition("my-policy-def-1");
        assertThat(policyDefinitionService.query(QuerySpec.max()).getContent().toList())
                .extracting(Entity::getId).contains("always-true", "my-policy-def-1");

        // act
        var response = client.uiApi().deletePolicyDefinition("my-policy-def-1");

        // assert
        assertThat(response.getId()).isEqualTo("my-policy-def-1");
        assertThat(policyDefinitionService.query(QuerySpec.max()).getContent())
                .extracting(Entity::getId).containsExactly("always-true");
    }

    private void createPolicyDefinition(String policyDefinitionId) {
        var policy = new UiPolicyCreateRequest(List.of(constraint));
        var policyDefinition = new PolicyDefinitionCreateRequest(policyDefinitionId, policy);
        client.uiApi().createPolicyDefinition(policyDefinition);
    }

    @SneakyThrows
    private void createPolicyDefinition(PolicyDefinitionService policyDefinitionService,
                                        String policyDefinitionId, long createdAt) {
        createPolicyDefinition(policyDefinitionId);
        var policyDefinition = policyDefinitionService.findById(policyDefinitionId);

        // Forcefully overwrite createdAt
        var createdAtField = Entity.class.getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        createdAtField.set(policyDefinition, createdAt);
        policyDefinitionService.update(policyDefinition);
    }
}
