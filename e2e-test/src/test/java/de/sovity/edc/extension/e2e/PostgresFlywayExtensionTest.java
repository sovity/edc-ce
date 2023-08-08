/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.extension.e2e;

import de.sovity.edc.extension.e2e.connector.Connector;
import de.sovity.edc.extension.e2e.connector.JsonLdConnectorUtil;
import de.sovity.edc.extension.e2e.connector.factory.TestConnectorFactory;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseFactory;
import jakarta.json.JsonObject;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static de.sovity.edc.extension.e2e.connector.config.EdcApiGroup.PROTOCOL;
import static jakarta.json.Json.createObjectBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.eclipse.edc.connector.transfer.spi.types.TransferProcessStates.COMPLETED;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.CONTEXT;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;
import static org.eclipse.edc.jsonld.spi.PropertyAndTypeNames.ODRL_POLICY_ATTRIBUTE;

@EnabledIfEnvironmentVariable(
        named = "E2E_TEST_ENABLED",
        matches = "true")
public class PostgresFlywayExtensionTest {

    private static final String PROVIDER_TARGET_URL = "https://google.de";
    private static final String CONSUMER_BACKEND_URL = "https://webhook.site/e542f69e-ff0a-4771-af18-0900a399137a";
    private static final Duration TIMEOUT = Duration.ofSeconds(60);
    private static final String MIGRATED_M8_ASSET_ID = "test-1.0";

    @RegisterExtension
    static EdcExtension providerEdcContext = new EdcExtension();
    @RegisterExtension
    static EdcExtension consumerEdcContext = new EdcExtension();

    @RegisterExtension
    private static final TestDatabase PROVIDER_DATABASE = TestDatabaseFactory.getTestDatabase();
    @RegisterExtension
    private static final TestDatabase CONSUMER_DATABASE = TestDatabaseFactory.getTestDatabase();

    private Connector providerConnector;
    private Connector consumerConnector;

    @BeforeEach
    void setUp() {
        providerConnector = new TestConnectorFactory(
                "provider",
                providerEdcContext,
                PROVIDER_DATABASE)
                .createConnector();
        consumerConnector = new TestConnectorFactory(
                "consumer",
                consumerEdcContext,
                CONSUMER_DATABASE)
                .createConnector();
    }

    @Test
    void consumeM8Offer() {
        var assetIds = providerConnector.getAssetIds();
        assertThat(assetIds).contains(MIGRATED_M8_ASSET_ID);
        var providerProtocolApi = providerConnector.getUriForApi(PROTOCOL);
        consumeOffer(providerProtocolApi, MIGRATED_M8_ASSET_ID);
    }

    @Test
    void createAndConsumeOffer() {
        var assetId = UUID.randomUUID().toString();
        createContractOffer(assetId);
        var providerProtocolApi = providerConnector.getUriForApi(PROTOCOL);
        consumeOffer(providerProtocolApi, assetId);
    }

    private void consumeOffer(URI providerProtocolApi, String assetId) {
        var dataset = consumerConnector.getDatasetForAsset(assetId, providerProtocolApi);
        var contractId = JsonLdConnectorUtil.getContractId(dataset);
        var policy = dataset.getJsonArray(ODRL_POLICY_ATTRIBUTE).get(0).asJsonObject();

        var contractAgreementId = consumerConnector.negotiateContract(
                providerConnector.getParticipantId(),
                providerProtocolApi,
                contractId.toString(),
                contractId.assetIdPart(),
                policy);

        var destination = JsonLdConnectorUtil.httpDataAddress(CONSUMER_BACKEND_URL);
        var transferProcessId = consumerConnector.initiateTransfer(contractAgreementId, assetId,
                providerProtocolApi, destination);

        assertThat(transferProcessId).isNotNull();

        await().atMost(TIMEOUT).untilAsserted(() -> {
            var state = consumerConnector.getTransferProcessState(transferProcessId);
            assertThat(state).isEqualTo(COMPLETED.name());
        });
    }

    private void createContractOffer(String assetId) {
        var contractDefinitionId = UUID.randomUUID().toString();
        providerConnector.createAsset(assetId, httpDataAddressProperties());
        var noConstraintPolicyId = providerConnector.createPolicy(noConstraintPolicy());
        providerConnector.createContractDefinition(
                assetId,
                contractDefinitionId,
                noConstraintPolicyId,
                noConstraintPolicyId);
    }

    private JsonObject noConstraintPolicy() {
        return createObjectBuilder()
                .add(CONTEXT, "https://www.w3.org/ns/odrl.jsonld")
                .add(TYPE, "use")
                .build();
    }

    private Map<String, Object> httpDataAddressProperties() {
        return Map.of(
                "name", "transfer-test",
                "baseUrl", PROVIDER_TARGET_URL,
                "type", "HttpData",
                "proxyQueryParams", "true"
        );
    }
}