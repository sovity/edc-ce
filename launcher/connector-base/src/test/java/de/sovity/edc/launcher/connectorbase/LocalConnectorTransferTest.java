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

package de.sovity.edc.launcher.connectorbase;

import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.DataTransferTestUtil;
import de.sovity.edc.extension.e2e.connector.JsonLdConnectorUtil;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory;
import de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroup;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseFactory;
import org.assertj.core.api.Assertions;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.UUID;

@EnabledIfEnvironmentVariable(
        named = "E2E_TEST_ENABLED",
        matches = "true")
public class LocalConnectorTransferTest {

    private static final String PROVIDER_PARTICIPANT_ID = "provider";
    private static final String CONSUMER_PARTICIPANT_ID = "consumer";
    private static final String TEST_BACKEND_TEST_DATA = UUID.randomUUID().toString();
    private static final String TEST_BACKEND_TEST_DATA_M8 = "b130536e-0a51-4d2d-aa8c-7591d6ad5bc8";

    @RegisterExtension
    static EdcExtension providerEdcContext = new EdcExtension();
    @RegisterExtension
    static EdcExtension consumerEdcContext = new EdcExtension();

    @RegisterExtension
    static final TestDatabase PROVIDER_DATABASE = TestDatabaseFactory.getTestDatabase();
    @RegisterExtension
    static final TestDatabase CONSUMER_DATABASE = TestDatabaseFactory.getTestDatabase();

    private ConnectorRemote providerConnector;
    private ConnectorRemote consumerConnector;

    @BeforeEach
    void setUp() {
        var providerConfig = ConnectorConfigFactory.forTestDatabase(PROVIDER_PARTICIPANT_ID, PROVIDER_DATABASE);
        providerEdcContext.setConfiguration(providerConfig.getConfigAsMap());
        providerConnector = new ConnectorRemote(providerConfig.toConnectorRemoteConfig());

        var consumerConfig = ConnectorConfigFactory.forTestDatabase(CONSUMER_PARTICIPANT_ID, CONSUMER_DATABASE);
        consumerEdcContext.setConfiguration(consumerConfig.getConfigAsMap());
        consumerConnector = new ConnectorRemote(consumerConfig.toConnectorRemoteConfig());
    }

    @Test
    void consumeM8Offer() {
        var assetIds = providerConnector.getAssetIds();
        Assertions.assertThat(assetIds).contains(DataTransferTestUtil.MIGRATED_M8_ASSET_ID);
        consumerConnector.consumeOffer(
                providerConnector.getParticipantId(),
                providerConnector.getConfig().getApiGroupConfigPart(EdcApiGroup.Protocol).getUri(),
                DataTransferTestUtil.MIGRATED_M8_ASSET_ID,
                JsonLdConnectorUtil.httpDataAddress(getTestBackendUrl("consume")));
        DataTransferTestUtil.validateDataTransferred(
                getTestBackendUrl("getConsumedData"),
                TEST_BACKEND_TEST_DATA_M8);
    }

    @Test
    void createAndConsumeOffer() {
        var assetId = UUID.randomUUID().toString();
        DataTransferTestUtil.createTestOffer(
                providerConnector,
                assetId,
                getTestBackendUrl(String.format("provide/%s", TEST_BACKEND_TEST_DATA)));
        consumerConnector.consumeOffer(
                providerConnector.getParticipantId(),
                providerConnector.getConfig().getApiGroupConfigPart(EdcApiGroup.Protocol).getUri(),
                assetId,
                JsonLdConnectorUtil.httpDataAddress(getTestBackendUrl("consume")));
        DataTransferTestUtil.validateDataTransferred(
                getTestBackendUrl("getConsumedData"),
                TEST_BACKEND_TEST_DATA);
    }

    private String getTestBackendUrl(String method) {
        var defaultApiGroup = providerConnector.getConfig().defaultApiGroupConfig();
        return String.format(
                "%s/test-backend/%s",
                defaultApiGroup.getUri(),
                method);
    }

}