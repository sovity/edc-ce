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

package de.sovity.edc.e2e;

import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.MockDataAddressRemote;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseFactory;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.nio.file.Paths;
import java.util.UUID;

import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtil.validateDataTransferred;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static org.assertj.core.api.Assertions.assertThat;

class ConnectorTransferTest {

    private static final String PROVIDER_PARTICIPANT_ID = "provider";
    private static final String CONSUMER_PARTICIPANT_ID = "consumer";
    private static final String TEST_BACKEND_TEST_DATA = UUID.randomUUID().toString();
    private static final String MIGRATED_MS8_ASSET_EXPECTED_DATA = "b130536e-0a51-4d2d-aa8c-7591d6ad5bc8";
    public static final String MIGRATED_MS8_ASSET_ID = "ms8-asset-1.0";

    @RegisterExtension
    static EdcExtension providerEdcContext = new EdcExtension();
    @RegisterExtension
    static EdcExtension consumerEdcContext = new EdcExtension();

    @RegisterExtension
    static final TestDatabase PROVIDER_DATABASE = TestDatabaseFactory.getTestDatabase(1);
    @RegisterExtension
    static final TestDatabase CONSUMER_DATABASE = TestDatabaseFactory.getTestDatabase(2);

    private ConnectorRemote providerConnector;
    private ConnectorRemote consumerConnector;
    private MockDataAddressRemote dataAddress;

    @BeforeEach
    void setup() {
        var providerConfig = forTestDatabase(PROVIDER_PARTICIPANT_ID, 22000, PROVIDER_DATABASE);
        providerConfig.setProperty("edc.flyway.additional.migration.locations",
                "filesystem:%s".formatted(getAbsoluteTestResourcePath("db/additional-test-data/provider")));
        providerEdcContext.setConfiguration(providerConfig.getProperties());
        providerConnector = new ConnectorRemote(fromConnectorConfig(providerConfig));

        var consumerConfig = forTestDatabase(CONSUMER_PARTICIPANT_ID, 23000, CONSUMER_DATABASE);
        consumerConfig.setProperty("edc.flyway.additional.migration.locations",
                "filesystem:%s".formatted(getAbsoluteTestResourcePath("db/additional-test-data/consumer")));
        consumerEdcContext.setConfiguration(consumerConfig.getProperties());
        consumerConnector = new ConnectorRemote(fromConnectorConfig(consumerConfig));

        // We use the provider EDC as data sink / data source (it has the test-backend-controller extension)
        dataAddress = new MockDataAddressRemote(providerConnector.getConfig().getDefaultEndpoint());
    }

    @Test
    void testDataTransfer_freshDataOffer() {
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

    @Test
    void testDataTransfer_migratedMs8DataOffer() {
        // arrange
        var assetIds = providerConnector.getAssetIds();
        assertThat(assetIds).contains(MIGRATED_MS8_ASSET_ID);

        // act
        consumerConnector.consumeOffer(
                providerConnector.getParticipantId(),
                providerConnector.getConfig().getProtocolEndpoint().getUri(),
                MIGRATED_MS8_ASSET_ID,
                dataAddress.getDataSinkJsonLd());

        // assert
        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), MIGRATED_MS8_ASSET_EXPECTED_DATA);
    }

    public String getAbsoluteTestResourcePath(String path) {
        return Paths.get("").resolve("src/test/resources").resolve(path).toAbsolutePath().toString();
    }
}
