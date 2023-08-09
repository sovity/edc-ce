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
import de.sovity.edc.extension.e2e.connector.factory.LocalTestConnectorFactory;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseFactory;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.UUID;

import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtils.MIGRATED_M8_ASSET_ID;
import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(
        named = "E2E_TEST_ENABLED",
        matches = "true")
public class LocalConnectorTransferTest {

    private static final String PROVIDER_PARTICIPANT_ID = "provider";
    private static final String CONSUMER_PARTICIPANT_ID = "consumer";
    public static final String TEST_BACKEND_BASE_URL = "http://localhost:33001/api/test-backend";
    public static final String TEST_BACKEND_CHECK_URL = String.format(
            "%s/getConsumedData",
            TEST_BACKEND_BASE_URL);
    private static final String TEST_BACKEND_TEST_DATA = UUID.randomUUID().toString();
    public static final String PROVIDER_TARGET_URL = String.format(
            "%s/provide/%s",
            TEST_BACKEND_BASE_URL,
            TEST_BACKEND_TEST_DATA);
    public static final String CONSUMER_BACKEND_URL = String.format(
            "%s/consume",
            TEST_BACKEND_BASE_URL);

    @RegisterExtension
    static EdcExtension providerEdcContext = new EdcExtension();
    @RegisterExtension
    static EdcExtension consumerEdcContext = new EdcExtension();

    @RegisterExtension
    static final TestDatabase PROVIDER_DATABASE = TestDatabaseFactory.getTestDatabase();
    @RegisterExtension
    static final TestDatabase CONSUMER_DATABASE = TestDatabaseFactory.getTestDatabase();

    private Connector providerConnector;
    private Connector consumerConnector;

    @BeforeEach
    void setUp() {
        providerConnector = new LocalTestConnectorFactory(
                PROVIDER_PARTICIPANT_ID,
                providerEdcContext,
                PROVIDER_DATABASE)
                .createConnector();
        consumerConnector = new LocalTestConnectorFactory(
                CONSUMER_PARTICIPANT_ID,
                consumerEdcContext,
                CONSUMER_DATABASE)
                .createConnector();
    }

    @Test
    void consumeM8Offer() {
        var assetIds = providerConnector.getAssetIds();
        assertThat(assetIds).contains(MIGRATED_M8_ASSET_ID);
        consumerConnector.consumeOffer(
                PROVIDER_PARTICIPANT_ID,
                providerConnector.getProtocolApiUri(),
                MIGRATED_M8_ASSET_ID,
                JsonLdConnectorUtil.httpDataAddress(CONSUMER_BACKEND_URL));
        DataTransferTestUtils.validateDataTransferred(TEST_BACKEND_CHECK_URL,
                TEST_BACKEND_TEST_DATA);
    }

    @Test
    void createAndConsumeOffer() {
        var assetId = UUID.randomUUID().toString();
        DataTransferTestUtils.createTestOffer(
                providerConnector,
                assetId,
                PROVIDER_TARGET_URL);
        consumerConnector.consumeOffer(
                PROVIDER_PARTICIPANT_ID,
                providerConnector.getProtocolApiUri(),
                assetId,
                JsonLdConnectorUtil.httpDataAddress(CONSUMER_BACKEND_URL));
        DataTransferTestUtils.validateDataTransferred(
                TEST_BACKEND_CHECK_URL,
                TEST_BACKEND_TEST_DATA);
    }

}