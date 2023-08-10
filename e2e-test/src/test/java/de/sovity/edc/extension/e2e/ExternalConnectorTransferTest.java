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
import de.sovity.edc.extension.e2e.connector.DataTransferTestUtil;
import de.sovity.edc.extension.e2e.connector.JsonLdConnectorUtil;
import de.sovity.edc.extension.e2e.connector.factory.EnvConnectorFactoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.UUID;

import static de.sovity.edc.extension.e2e.TransferTestVariables.CONSUMER_TARGET_URL;
import static de.sovity.edc.extension.e2e.TransferTestVariables.PROVIDER_TARGET_URL;
import static de.sovity.edc.extension.e2e.TransferTestVariables.TEST_BACKEND_CHECK_URL;
import static de.sovity.edc.extension.e2e.TransferTestVariables.TEST_BACKEND_TEST_DATA;
import static de.sovity.edc.extension.e2e.env.EnvUtil.loadRequiredVariable;

@EnabledIfEnvironmentVariable(
        named = "E2E_TEST_EXTERNAL_CONNECTOR_ENABLED",
        matches = "true")
public class ExternalConnectorTransferTest {

    private static final String VAR_PROVIDER_PARTICIPANT_ID = "PROVIDER_PARTICIPANT_ID";
    private static final String VAR_CONSUMER_PARTICIPANT_ID = "CONSUMER_PARTICIPANT_ID";
    private Connector providerConnector;
    private Connector consumerConnector;
    private String providerTargetUrl;
    private String consumerTargetUrl;
    private String testBackendCheckUrl;
    private String testBackendTestData;

    @BeforeEach
    void setUp() {
        initConnectors();
        providerTargetUrl = loadRequiredVariable(PROVIDER_TARGET_URL);
        consumerTargetUrl = loadRequiredVariable(CONSUMER_TARGET_URL);
        testBackendCheckUrl = System.getenv(TEST_BACKEND_CHECK_URL);
        if (testBackendCheckUrl != null) {
            testBackendTestData = loadRequiredVariable(TEST_BACKEND_TEST_DATA);
        }
    }

    private void initConnectors() {
        var envConnectorFactory = new EnvConnectorFactoryImpl();
        var providerParticipantId = loadRequiredVariable(VAR_PROVIDER_PARTICIPANT_ID);
        providerConnector = envConnectorFactory.createConnector(providerParticipantId);
        var consumerParticipantId = loadRequiredVariable(VAR_CONSUMER_PARTICIPANT_ID);
        consumerConnector = envConnectorFactory.createConnector(consumerParticipantId);
    }

    @Test
    void createAndConsumeOffer() {
        var assetId = UUID.randomUUID().toString();
        DataTransferTestUtil.createTestOffer(
                providerConnector,
                assetId,
                providerTargetUrl);
        consumerConnector.consumeOffer(
                providerConnector.getParticipantId(),
                providerConnector.getProtocolApiUri(),
                assetId,
                JsonLdConnectorUtil.httpDataAddress(consumerTargetUrl));

        if (testBackendCheckUrl != null) {
            DataTransferTestUtil.validateDataTransferred(
                    testBackendCheckUrl,
                    testBackendTestData);
        }
    }

}