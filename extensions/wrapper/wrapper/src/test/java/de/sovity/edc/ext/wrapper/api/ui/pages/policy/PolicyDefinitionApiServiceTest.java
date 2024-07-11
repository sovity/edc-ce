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
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.db.EdcRuntimeExtensionWithTestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseFactory;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.junit.extensions.EdcRuntimeExtension;
import org.eclipse.edc.spi.entity.Entity;
import org.eclipse.edc.spi.query.QuerySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
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

    // TODO: find another workaround to set the date and time
    @Disabled
    @Test
    void sortPoliciesFromNewestToOldest() {
        // arrange
        createPolicyDefinition("my-policy-def-0");
        createPolicyDefinition("my-policy-def-1");
        createPolicyDefinition("my-policy-def-2");

        // act
        var result = client.uiApi().getPolicyDefinitionPage();

        // assert
        assertThat(result.getPolicies())
                .extracting(PolicyDefinitionDto::getPolicyDefinitionId)
            // TODO: this is weird: the always true policy is supposed to be the oldest, but it's created with the init time when it got inserted for the first time, which is *after* the timestamp of the other definitions
                .containsExactly(
                        "always-true",
                        "my-policy-def-2",
                        "my-policy-def-1",
                        "my-policy-def-0");
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
    private void createPolicyDefinition(
            PolicyDefinitionService policyDefinitionService,
            String policyDefinitionId,
            long createdAt) {
        createPolicyDefinition(policyDefinitionId);
        var policyDefinition = policyDefinitionService.findById(policyDefinitionId);

        // Forcefully overwrite createdAt
        var createdAtField = Entity.class.getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        createdAtField.set(policyDefinition, createdAt);
        policyDefinitionService.update(policyDefinition);
    }
}
