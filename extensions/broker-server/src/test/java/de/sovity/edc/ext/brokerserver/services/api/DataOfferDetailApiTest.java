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

import de.sovity.edc.ext.brokerserver.client.gen.model.DataOfferDetailPageQuery;
import de.sovity.edc.ext.brokerserver.client.gen.model.DataOfferDetailPageResult;
import de.sovity.edc.ext.brokerserver.db.TestDatabase;
import de.sovity.edc.ext.brokerserver.db.TestDatabaseFactory;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
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

import static de.sovity.edc.ext.brokerserver.AssertionUtils.assertEqualUsingJson;
import static de.sovity.edc.ext.brokerserver.TestAsset.getAssetJsonLd;
import static de.sovity.edc.ext.brokerserver.TestAsset.setDataOfferAssetMetadata;
import static de.sovity.edc.ext.brokerserver.TestPolicy.createAfterYesterdayConstraint;
import static de.sovity.edc.ext.brokerserver.TestPolicy.createAfterYesterdayPolicyJson;
import static de.sovity.edc.ext.brokerserver.TestUtils.brokerServerClient;
import static de.sovity.edc.ext.brokerserver.TestUtils.createConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class DataOfferDetailApiTest {

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration(TEST_DATABASE, Map.of(
        )));
    }

    @Test
    void testQueryDataOfferDetails() {
        TEST_DATABASE.testTransaction(dsl -> {
            var today = OffsetDateTime.now().withNano(0);

            var assetJsonLd1 = getAssetJsonLd("my-asset-1", Map.of(
                Prop.Mobility.DATA_CATEGORY, "my-category",
                Prop.Dcterms.TITLE, "My Asset 1"
            ));

            var assetJsonLd2 = getAssetJsonLd("my-asset-2", Map.of(
                Prop.Mobility.DATA_CATEGORY, "my-category-2",
                Prop.Dcterms.TITLE, "My Asset 2"
            ));

            createConnector(dsl, today, "https://my-connector/api/dsp");
            createDataOffer(dsl, today, "https://my-connector/api/dsp", assetJsonLd1);

            createDataOfferView(dsl, today, "https://my-connector/api/dsp", "my-asset-1");
            createDataOfferView(dsl, today, "https://my-connector/api/dsp", "my-asset-1");

            createConnector(dsl, today, "https://my-connector2/api/dsp");
            createDataOffer(dsl, today, "https://my-connector2/api/dsp", assetJsonLd2);

            var actual = brokerServerClient().brokerServerApi().dataOfferDetailPage(new DataOfferDetailPageQuery("https://my-connector/api/dsp", "my-asset-1"));
            assertThat(actual.getAssetId()).isEqualTo("my-asset-1");
            assertThat(actual.getConnectorEndpoint()).isEqualTo("https://my-connector/api/dsp");
            assertThat(actual.getConnectorOfflineSinceOrLastUpdatedAt()).isEqualTo(today);
            assertThat(actual.getConnectorOnlineStatus()).isEqualTo(DataOfferDetailPageResult.ConnectorOnlineStatusEnum.ONLINE);
            assertThat(actual.getCreatedAt()).isEqualTo(today.minusDays(5));
            assertThat(actual.getAsset().getAssetId()).isEqualTo("my-asset-1");
            assertThat(actual.getAsset().getDataCategory()).isEqualTo("my-category");
            assertThat(actual.getAsset().getTitle()).isEqualTo("My Asset 1");
            assertThat(actual.getUpdatedAt()).isEqualTo(today);
            assertThat(actual.getContractOffers()).hasSize(1);
            var contractOffer = actual.getContractOffers().get(0);
            assertThat(contractOffer.getContractOfferId()).isEqualTo("my-contract-offer-1");
            assertEqualUsingJson(contractOffer.getContractPolicy().getConstraints().get(0), createAfterYesterdayConstraint());
            assertThat(contractOffer.getCreatedAt()).isEqualTo(today.minusDays(5));
            assertThat(contractOffer.getUpdatedAt()).isEqualTo(today);
            assertThat(actual.getViewCount()).isEqualTo(2);
        });
    }

    private void createConnector(DSLContext dsl, OffsetDateTime today, String connectorEndpoint) {
        var connector = dsl.newRecord(Tables.CONNECTOR);
        connector.setParticipantId("my-connector");
        connector.setEndpoint(connectorEndpoint);
        connector.setOnlineStatus(ConnectorOnlineStatus.ONLINE);
        connector.setCreatedAt(today.minusDays(1));
        connector.setLastRefreshAttemptAt(today);
        connector.setLastSuccessfulRefreshAt(today);
        connector.setDataOffersExceeded(ConnectorDataOffersExceeded.OK);
        connector.setContractOffersExceeded(ConnectorContractOffersExceeded.OK);
        connector.insert();
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
        contractOffer.setPolicy(createAfterYesterdayPolicyJson());
        contractOffer.insert();
    }

    private static void createDataOfferView(DSLContext dsl, OffsetDateTime date, String connectorEndpoint, String assetId) {
        var view = dsl.newRecord(Tables.DATA_OFFER_VIEW_COUNT);
        view.setAssetId(assetId);
        view.setConnectorEndpoint(connectorEndpoint);
        view.setDate(date);
        view.insert();
    }
}
