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

import de.sovity.edc.extension.e2e.connector.remotes.management_api.ManagementApiConnectorRemote;
import de.sovity.edc.extension.e2e.connector.remotes.test_backend_controller.TestBackendRemote;
import de.sovity.edc.extension.e2e.junit.CeE2eTestExtension;
import de.sovity.edc.extension.e2e.junit.utils.Consumer;
import de.sovity.edc.extension.e2e.junit.utils.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.UUID;

import static de.sovity.edc.extension.e2e.connector.remotes.management_api.DataTransferTestUtil.validateDataTransferred;

class ManagementApiTransferTest {

    @RegisterExtension
    private static CeE2eTestExtension e2eTestExtension = CeE2eTestExtension.builder()
        .additionalModule(":launchers:connectors:sovity-dev")
        .build();

    private TestBackendRemote dataAddress;
    private static final String TEST_BACKEND_TEST_DATA = UUID.randomUUID().toString();

    @BeforeEach
    void setup(@Provider ManagementApiConnectorRemote providerConnector) {
        // We use the provider EDC as data sink / data source (it has the test-backend-controller extension)
        dataAddress = new TestBackendRemote(providerConnector.getConfig().getDefaultApiUrl());
    }

    @Test
    void testDataTransfer(
        @Consumer ManagementApiConnectorRemote consumerConnector,
        @Provider ManagementApiConnectorRemote providerConnector
    ) {
        // arrange
        var assetId = UUID.randomUUID().toString();
        providerConnector.createDataOffer(assetId, dataAddress.getDataSourceUrl(TEST_BACKEND_TEST_DATA));

        // act
        consumerConnector.consumeOffer(
            providerConnector.getParticipantId(),
            providerConnector.getConfig().getProtocolApiUrl(),
            assetId,
            dataAddress.getDataSinkJsonLd());

        // assert
        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), TEST_BACKEND_TEST_DATA);
    }
}
