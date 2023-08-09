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
import de.sovity.edc.extension.e2e.connector.DataTransferTestUtils;
import de.sovity.edc.extension.e2e.connector.JsonLdConnectorUtil;
import de.sovity.edc.extension.e2e.connector.TestConnector;
import de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroupConfig;
import de.sovity.edc.extension.e2e.connector.config.api.auth.ApiKeyAuthProvider;
import de.sovity.edc.extension.e2e.connector.config.api.auth.NoneAuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import static de.sovity.edc.extension.e2e.TransferTestVariables.CONSUMER_EDC_MANAGEMENT_AUTH_HEADER;
import static de.sovity.edc.extension.e2e.TransferTestVariables.CONSUMER_EDC_MANAGEMENT_AUTH_VALUE;
import static de.sovity.edc.extension.e2e.TransferTestVariables.CONSUMER_EDC_MANAGEMENT_URL;
import static de.sovity.edc.extension.e2e.TransferTestVariables.CONSUMER_EDC_PROTOCOL_URL;
import static de.sovity.edc.extension.e2e.TransferTestVariables.CONSUMER_PARTICIPANT_ID;
import static de.sovity.edc.extension.e2e.TransferTestVariables.CONSUMER_TARGET_URL;
import static de.sovity.edc.extension.e2e.TransferTestVariables.PROVIDER_EDC_MANAGEMENT_AUTH_HEADER;
import static de.sovity.edc.extension.e2e.TransferTestVariables.PROVIDER_EDC_MANAGEMENT_AUTH_VALUE;
import static de.sovity.edc.extension.e2e.TransferTestVariables.PROVIDER_EDC_MANAGEMENT_URL;
import static de.sovity.edc.extension.e2e.TransferTestVariables.PROVIDER_EDC_PROTOCOL_URL;
import static de.sovity.edc.extension.e2e.TransferTestVariables.PROVIDER_PARTICIPANT_ID;
import static de.sovity.edc.extension.e2e.TransferTestVariables.PROVIDER_TARGET_URL;
import static de.sovity.edc.extension.e2e.TransferTestVariables.TEST_BACKEND_CHECK_URL;
import static de.sovity.edc.extension.e2e.TransferTestVariables.TEST_BACKEND_TEST_DATA;

@EnabledIfEnvironmentVariable(
        named = "E2E_TEST_EXTERNAL_CONNECTOR_ENABLED",
        matches = "true")
public class ExternalConnectorTransferTest {
    private Connector providerConnector;
    private Connector consumerConnector;
    private String providerTargetUrl;
    private String consumerTargetUrl;
    private String testBackendCheckUrl;
    private String testBackendTestData;

    @BeforeEach
    void setUp() throws URISyntaxException {
        providerConnector = TestConnector.builder()
                .participantId(loadRequiredVariable(PROVIDER_PARTICIPANT_ID))
                .managementApiGroupConfig(getProviderManagementApiGroupConfig())
                .protocolApiGroupConfig(getProtocolApiGroupConfig(PROVIDER_EDC_PROTOCOL_URL))
                .build();
        consumerConnector = TestConnector.builder()
                .participantId(loadRequiredVariable(CONSUMER_PARTICIPANT_ID))
                .managementApiGroupConfig(getConsumerManagementApiGroupConfig())
                .protocolApiGroupConfig(getProtocolApiGroupConfig(CONSUMER_EDC_PROTOCOL_URL))
                .build();
        providerTargetUrl = loadRequiredVariable(PROVIDER_TARGET_URL);
        consumerTargetUrl = loadRequiredVariable(CONSUMER_TARGET_URL);
        testBackendCheckUrl = System.getenv(TEST_BACKEND_CHECK_URL);
        if (testBackendCheckUrl != null) {
            testBackendTestData = loadRequiredVariable(TEST_BACKEND_TEST_DATA);
        }
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
        return EdcApiGroupConfig.protocolFromUri(
                getUriFromEnv(variableName),
                new NoneAuthProvider());
    }

    private EdcApiGroupConfig getProviderManagementApiGroupConfig() throws URISyntaxException {
        return EdcApiGroupConfig.mgntFromUri(
                getUriFromEnv(PROVIDER_EDC_MANAGEMENT_URL),
                new ApiKeyAuthProvider(
                        loadRequiredVariable(PROVIDER_EDC_MANAGEMENT_AUTH_HEADER),
                        loadRequiredVariable(PROVIDER_EDC_MANAGEMENT_AUTH_VALUE)));
    }

    private EdcApiGroupConfig getConsumerManagementApiGroupConfig() throws URISyntaxException {
        return EdcApiGroupConfig.mgntFromUri(
                getUriFromEnv(CONSUMER_EDC_MANAGEMENT_URL),
                new ApiKeyAuthProvider(
                        loadRequiredVariable(CONSUMER_EDC_MANAGEMENT_AUTH_HEADER),
                        loadRequiredVariable(CONSUMER_EDC_MANAGEMENT_AUTH_VALUE)));
    }

    private URI getUriFromEnv(String variableName) throws URISyntaxException {
        return new URI(loadRequiredVariable(variableName));
    }

    @Test
    void createAndConsumeOffer() {
        var assetId = UUID.randomUUID().toString();
        DataTransferTestUtils.createTestOffer(
                providerConnector,
                assetId,
                providerTargetUrl);
        consumerConnector.consumeOffer(
                loadRequiredVariable(PROVIDER_PARTICIPANT_ID),
                providerConnector.getProtocolApiUri(),
                assetId,
                JsonLdConnectorUtil.httpDataAddress(consumerTargetUrl));

        if (testBackendCheckUrl != null) {
            DataTransferTestUtils.validateDataTransferred(
                    testBackendCheckUrl,
                    testBackendTestData);
        }
    }

}