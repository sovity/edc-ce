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

package de.sovity.edc.e2e;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.TransferProcessSimplifiedState;
import de.sovity.edc.client.gen.model.UiPolicyExpressionType;
import de.sovity.edc.extension.e2e.extension.Consumer;
import de.sovity.edc.extension.e2e.extension.E2eScenario;
import de.sovity.edc.extension.e2e.extension.E2eTestExtension;
import de.sovity.edc.extension.e2e.extension.Provider;
import de.sovity.edc.extension.policy.AlwaysTruePolicyConstants;
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.ws.rs.HttpMethod;
import lombok.val;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.util.Map;

import static de.sovity.edc.extension.e2e.extension.CeE2eTestExtensionConfigFactory.withModule;
import static org.assertj.core.api.Assertions.assertThat;

class AlwaysTrueMigrationTest {

    @RegisterExtension
    private static final E2eTestExtension E2E_TEST_EXTENSION = new E2eTestExtension(
        withModule(":launchers:connectors:sovity-dev")
            .consumerConfigCustomizer(config -> config.getProperties()
                .put("edc.flyway.additional.migration.locations", "classpath:db/additional-test-data/always-true-policy-migrated"))
            .providerConfigCustomizer(config -> config.getProperties()
                .put("edc.flyway.additional.migration.locations", "classpath:db/additional-test-data/always-true-policy-legacy"))
            .build()
    );

    @Test
    @DisabledOnGithub
    void test_migrated_policy_working_test_legacy_policy_working(
        E2eScenario scenario,
        ClientAndServer mockServer,
        @Provider EdcClient providerClient,
        @Consumer EdcClient consumerClient
    ) {
        // assert correct policies
        val oldAlwaysTruePolicyCreatedAfterMigration = providerClient.uiApi().getPolicyDefinitionPage().getPolicies().stream().filter(
            policy -> policy.getPolicyDefinitionId().equals(AlwaysTruePolicyConstants.POLICY_DEFINITION_ID)
        ).findFirst().orElseThrow().getPolicy().getExpression();

        val migratedAlwaysTruePolicy = consumerClient.uiApi().getPolicyDefinitionPage().getPolicies().stream().filter(
            policy -> policy.getPolicyDefinitionId().equals(AlwaysTruePolicyConstants.POLICY_DEFINITION_ID)
        ).findFirst().orElseThrow().getPolicy().getExpression();

        assertThat(oldAlwaysTruePolicyCreatedAfterMigration.getType()).isEqualTo(UiPolicyExpressionType.CONSTRAINT);
        assertThat(oldAlwaysTruePolicyCreatedAfterMigration.getConstraint().getLeft()).isEqualTo(
            AlwaysTruePolicyConstants.EXPRESSION_LEFT_VALUE);
        assertThat(oldAlwaysTruePolicyCreatedAfterMigration.getConstraint().getRight().getValue()).isEqualTo(
            AlwaysTruePolicyConstants.EXPRESSION_RIGHT_VALUE);
        assertThat(oldAlwaysTruePolicyCreatedAfterMigration.getConstraint().getOperator()).isEqualTo(OperatorDto.EQ);

        assertThat(migratedAlwaysTruePolicy.getType()).isEqualTo(UiPolicyExpressionType.EMPTY);
        assertThat(migratedAlwaysTruePolicy.getConstraint()).isNull();

        testTransfer(scenario, mockServer, consumerClient);
    }

    public static void testTransfer(E2eScenario scenario, ClientAndServer mockServer, EdcClient consumerClient) {
        // arrange
        val destinationPath = "/destination/some/path/";
        val destinationUrl = "http://localhost:" + mockServer.getPort() + destinationPath;
        mockServer.when(HttpRequest.request(destinationPath).withMethod("POST")).respond(it -> HttpResponse.response().withStatusCode(200));

        val asset = scenario.createAsset();
        scenario.createContractDefinition(asset); //this automatically uses the always-true policy
        val negotiation = scenario.negotiateAssetAndAwait(asset);

        // act
        val transfer = scenario.transferAndAwait(
            InitiateTransferRequest.builder()
                .contractAgreementId(negotiation.getContractAgreementId())
                .dataSinkProperties(
                    Map.of(
                        Prop.Edc.BASE_URL, destinationUrl,
                        Prop.Edc.METHOD, HttpMethod.POST,
                        Prop.Edc.TYPE, "HttpData"
                    )
                )
                .build()
        );
        val transferProcess = consumerClient.uiApi().getTransferHistoryPage().getTransferEntries().stream().filter(
            process -> process.getTransferProcessId().equals(transfer)
        ).findFirst().orElseThrow();

        // assert
        AssertionsForClassTypes.assertThat(transferProcess.getState().getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.OK);
    }
}
