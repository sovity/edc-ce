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
 *       sovity GmbH - initial implementation
 *
 */

package de.sovity.edc.ext.brokerserver.services.api;

import de.sovity.edc.ext.brokerserver.TestUtils;
import de.sovity.edc.ext.brokerserver.client.BrokerServerClient;
import de.sovity.edc.ext.brokerserver.client.gen.model.ConnectorListEntry;
import de.sovity.edc.ext.brokerserver.client.gen.model.ConnectorPageQuery;
import de.sovity.edc.ext.brokerserver.db.TestDatabase;
import de.sovity.edc.ext.brokerserver.db.TestDatabaseFactory;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.BrokerEventStatus;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.BrokerEventType;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.MeasurementErrorStatus;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.MeasurementType;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.TableField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static de.sovity.edc.ext.brokerserver.TestUtils.ADMIN_API_KEY;
import static de.sovity.edc.ext.brokerserver.TestUtils.brokerServerClient;
import static de.sovity.edc.ext.brokerserver.TestUtils.createConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class DeleteConnectorsApiTest {
    BrokerServerClient client;
    String firstConnector = "http://a";
    String otherConnector = "http://b";

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration(TEST_DATABASE, Map.of()));
        client = brokerServerClient();
    }

    @Test
    void testRemoveConnectors() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            setupConnectorData(dsl, firstConnector);
            setupConnectorData(dsl, otherConnector);

            var connectorsBefore = List.of(firstConnector, otherConnector);
            assertContainsEndpoints(dsl, Tables.BROKER_EXECUTION_TIME_MEASUREMENT.CONNECTOR_ENDPOINT, connectorsBefore);
            assertContainsEndpoints(dsl, Tables.DATA_OFFER_CONTRACT_OFFER.CONNECTOR_ENDPOINT, connectorsBefore);
            assertContainsEndpoints(dsl, Tables.DATA_OFFER.CONNECTOR_ENDPOINT, connectorsBefore);
            assertContainsEndpoints(dsl, Tables.DATA_OFFER_VIEW_COUNT.CONNECTOR_ENDPOINT, connectorsBefore);
            assertContainsEndpoints(dsl, Tables.CONNECTOR.ENDPOINT, connectorsBefore);

            // act
            client.brokerServerApi().deleteConnectors(ADMIN_API_KEY, List.of(firstConnector));

            // assert
            assertThat(client.brokerServerApi().connectorPage(new ConnectorPageQuery()).getConnectors())
                    .extracting(ConnectorListEntry::getEndpoint)
                    .containsExactly(otherConnector);

            var connectorsAfter = List.of(otherConnector);
            assertContainsEndpoints(dsl, Tables.BROKER_EXECUTION_TIME_MEASUREMENT.CONNECTOR_ENDPOINT, connectorsAfter);
            assertContainsEndpoints(dsl, Tables.DATA_OFFER_CONTRACT_OFFER.CONNECTOR_ENDPOINT, connectorsAfter);
            assertContainsEndpoints(dsl, Tables.DATA_OFFER.CONNECTOR_ENDPOINT, connectorsAfter);
            assertContainsEndpoints(dsl, Tables.DATA_OFFER_VIEW_COUNT.CONNECTOR_ENDPOINT, connectorsAfter);
            assertContainsEndpoints(dsl, Tables.CONNECTOR.ENDPOINT, connectorsAfter);
        });
    }

    private <T extends Record> void assertContainsEndpoints(
            DSLContext dsl,
            TableField<T, String> endpointField,
            Collection<String> expected
    ) {
        var actual = dsl.select(endpointField).from(endpointField.getTable()).fetchSet(endpointField);
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    public void setupConnectorData(DSLContext dsl, String endpoint) {
        client.brokerServerApi().addConnectors(ADMIN_API_KEY, List.of(endpoint));

        var assetId = "my-asset";

        var dataOffer = dsl.newRecord(Tables.DATA_OFFER);
        dataOffer.setAssetId(assetId);
        dataOffer.setAssetName("My Asset");
        dataOffer.setAssetProperties(JSONB.valueOf("{}"));
        dataOffer.setConnectorEndpoint(endpoint);
        dataOffer.setCreatedAt(OffsetDateTime.now());
        dataOffer.setUpdatedAt(OffsetDateTime.now());
        dataOffer.insert();

        var contractOffer = dsl.newRecord(Tables.DATA_OFFER_CONTRACT_OFFER);
        contractOffer.setAssetId(assetId);
        contractOffer.setConnectorEndpoint(endpoint);
        contractOffer.setContractOfferId("my-asset-co");
        contractOffer.setCreatedAt(OffsetDateTime.now());
        contractOffer.setPolicy(JSONB.valueOf("{}"));
        contractOffer.setUpdatedAt(OffsetDateTime.now());
        contractOffer.insert();

        var logEntry = dsl.newRecord(Tables.BROKER_EVENT_LOG);
        logEntry.setEvent(BrokerEventType.CONNECTOR_UPDATED);
        logEntry.setUserMessage("Hello World!");
        logEntry.setAssetId(assetId);
        logEntry.setCreatedAt(OffsetDateTime.now());
        logEntry.setConnectorEndpoint(endpoint);
        logEntry.setEventStatus(BrokerEventStatus.OK);
        logEntry.insert();

        var measurement = dsl.newRecord(Tables.BROKER_EXECUTION_TIME_MEASUREMENT);
        measurement.setConnectorEndpoint(endpoint);
        measurement.setCreatedAt(OffsetDateTime.now());
        measurement.setDurationInMs(500L);
        measurement.setErrorStatus(MeasurementErrorStatus.OK);
        measurement.setType(MeasurementType.CONNECTOR_REFRESH);
        measurement.insert();

        var view = dsl.newRecord(Tables.DATA_OFFER_VIEW_COUNT);
        view.setConnectorEndpoint(endpoint);
        view.setAssetId(assetId);
        view.setDate(OffsetDateTime.now());
        view.insert();
    }


    @Test
    void testDeleteWrongApiKey() {
        TEST_DATABASE.testTransaction(dsl -> TestUtils.assertIs401(() ->
                client.brokerServerApi().deleteConnectors("wrong-api-key", List.of())));
    }
}
