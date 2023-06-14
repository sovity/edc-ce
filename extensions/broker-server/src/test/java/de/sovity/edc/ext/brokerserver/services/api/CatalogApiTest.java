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
import de.sovity.edc.client.gen.model.CatalogPageQuery;
import de.sovity.edc.client.gen.model.CatalogPageResult;
import de.sovity.edc.client.gen.model.CnfFilterAttribute;
import de.sovity.edc.client.gen.model.CnfFilterItem;
import de.sovity.edc.client.gen.model.CnfFilterValue;
import de.sovity.edc.client.gen.model.CnfFilterValueAttribute;
import de.sovity.edc.client.gen.model.DataOfferListEntry;
import de.sovity.edc.ext.brokerserver.BrokerServerExtension;
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
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static de.sovity.edc.client.gen.model.DataOfferListEntry.ConnectorOnlineStatusEnum.ONLINE;
import static de.sovity.edc.ext.brokerserver.TestUtils.createConfiguration;
import static de.sovity.edc.ext.brokerserver.TestUtils.edcClient;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class CatalogApiTest {

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration(TEST_DATABASE, Map.of(
                BrokerServerExtension.CATALOG_PAGE_PAGE_SIZE, "10"
        )));
    }

    @Test
    void testDataOfferDetails() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var today = OffsetDateTime.now().withNano(0);

            createConnector(dsl, today);
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset",
                    AssetProperty.ASSET_NAME, "my-asset"
            ));


            var result = edcClient().brokerServerApi().catalogPage(new CatalogPageQuery());
            assertThat(result.getDataOffers()).hasSize(1);

            var dataOfferResult = result.getDataOffers().get(0);
            assertThat(dataOfferResult.getConnectorEndpoint()).isEqualTo("http://my-connector/ids/data");
            assertThat(dataOfferResult.getConnectorOfflineSinceOrLastUpdatedAt()).isEqualTo(today);
            assertThat(dataOfferResult.getConnectorOnlineStatus()).isEqualTo(ONLINE);
            assertThat(dataOfferResult.getAssetId()).isEqualTo("urn:artifact:my-asset");
            assertThat(dataOfferResult.getProperties()).isEqualTo(Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset",
                    AssetProperty.ASSET_NAME, "my-asset"
            ));
            assertThat(dataOfferResult.getCreatedAt()).isEqualTo(today.minusDays(5));
            assertThat(toJson(dataOfferResult.getContractOffers().get(0).getContractPolicy().getLegacyPolicy())).isEqualTo(toJson(dummyPolicy()));
        });
    }

    @Test
    void testAvailableFilters_noFilter() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var today = OffsetDateTime.now().withNano(0);

            createConnector(dsl, today);
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset-1",
                    AssetProperty.DATA_CATEGORY, "my-category-1",
                    AssetProperty.TRANSPORT_MODE, "MY-TRANSPORT-MODE-1",
                    AssetProperty.DATA_SUBCATEGORY, "MY-SUBCATEGORY-2"
            ));
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset-2",
                    AssetProperty.DATA_CATEGORY, "my-category-1",
                    AssetProperty.TRANSPORT_MODE, "my-transport-mode-2",
                    AssetProperty.DATA_SUBCATEGORY, "MY-SUBCATEGORY-2"
            ));
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset-3",
                    AssetProperty.DATA_CATEGORY, "my-category-1",
                    AssetProperty.TRANSPORT_MODE, "MY-TRANSPORT-MODE-1",
                    AssetProperty.DATA_SUBCATEGORY, "my-subcategory-1"
            ));
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset-4",
                    AssetProperty.DATA_CATEGORY, "my-category-1",
                    AssetProperty.TRANSPORT_MODE, ""
            ));


            var result = edcClient().brokerServerApi().catalogPage(new CatalogPageQuery());

            assertThat(result.getAvailableFilters().getFields())
                    .extracting(CnfFilterAttribute::getId)
                    .containsExactly(
                            AssetProperty.DATA_CATEGORY,
                            AssetProperty.DATA_SUBCATEGORY,
                            AssetProperty.DATA_MODEL,
                            AssetProperty.TRANSPORT_MODE,
                            AssetProperty.GEO_REFERENCE_METHOD
                    );

            assertThat(result.getAvailableFilters().getFields())
                    .extracting(CnfFilterAttribute::getTitle)
                    .containsExactly(
                            "Data Category",
                            "Data Subcategory",
                            "Data Model",
                            "Transport Mode",
                            "Geo Reference Method"
                    );

            var dataCategory = getAvailableFilter(result, AssetProperty.DATA_CATEGORY);
            assertThat(dataCategory.getTitle()).isEqualTo("Data Category");
            assertThat(dataCategory.getValues()).extracting(CnfFilterItem::getId).containsExactly("my-category-1");
            assertThat(dataCategory.getValues()).extracting(CnfFilterItem::getTitle).containsExactly("my-category-1");

            var transportMode = getAvailableFilter(result, AssetProperty.TRANSPORT_MODE);
            assertThat(transportMode.getTitle()).isEqualTo("Transport Mode");
            assertThat(transportMode.getValues()).extracting(CnfFilterItem::getId).containsExactly("MY-TRANSPORT-MODE-1", "my-transport-mode-2", "");
            assertThat(transportMode.getValues()).extracting(CnfFilterItem::getTitle).containsExactly("MY-TRANSPORT-MODE-1", "my-transport-mode-2", "");

            var dataSubcategory = getAvailableFilter(result, AssetProperty.DATA_SUBCATEGORY);
            assertThat(dataSubcategory.getTitle()).isEqualTo("Data Subcategory");
            assertThat(dataSubcategory.getValues()).extracting(CnfFilterItem::getId).containsExactly("my-subcategory-1", "MY-SUBCATEGORY-2", "");
            assertThat(dataSubcategory.getValues()).extracting(CnfFilterItem::getTitle).containsExactly("my-subcategory-1", "MY-SUBCATEGORY-2", "");
        });
    }

    private CnfFilterAttribute getAvailableFilter(CatalogPageResult result, String filterId) {
        return result.getAvailableFilters().getFields().stream()
                .filter(it -> it.getId().equals(filterId)).findFirst()
                .orElseThrow(() -> new IllegalStateException("Filter not found"));
    }

    @Test
    void testAvailableFilters_withFilter() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var today = OffsetDateTime.now().withNano(0);

            createConnector(dsl, today);
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset-1",
                    AssetProperty.DATA_CATEGORY, "my-category"
            ));
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset-2"
            ));


            var query = new CatalogPageQuery();
            query.setFilter(new CnfFilterValue(List.of(
                    new CnfFilterValueAttribute(AssetProperty.DATA_CATEGORY, List.of(""))
            )));

            var result = edcClient().brokerServerApi().catalogPage(query);
            var actual = getAvailableFilter(result, AssetProperty.DATA_CATEGORY);

            assertThat(actual.getId()).isEqualTo(AssetProperty.DATA_CATEGORY);
            assertThat(actual.getTitle()).isEqualTo("Data Category");
            assertThat(actual.getValues()).extracting(CnfFilterItem::getId).containsExactly("");
            assertThat(actual.getValues()).extracting(CnfFilterItem::getTitle).containsExactly("");
        });
    }

    @Test
    void testPagination_firstPage() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var today = OffsetDateTime.now().withNano(0);

            createConnector(dsl, today);
            IntStream.range(0, 15).forEach(i -> createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset-%d".formatted(i)
            )));
            IntStream.range(0, 15).forEach(i -> createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:some-other-asset-%d".formatted(i)
            )));


            var query = new CatalogPageQuery();
            query.setSearchQuery("my-asset");
            query.setSorting(CatalogPageQuery.SortingEnum.TITLE);

            var result = edcClient().brokerServerApi().catalogPage(query);
            assertThat(result.getDataOffers()).extracting(DataOfferListEntry::getAssetId)
                    .isEqualTo(IntStream.range(0, 10).mapToObj("urn:artifact:my-asset-%d"::formatted).toList());

            var actual = result.getPaginationMetadata();
            assertThat(actual.getPageOneBased()).isEqualTo(1);
            assertThat(actual.getPageSize()).isEqualTo(10);
            assertThat(actual.getNumVisible()).isEqualTo(10);
            assertThat(actual.getNumTotal()).isEqualTo(15);
        });
    }

    @Test
    void testPagination_secondPage() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var today = OffsetDateTime.now().withNano(0);

            createConnector(dsl, today);
            IntStream.range(0, 15).forEach(i -> createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset-%d".formatted(i)
            )));
            IntStream.range(0, 15).forEach(i -> createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:some-other-asset-%d".formatted(i)
            )));


            var query = new CatalogPageQuery();
            query.setSearchQuery("my-asset");
            query.setPageOneBased(2);
            query.setSorting(CatalogPageQuery.SortingEnum.TITLE);

            var result = edcClient().brokerServerApi().catalogPage(query);

            assertThat(result.getDataOffers()).extracting(DataOfferListEntry::getAssetId)
                    .isEqualTo(IntStream.range(10, 15).mapToObj("urn:artifact:my-asset-%d"::formatted).toList());

            var actual = result.getPaginationMetadata();
            assertThat(actual.getPageOneBased()).isEqualTo(2);
            assertThat(actual.getPageSize()).isEqualTo(10);
            assertThat(actual.getNumVisible()).isEqualTo(5);
            assertThat(actual.getNumTotal()).isEqualTo(15);
        });
    }

    private void createDataOffer(DSLContext dsl, OffsetDateTime today, Map<String, String> assetProperties) {
        var dataOffer = dsl.newRecord(Tables.DATA_OFFER);
        dataOffer.setAssetId(assetProperties.get(AssetProperty.ASSET_ID));
        dataOffer.setAssetName(assetProperties.getOrDefault(AssetProperty.ASSET_NAME, dataOffer.getAssetId()));
        dataOffer.setAssetProperties(JSONB.jsonb(assetProperties(assetProperties)));
        dataOffer.setConnectorEndpoint("http://my-connector/ids/data");
        dataOffer.setCreatedAt(today.minusDays(5));
        dataOffer.setUpdatedAt(today);
        dataOffer.insert();

        var contractOffer = dsl.newRecord(Tables.DATA_OFFER_CONTRACT_OFFER);
        contractOffer.setContractOfferId("my-contract-offer-1");
        contractOffer.setConnectorEndpoint("http://my-connector/ids/data");
        contractOffer.setAssetId(assetProperties.get(AssetProperty.ASSET_ID));
        contractOffer.setCreatedAt(today.minusDays(5));
        contractOffer.setUpdatedAt(today);
        contractOffer.setPolicy(JSONB.jsonb(policyToJson(dummyPolicy())));
        contractOffer.insert();
    }

    private void createConnector(DSLContext dsl, OffsetDateTime today) {
        var connector = dsl.newRecord(Tables.CONNECTOR);
        connector.setConnectorId("http://my-connector");
        connector.setEndpoint("http://my-connector/ids/data");
        connector.setOnlineStatus(ConnectorOnlineStatus.ONLINE);
        connector.setCreatedAt(today.minusDays(1));
        connector.setLastRefreshAttemptAt(today);
        connector.setLastSuccessfulRefreshAt(today);
        connector.setDataOffersExceeded(ConnectorDataOffersExceeded.OK);
        connector.setContractOffersExceeded(ConnectorContractOffersExceeded.OK);
        connector.insert();
    }

    private Policy dummyPolicy() {
        return Policy.Builder.newInstance()
                .assignee("Example Assignee")
                .build();
    }

    private String policyToJson(Policy policy) {
        return toJson(policy);
    }

    private String toJson(Object o) {
        return new ObjectMapper().valueToTree(o).toString();
    }

    @SneakyThrows
    private String assetProperties(Map<String, String> assetProperties) {
        return new ObjectMapper().writeValueAsString(assetProperties);
    }
}
