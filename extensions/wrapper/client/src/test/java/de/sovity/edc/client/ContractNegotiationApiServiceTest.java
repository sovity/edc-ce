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

package de.sovity.edc.client;

import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.MockDataAddressRemote;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.UUID;

import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtil.validateDataTransferred;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.basicEdcConfig;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;

@ApiTest
@ExtendWith(EdcExtension.class)
class ContractNegotiationApiServiceTest {


    private static final String PROVIDER_PARTICIPANT_ID = "provider";
    private static final String CONSUMER_PARTICIPANT_ID = "consumer";
    private static final String TEST_BACKEND_TEST_DATA = UUID.randomUUID().toString();

    @RegisterExtension
    static EdcExtension providerEdcContext = new EdcExtension();
    @RegisterExtension
    static EdcExtension consumerEdcContext = new EdcExtension();

    private ConnectorRemote providerConnector;
    private ConnectorRemote consumerConnector;
    private MockDataAddressRemote dataAddress;

    @BeforeEach
    void setup() {
        var providerConfig = basicEdcConfig(PROVIDER_PARTICIPANT_ID, 24000);
        providerEdcContext.setConfiguration(providerConfig.getProperties());
        providerConnector = new ConnectorRemote(fromConnectorConfig(providerConfig));

        var consumerConfig = basicEdcConfig(CONSUMER_PARTICIPANT_ID, 25000);
        consumerEdcContext.setConfiguration(consumerConfig.getProperties());
        consumerConnector = new ConnectorRemote(fromConnectorConfig(consumerConfig));

        // We use the provider EDC as data sink / data source (it has the test-backend-controller extension)
        dataAddress = new MockDataAddressRemote(providerConnector.getConfig().getDefaultEndpoint());
    }

    @Test
    void testContractNegotiation() {
        // arrange
        var assetId = UUID.randomUUID().toString();
        providerConnector.createDataOffer(assetId, dataAddress.getDataSourceUrl(TEST_BACKEND_TEST_DATA));

        // act
        consumerConnector.consumeOffer(
                providerConnector.getParticipantId(),
                providerConnector.getConfig().getProtocolEndpoint().getUri(),
                assetId,
                dataAddress.getDataSinkJsonLd());

        // assert
        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), TEST_BACKEND_TEST_DATA);
    }
}
