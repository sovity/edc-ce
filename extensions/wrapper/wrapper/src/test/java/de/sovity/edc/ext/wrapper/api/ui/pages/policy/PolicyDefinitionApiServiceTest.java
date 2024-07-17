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
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.client.gen.model.PolicyDefinitionDto;
import de.sovity.edc.client.gen.model.UiPolicyConstraint;
import de.sovity.edc.client.gen.model.UiPolicyCreateRequest;
import de.sovity.edc.client.gen.model.UiPolicyLiteral;
import de.sovity.edc.client.gen.model.UiPolicyLiteralType;
import de.sovity.edc.ext.db.jooq.Tables;
import de.sovity.edc.extension.db.directaccess.DslContextFactory;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.db.EdcRuntimeExtensionWithTestDatabase;
import lombok.SneakyThrows;
import lombok.val;
import org.eclipse.edc.connector.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.spi.entity.Entity;
import org.eclipse.edc.spi.query.QuerySpec;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.Map;

import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
class PolicyDefinitionApiServiceTest {
    private static ConnectorConfig config;
    private static EdcClient client;

    @RegisterExtension
    static EdcRuntimeExtensionWithTestDatabase providerExtension = new EdcRuntimeExtensionWithTestDatabase(
        ":launchers:connectors:sovity-dev",
        "provider",
        testDatabase -> {
            config = forTestDatabase("my-edc-participant-id", testDatabase);
            client = EdcClient.builder()
                .managementApiUrl(config.getManagementEndpoint().getUri().toString())
                .managementApiKey(config.getProperties().get("edc.api.auth.key"))
                .build();
            return config.getProperties();
        }
    );

    UiPolicyConstraint constraint = UiPolicyConstraint.builder()
        .left("a")
        .operator(OperatorDto.EQ)
        .right(UiPolicyLiteral.builder()
            .type(UiPolicyLiteralType.STRING)
            .value("b")
            .build())
        .build();

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
    void sortPoliciesFromNewestToOldest(DslContextFactory dslContextFactory) {
        // arrange
        createPolicyDefinition("my-policy-def-0");
        createPolicyDefinition("my-policy-def-1");
        createPolicyDefinition("my-policy-def-2");

        dslContextFactory.transaction(dsl ->
            Map.of(
                "my-policy-def-0", 1628956800000L,
                "my-policy-def-1", 1628956801000L,
                "my-policy-def-2", 1628956802000L
            ).forEach((id, time) -> setPolicyDefCreatedAt(dsl, id, time)));

        // act
        var result = client.uiApi().getPolicyDefinitionPage();

        // assert
        assertThat(result.getPolicies())
            .extracting(PolicyDefinitionDto::getPolicyDefinitionId)
            .containsExactly(
                "always-true",
                "my-policy-def-2",
                "my-policy-def-1",
                "my-policy-def-0");
    }

    private static void setPolicyDefCreatedAt(DSLContext dsl, String id, Long time) {
        val p = Tables.EDC_POLICYDEFINITIONS;
        dsl.update(p)
            .set(p.CREATED_AT, time)
            .where(p.POLICY_ID.eq(id))
            .execute();
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

}
