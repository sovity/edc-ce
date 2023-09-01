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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.brokerserver.client.gen.model.DataOfferDetailPageQuery;
import de.sovity.edc.ext.brokerserver.client.gen.model.DataOfferDetailPageResult;
import de.sovity.edc.ext.brokerserver.dao.AssetProperty;
import de.sovity.edc.ext.brokerserver.db.TestDatabase;
import de.sovity.edc.ext.brokerserver.db.TestDatabaseFactory;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import lombok.SneakyThrows;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.policy.model.Policy;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;
import java.util.Map;

import static de.sovity.edc.ext.brokerserver.AssertionUtils.assertEqualJson;
import static de.sovity.edc.ext.brokerserver.TestUtils.createConfiguration;
import static de.sovity.edc.ext.brokerserver.TestUtils.brokerServerClient;
import static groovy.json.JsonOutput.toJson;
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

            createConnector(dsl, today, "http://my-connector2/ids/data");
            createDataOffer(dsl, today, Map.of(
                AssetProperty.ASSET_ID, "urn:artifact:my-asset-2",
                AssetProperty.DATA_CATEGORY, "my-category2",
                AssetProperty.ASSET_NAME, "My Asset 2"
            ), "http://my-connector2/ids/data");

            createConnector(dsl, today, "http://my-connector/ids/data");
            createDataOffer(dsl, today, Map.of(
                AssetProperty.ASSET_ID, "urn:artifact:my-asset-1",
                AssetProperty.DATA_CATEGORY, "my-category",
                AssetProperty.ASSET_NAME, "My Asset 1"
            ), "http://my-connector/ids/data");

            //create view for dataoffer
            createDataOfferView(dsl, today, Map.of(
                AssetProperty.ASSET_ID, "urn:artifact:my-asset-1",
                AssetProperty.DATA_CATEGORY, "my-category",
                AssetProperty.ASSET_NAME, "My Asset 1"
            ), "http://my-connector/ids/data");
            createDataOfferView(dsl, today, Map.of(
                AssetProperty.ASSET_ID, "urn:artifact:my-asset-1",
                AssetProperty.DATA_CATEGORY, "my-category",
                AssetProperty.ASSET_NAME, "My Asset 1"
            ), "http://my-connector/ids/data");

            var actual = brokerServerClient().brokerServerApi().dataOfferDetailPage(new DataOfferDetailPageQuery("http://my-connector/ids/data", "urn:artifact:my-asset-1"));
            assertThat(actual.getAssetId()).isEqualTo("urn:artifact:my-asset-1");
            assertThat(actual.getConnectorEndpoint()).isEqualTo("http://my-connector/ids/data");
            assertThat(actual.getConnectorOfflineSinceOrLastUpdatedAt()).isEqualTo(today);
            assertThat(actual.getConnectorOnlineStatus()).isEqualTo(DataOfferDetailPageResult.ConnectorOnlineStatusEnum.ONLINE);
            assertThat(actual.getCreatedAt()).isEqualTo(today.minusDays(5));
            assertThat(actual.getProperties()).isEqualTo(Map.of(
                AssetProperty.ASSET_ID, "urn:artifact:my-asset-1",
                AssetProperty.DATA_CATEGORY, "my-category",
                AssetProperty.ASSET_NAME, "My Asset 1"
            ));
            assertThat(actual.getUpdatedAt()).isEqualTo(today);
            assertThat(actual.getContractOffers()).hasSize(1);
            var contractOffer = actual.getContractOffers().get(0);
            assertThat(contractOffer.getContractOfferId()).isEqualTo("my-contract-offer-1");
            assertEqualJson(contractOffer.getContractPolicy().getLegacyPolicy(), policyToJson(dummyPolicy()));
            assertThat(contractOffer.getCreatedAt()).isEqualTo(today.minusDays(5));
            assertThat(contractOffer.getUpdatedAt()).isEqualTo(today);
            assertThat(actual.getViewCount()).isEqualTo(2);
        });
    }

    private void createConnector(DSLContext dsl, OffsetDateTime today, String connectorEndpoint) {
        var connector = dsl.newRecord(Tables.CONNECTOR);
        connector.setConnectorId("http://my-connector");
        connector.setEndpoint(connectorEndpoint);
        connector.setOnlineStatus(ConnectorOnlineStatus.ONLINE);
        connector.setCreatedAt(today.minusDays(1));
        connector.setLastRefreshAttemptAt(today);
        connector.setLastSuccessfulRefreshAt(today);
        connector.setDataOffersExceeded(ConnectorDataOffersExceeded.OK);
        connector.setContractOffersExceeded(ConnectorContractOffersExceeded.OK);
        connector.insert();
    }

    private void createDataOffer(DSLContext dsl, OffsetDateTime today, Map<String, String> assetProperties, String connectorEndpoint) {
        var dataOffer = dsl.newRecord(Tables.DATA_OFFER);
        dataOffer.setAssetId(assetProperties.get(AssetProperty.ASSET_ID));
        dataOffer.setAssetName(assetProperties.getOrDefault(AssetProperty.ASSET_NAME, dataOffer.getAssetId()));
        dataOffer.setAssetProperties(JSONB.jsonb(assetProperties(assetProperties)));
        dataOffer.setConnectorEndpoint(connectorEndpoint);
        dataOffer.setCreatedAt(today.minusDays(5));
        dataOffer.setUpdatedAt(today);
        dataOffer.insert();

        var contractOffer = dsl.newRecord(Tables.DATA_OFFER_CONTRACT_OFFER);
        contractOffer.setContractOfferId("my-contract-offer-1");
        contractOffer.setConnectorEndpoint(connectorEndpoint);
        contractOffer.setAssetId(assetProperties.get(AssetProperty.ASSET_ID));
        contractOffer.setCreatedAt(today.minusDays(5));
        contractOffer.setUpdatedAt(today);
        contractOffer.setPolicy(JSONB.jsonb(policyToJson(dummyPolicy())));
        contractOffer.insert();
    }

    private static void createDataOfferView(DSLContext dsl, OffsetDateTime date, Map<String, String> assetProperties, String connectorEndpoint) {
        var view = dsl.newRecord(Tables.DATA_OFFER_VIEW_COUNT);
        view.setAssetId(assetProperties.get(AssetProperty.ASSET_ID));
        view.setConnectorEndpoint(connectorEndpoint);
        view.setDate(date);
        view.insert();
    }

    private Policy dummyPolicy() {
        return Policy.Builder.newInstance()
            .assignee("Example Assignee")
            .build();
    }

    private String policyToJson(Policy policy) {
        return toJson(policy);
    }

    @SneakyThrows
    private String assetProperties(Map<String, String> assetProperties) {
        return new ObjectMapper().writeValueAsString(assetProperties);
    }
}
