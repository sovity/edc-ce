/*
 * Copyright (c) 2024 sovity GmbH
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
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateDto;
import de.sovity.edc.client.gen.model.PolicyDefinitionDto;
import de.sovity.edc.client.gen.model.UiPolicyConstraint;
import de.sovity.edc.client.gen.model.UiPolicyExpression;
import de.sovity.edc.client.gen.model.UiPolicyExpressionType;
import de.sovity.edc.client.gen.model.UiPolicyLiteral;
import de.sovity.edc.client.gen.model.UiPolicyLiteralType;
import de.sovity.edc.ext.db.jooq.Tables;
import de.sovity.edc.extension.db.directaccess.DslContextFactory;
import de.sovity.edc.extension.e2e.junit.CeIntegrationTestExtension;
import lombok.val;
import org.eclipse.edc.connector.controlplane.services.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.spi.entity.Entity;
import org.eclipse.edc.spi.query.QuerySpec;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PolicyDefinitionApiServiceTest {

    @RegisterExtension
    private static final CeIntegrationTestExtension INTEGRATION_TEST_EXTENSION = CeIntegrationTestExtension.builder()
        .additionalModule(":launchers:connectors:sovity-dev")
        .build();

    UiPolicyExpression expression = UiPolicyExpression.builder()
        .type(UiPolicyExpressionType.CONSTRAINT)
        .constraint(UiPolicyConstraint.builder()
            .left("a")
            .operator(OperatorDto.EQ)
            .right(UiPolicyLiteral.builder()
                .type(UiPolicyLiteralType.STRING)
                .value("b")
                .build())
            .build())
        .build();

    @Test
    void getPolicyList(EdcClient client) {
        // arrange
        val policyDefinitionId = "my-policy-def-1";
        createPolicyDefinition(client, policyDefinitionId);

        // act
        var response = client.uiApi().getPolicyDefinitionPage();

        // assert
        var policyDefinitions = response.getPolicies();
        assertThat(policyDefinitions).hasSize(2);
        var policyDefinition = policyDefinitions.stream()
            .filter(it -> it.getPolicyDefinitionId().equals(policyDefinitionId))
            .findFirst().get();
        assertThat(policyDefinition.getPolicyDefinitionId()).isEqualTo(policyDefinitionId);
        assertThat(policyDefinition.getPolicy().getExpression()).usingRecursiveComparison().isEqualTo(expression);

        // cleanup
        client.uiApi().deletePolicyDefinition(policyDefinitionId);
    }

    @Test
    void sortPoliciesFromNewestToOldest(EdcClient client, DslContextFactory dslContextFactory) {
        // arrange
        createPolicyDefinition(client, "my-policy-def-10");
        createPolicyDefinition(client, "my-policy-def-11");
        createPolicyDefinition(client, "my-policy-def-12");

        dslContextFactory.transaction(dsl ->
            Map.of(
                "my-policy-def-10", 1628956800000L,
                "my-policy-def-11", 1628956801000L,
                "my-policy-def-12", 1628956802000L
            ).forEach((id, time) -> setPolicyDefCreatedAt(dsl, id, time)));

        // act
        var result = client.uiApi().getPolicyDefinitionPage();

        // assert
        assertThat(result.getPolicies())
            .extracting(PolicyDefinitionDto::getPolicyDefinitionId)
            .containsExactly(
                "always-true",
                "my-policy-def-12",
                "my-policy-def-11",
                "my-policy-def-10");

        // cleanup
        List.of(
            "my-policy-def-12",
            "my-policy-def-11",
            "my-policy-def-10"
        ).forEach(id -> client.uiApi().deletePolicyDefinition(id));
    }

    private static void setPolicyDefCreatedAt(DSLContext dsl, String id, Long time) {
        val p = Tables.EDC_POLICYDEFINITIONS;
        dsl.update(p)
            .set(p.CREATED_AT, time)
            .where(p.POLICY_ID.eq(id))
            .execute();
    }

    @Test
    void test_delete(EdcClient client, PolicyDefinitionService policyDefinitionService) {
        // arrange
        String policyDefinitionId = "my-policy-def-21";
        createPolicyDefinition(client, policyDefinitionId);
        assertThat(policyDefinitionService.query(QuerySpec.max()).getContent().toList())
            .extracting(Entity::getId).contains("always-true", policyDefinitionId);

        // act
        var response = client.uiApi().deletePolicyDefinition(policyDefinitionId);

        // assert
        assertThat(response.getId()).isEqualTo(policyDefinitionId);
        assertThat(policyDefinitionService.query(QuerySpec.max()).getContent())
            .extracting(Entity::getId).containsExactly("always-true");
    }

    private void createPolicyDefinition(EdcClient client, String policyDefinitionId) {
        var policyDefinition = new PolicyDefinitionCreateDto(policyDefinitionId, expression);
        client.uiApi().createPolicyDefinitionV2(policyDefinition);
    }

}
