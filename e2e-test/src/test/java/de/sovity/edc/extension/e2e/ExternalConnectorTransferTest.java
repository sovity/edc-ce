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
import de.sovity.edc.extension.e2e.connector.ConnectorTestUtils;
import de.sovity.edc.extension.e2e.connector.JsonLdConnectorUtil;
import de.sovity.edc.extension.e2e.connector.TestConnector;
import de.sovity.edc.extension.e2e.connector.config.EdcApiGroupConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import static de.sovity.edc.extension.e2e.connector.ConnectorTestUtils.CONSUMER_BACKEND_URL;
import static de.sovity.edc.extension.e2e.connector.ConnectorTestUtils.PROVIDER_TARGET_URL;
import static de.sovity.edc.extension.e2e.connector.ConnectorTestUtils.TEST_BACKEND_CHECK_URL;
import static de.sovity.edc.extension.e2e.connector.ConnectorTestUtils.TEST_BACKEND_TEST_DATA;

@EnabledIfEnvironmentVariable(
        named = "E2E_TEST_EXTERNAL_CONNECTOR_ENABLED",
        matches = "true")
public class ExternalConnectorTransferTest {

    private static final String PROVIDER_EDC_MANAGEMENT_URL = "PROVIDER_EDC_MANAGEMENT_URL";
    private static final String PROVIDER_EDC_PROTOCOL_URL = "PROVIDER_EDC_PROTOCOL_URL";
    private static final String CONSUMER_EDC_MANAGEMENT_URL = "CONSUMER_EDC_MANAGEMENT_URL";
    private static final String CONSUMER_EDC_PROTOCOL_URL = "CONSUMER_EDC_PROTOCOL_URL";
    private static final String PROVIDER_PARTICIPANT_ID = "PROVIDER_PARTICIPANT_ID";
    private static final String CONSUMER_PARTICIPANT_ID = "CONSUMER_PARTICIPANT_ID";
    private Connector providerConnector;
    private Connector consumerConnector;

    @BeforeEach
    void setUp() throws URISyntaxException {
        providerConnector = TestConnector.builder()
                .participantId(loadRequiredVariable(PROVIDER_PARTICIPANT_ID))
                .managementApiGroupConfig(getManagementApiGroupConfig(PROVIDER_EDC_MANAGEMENT_URL))
                .protocolApiGroupConfig(getProtocolApiGroupConfig(PROVIDER_EDC_PROTOCOL_URL))
                .build();
        consumerConnector = TestConnector.builder()
                .participantId(loadRequiredVariable(CONSUMER_PARTICIPANT_ID))
                .managementApiGroupConfig(getManagementApiGroupConfig(CONSUMER_EDC_MANAGEMENT_URL))
                .protocolApiGroupConfig(getProtocolApiGroupConfig(CONSUMER_EDC_PROTOCOL_URL))
                .build();
    }

    private String loadRequiredVariable(String variableName) {
        return Optional.ofNullable(System.getenv(variableName))
                .orElseThrow(() -> {
                    var message = String.format(
                            "Missing required environment variable: %s",
                            variableName);
                    return new IllegalArgumentException(message);
                });
    }

    private EdcApiGroupConfig getProtocolApiGroupConfig(String variableName) throws URISyntaxException {
        return EdcApiGroupConfig.protocolFromUri(getUriFromEnv(variableName));
    }

    private EdcApiGroupConfig getManagementApiGroupConfig(String variableName) throws URISyntaxException {
        return EdcApiGroupConfig.mgntFromUri(getUriFromEnv(variableName));
    }

    private URI getUriFromEnv(String variableName) throws URISyntaxException {
        return new URI(loadRequiredVariable(variableName));
    }

    @Test
    void createAndConsumeOffer() {
        var assetId = UUID.randomUUID().toString();
        ConnectorTestUtils.createTestOffer(
                providerConnector,
                assetId,
                PROVIDER_TARGET_URL);
        consumerConnector.consumeOffer(
                providerConnector.getProtocolApiUri(),
                assetId,
                JsonLdConnectorUtil.httpDataAddress(CONSUMER_BACKEND_URL));
        ConnectorTestUtils.validateDataTransferred(TEST_BACKEND_CHECK_URL, TEST_BACKEND_TEST_DATA);
    }

}