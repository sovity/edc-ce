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
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.e2e;

import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.MockDataAddressRemote;
import de.sovity.edc.extension.e2e.extension.Consumer;
import de.sovity.edc.extension.e2e.extension.E2eTestExtension;
import de.sovity.edc.extension.e2e.extension.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtil.validateDataTransferred;

@ExtendWith(E2eTestExtension.class)
class ManagementApiTransferTest {

    private MockDataAddressRemote dataAddress;
    private static final String TEST_BACKEND_TEST_DATA = UUID.randomUUID().toString();

    @BeforeEach
    void setup(@Provider ConnectorRemote providerConnector) {
        // We use the provider EDC as data sink / data source (it has the test-backend-controller extension)
        dataAddress = new MockDataAddressRemote(providerConnector.getConfig().getDefaultEndpoint());
    }

    @Test
    void testDataTransfer(@Consumer ConnectorRemote consumerConnector, @Provider ConnectorRemote providerConnector) {
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
