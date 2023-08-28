/*
 *  Copyright (c) 2022 sovity GmbH
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

import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.MockDataAddressRemote;
import de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroup;
import de.sovity.edc.extension.e2e.connector.config.api.auth.NoneAuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.UUID;

import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtil.validateDataTransferred;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.TEST_BACKEND_DEFAULT_ENDPOINT;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.getFromEnv;
import static de.sovity.edc.extension.e2e.connector.config.api.EdcApiConfigFactory.fromUri;
import static de.sovity.edc.extension.e2e.env.EnvUtil.getEnvVar;
import static de.sovity.edc.extension.e2e.env.EnvUtil.getEnvVarUri;

@EnabledIfEnvironmentVariable(named = "E2E_HAS_EXTERNAL_CONNECTORS", matches = "true")
class ExternalConnectorTransferTest {

    private static final String VAR_PROVIDER_PARTICIPANT_ID = "PROVIDER";
    private static final String VAR_CONSUMER_PARTICIPANT_ID = "CONSUMER";
    private ConnectorRemote providerConnector;
    private ConnectorRemote consumerConnector;
    private MockDataAddressRemote dataAddress;

    @BeforeEach
    void setUp() {
        initConnectors();
        initDataAddressRemote();
    }

    private void initConnectors() {
        var providerParticipantId = getEnvVar(VAR_PROVIDER_PARTICIPANT_ID);
        var providerConfig = getFromEnv(providerParticipantId);
        providerConnector = new ConnectorRemote(providerConfig);

        var consumerParticipantId = getEnvVar(VAR_CONSUMER_PARTICIPANT_ID);
        var consumerConfig = getFromEnv(consumerParticipantId);
        consumerConnector = new ConnectorRemote(consumerConfig);
    }

    private void initDataAddressRemote() {
        var endpoint = getEnvVarUri(TEST_BACKEND_DEFAULT_ENDPOINT);
        var endpointConfig = fromUri(EdcApiGroup.DEFAULT, endpoint, new NoneAuthProvider());
        dataAddress = new MockDataAddressRemote(endpointConfig);
    }

    @Test
    void testDataTransferBetweenTwoRunningEdcImages() {
        // arrange
        var assetId = UUID.randomUUID().toString();
        providerConnector.createDataOffer(assetId, dataAddress.getDataSourceUrl("dummy test data"));

        // act
        consumerConnector.consumeOffer(
                providerConnector.getParticipantId(),
                providerConnector.getConfig().getProtocolEndpoint().getUri(),
                assetId,
                dataAddress.getDataSinkJsonLd());

        // assert
        validateDataTransferred(
                dataAddress.getDataSinkSpyUrl(),
                "dummy test data"
        );
    }
}
