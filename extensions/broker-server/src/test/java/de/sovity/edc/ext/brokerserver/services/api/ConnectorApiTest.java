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

import de.sovity.edc.ext.brokerserver.TestPolicy;
import de.sovity.edc.ext.brokerserver.client.gen.model.ConnectorDetailPageQuery;
import de.sovity.edc.ext.brokerserver.client.gen.model.ConnectorPageQuery;
import de.sovity.edc.ext.brokerserver.db.TestDatabase;
import de.sovity.edc.ext.brokerserver.db.TestDatabaseFactory;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.MeasurementErrorStatus;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.MeasurementType;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.ConnectorRecord;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.JsonObject;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;
import java.util.Map;

import static de.sovity.edc.ext.brokerserver.TestAsset.getAssetJsonLd;
import static de.sovity.edc.ext.brokerserver.TestAsset.setDataOfferAssetMetadata;
import static de.sovity.edc.ext.brokerserver.TestUtils.brokerServerClient;
import static de.sovity.edc.ext.brokerserver.TestUtils.createConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class ConnectorApiTest {

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration(TEST_DATABASE, Map.of()));
    }

    @Test
    void testQueryConnectors() {
        TEST_DATABASE.testTransaction(dsl -> {
            var today = OffsetDateTime.now().withNano(0);

            var assetJsonLd = getAssetJsonLd("my-asset-1", Map.of(
                Prop.Mds.DATA_CATEGORY, "my-category",
                Prop.Dcterms.TITLE, "My Asset 1"
            ));

            createConnector(dsl, today, "https://my-connector/api/dsp");
            createDataOffer(dsl, today, "https://my-connector/api/dsp", assetJsonLd);

            var result = brokerServerClient().brokerServerApi().connectorPage(new ConnectorPageQuery());
            assertThat(result.getConnectors()).hasSize(1);

            var connector = result.getConnectors().get(0);
            assertThat(connector.getParticipantId()).isEqualTo("my-participant-id");
            assertThat(connector.getEndpoint()).isEqualTo("https://my-connector/api/dsp");
            assertThat(connector.getCreatedAt()).isEqualTo(today.minusDays(1));
            assertThat(connector.getLastRefreshAttemptAt()).isEqualTo(today);
            assertThat(connector.getLastSuccessfulRefreshAt()).isEqualTo(today);
            assertThat(connector.getNumDataOffers()).isEqualTo(1);
        });
    }

    @Test
    void testQueryConnectorDetails() {
        TEST_DATABASE.testTransaction(dsl -> {
            var today = OffsetDateTime.now().withNano(0);

            var assetJsonLd = getAssetJsonLd("my-asset-1", Map.of(
                Prop.Mds.DATA_CATEGORY, "my-category",
                Prop.Dcterms.TITLE, "My Asset 1"
            ));

            createConnector(dsl, today, "https://my-connector/api/dsp");
            createConnector(dsl, today, "https://my-connector2/api/dsp");
            createDataOffer(dsl, today, "https://my-connector/api/dsp", assetJsonLd);

            var connector = brokerServerClient().brokerServerApi().connectorDetailPage(new ConnectorDetailPageQuery("https://my-connector/api/dsp"));
            assertThat(connector.getParticipantId()).isEqualTo("my-participant-id");
            assertThat(connector.getEndpoint()).isEqualTo("https://my-connector/api/dsp");
            assertThat(connector.getCreatedAt()).isEqualTo(today.minusDays(1));
            assertThat(connector.getLastRefreshAttemptAt()).isEqualTo(today);
            assertThat(connector.getLastSuccessfulRefreshAt()).isEqualTo(today);
            assertThat(connector.getConnectorCrawlingTimeAvg()).isEqualTo(150L);
        });
    }

    private void createConnector(DSLContext dsl, OffsetDateTime today, String connectorEndpoint) {
        var connector = dsl.newRecord(Tables.CONNECTOR);
        connector.setParticipantId("my-participant-id");
        connector.setEndpoint(connectorEndpoint);
        connector.setOnlineStatus(ConnectorOnlineStatus.ONLINE);
        connector.setCreatedAt(today.minusDays(1));
        connector.setLastRefreshAttemptAt(today);
        connector.setLastSuccessfulRefreshAt(today);
        connector.setDataOffersExceeded(ConnectorDataOffersExceeded.OK);
        connector.setContractOffersExceeded(ConnectorContractOffersExceeded.OK);
        connector.insert();

        addCrawlingTime(dsl, today, connector, 100L);
        addCrawlingTime(dsl, today.plusHours(5), connector, 200L);
    }

    private static void addCrawlingTime(DSLContext dsl, OffsetDateTime today, ConnectorRecord connector, Long duration) {
        var crawlingTime = dsl.newRecord(Tables.BROKER_EXECUTION_TIME_MEASUREMENT);
        crawlingTime.setConnectorEndpoint(connector.getEndpoint());
        crawlingTime.setDurationInMs(duration);
        crawlingTime.setCreatedAt(today);
        crawlingTime.setType(MeasurementType.CONNECTOR_REFRESH);
        crawlingTime.setErrorStatus(MeasurementErrorStatus.OK);
        crawlingTime.insert();
    }

    private void createDataOffer(DSLContext dsl, OffsetDateTime today, String connectorEndpoint, JsonObject assetJsonLd) {
        var dataOffer = dsl.newRecord(Tables.DATA_OFFER);
        setDataOfferAssetMetadata(dataOffer, assetJsonLd, "my-participant-id");
        dataOffer.setConnectorEndpoint(connectorEndpoint);
        dataOffer.setCreatedAt(today.minusDays(5));
        dataOffer.setUpdatedAt(today);
        dataOffer.insert();

        var contractOffer = dsl.newRecord(Tables.CONTRACT_OFFER);
        contractOffer.setContractOfferId("my-contract-offer-1");
        contractOffer.setConnectorEndpoint(connectorEndpoint);
        contractOffer.setAssetId(dataOffer.getAssetId());
        contractOffer.setCreatedAt(today.minusDays(5));
        contractOffer.setUpdatedAt(today);
        contractOffer.setPolicy(TestPolicy.createAfterYesterdayPolicyJson());
        contractOffer.insert();
    }
}
