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
import de.sovity.edc.ext.brokerserver.BrokerServerExtension;
import de.sovity.edc.ext.brokerserver.client.gen.model.CatalogDataOffer;
import de.sovity.edc.ext.brokerserver.client.gen.model.CatalogPageQuery;
import de.sovity.edc.ext.brokerserver.client.gen.model.CatalogPageResult;
import de.sovity.edc.ext.brokerserver.client.gen.model.CnfFilterAttribute;
import de.sovity.edc.ext.brokerserver.client.gen.model.CnfFilterItem;
import de.sovity.edc.ext.brokerserver.client.gen.model.CnfFilterValue;
import de.sovity.edc.ext.brokerserver.client.gen.model.CnfFilterValueAttribute;
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
import org.eclipse.edc.policy.model.PolicyType;
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

import static de.sovity.edc.ext.brokerserver.TestUtils.brokerServerClient;
import static de.sovity.edc.ext.brokerserver.TestUtils.createConfiguration;
import static java.util.stream.IntStream.*;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class CatalogApiTest {

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration(TEST_DATABASE, Map.of(
                BrokerServerExtension.CATALOG_PAGE_PAGE_SIZE, "10",
                BrokerServerExtension.DEFAULT_CONNECTOR_DATASPACE, "MDS",
                BrokerServerExtension.KNOWN_DATASPACE_CONNECTORS, "Example1=http://my-connector2/ids/data,Example2=http://my-connector3/ids/data"
        )));
    }

    @Test
    void testDataSpace_two_dataspaces_filter_for_one() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var today = OffsetDateTime.now().withNano(0);

            createConnector(dsl, today, "http://my-connector/ids/data"); // Dataspace: MDS
            createConnector(dsl, today, "http://my-connector2/ids/data"); // Dataspace: Example1
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset",
                    AssetProperty.ASSET_NAME, "my-asset"
            ), "http://my-connector/ids/data"); // Dataspace: MDS
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset",
                    AssetProperty.ASSET_NAME, "my-asset"
            ), "http://my-connector2/ids/data"); // Dataspace: Example1

            var query = new CatalogPageQuery();
            query.setFilter(new CnfFilterValue(List.of(
                    new CnfFilterValueAttribute("dataSpace", List.of("Example1"))
            )));

            var result = brokerServerClient().brokerServerApi().catalogPage(query);
            assertThat(result.getDataOffers()).hasSize(1);

            var dataOfferResult = result.getDataOffers().get(0);
            assertThat(dataOfferResult.getConnectorEndpoint()).isEqualTo("http://my-connector2/ids/data");
        });
    }

    @Test
    void test_available_filter_values_to_filter_by() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var today = OffsetDateTime.now().withNano(0);

            createConnector(dsl, today, "http://my-connector/ids/data"); // Dataspace: MDS
            createConnector(dsl, today, "http://my-connector2/ids/data"); // Dataspace: Example1
            createConnector(dsl, today, "http://my-connector3/ids/data"); // Dataspace: Example2
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset",
                    AssetProperty.ASSET_NAME, "my-asset",
                    AssetProperty.LANGUAGE, "de"
            ), "http://my-connector/ids/data"); // Dataspace: MDS
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset",
                    AssetProperty.ASSET_NAME, "my-asset",
                    AssetProperty.LANGUAGE, "en"
            ), "http://my-connector2/ids/data"); // Dataspace: Example1
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset2",
                    AssetProperty.ASSET_NAME, "my-asset",
                    AssetProperty.LANGUAGE, "fr"
            ), "http://my-connector2/ids/data"); // Dataspace: Example1
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset3",
                    AssetProperty.ASSET_NAME, "my-asset",
                    AssetProperty.LANGUAGE, "fr"
            ), "http://my-connector3/ids/data"); // Dataspace: Example2

            // get all available filter values
            var result = brokerServerClient().brokerServerApi().catalogPage(new CatalogPageQuery());

            // assert that the filter values are correct
            var dataSpace = getAvailableFilter(result, "dataSpace");
            assertThat(dataSpace.getValues()).containsExactly(
                    new CnfFilterItem("Example1", "Example1"),
                    new CnfFilterItem("Example2", "Example2"),
                    new CnfFilterItem("MDS", "MDS")
            );
        });
    }

    @Test
    void testDataOfferDetails() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var today = OffsetDateTime.now().withNano(0);

            createConnector(dsl, today, "http://my-connector/ids/data");
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset",
                    AssetProperty.ASSET_NAME, "my-asset"
            ), "http://my-connector/ids/data");


            var result = brokerServerClient().brokerServerApi().catalogPage(new CatalogPageQuery());
            assertThat(result.getDataOffers()).hasSize(1);

            var dataOfferResult = result.getDataOffers().get(0);
            assertThat(dataOfferResult.getConnectorEndpoint()).isEqualTo("http://my-connector/ids/data");
            assertThat(dataOfferResult.getConnectorOfflineSinceOrLastUpdatedAt()).isEqualTo(today);
            assertThat(dataOfferResult.getConnectorOnlineStatus()).isEqualTo(CatalogDataOffer.ConnectorOnlineStatusEnum.ONLINE);
            assertThat(dataOfferResult.getAssetId()).isEqualTo("urn:artifact:my-asset");
            assertThat(dataOfferResult.getProperties()).isEqualTo(Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset",
                    AssetProperty.ASSET_NAME, "my-asset"
            ));
            assertThat(dataOfferResult.getCreatedAt()).isEqualTo(today.minusDays(5));
        });
    }

    /**
     * Tests against an issue where empty available filter values resulted in NULLs
     */
    @Test
    void testEmptyConnector() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var today = OffsetDateTime.now().withNano(0);
            createConnector(dsl, today, "http://my-connector/ids/data");

            // act
            var result = brokerServerClient().brokerServerApi().catalogPage(new CatalogPageQuery());

            // assert
            assertThat(result.getDataOffers()).isEmpty();
            assertThat(result.getAvailableFilters().getFields()).isNotEmpty();
            assertThat(result.getAvailableSortings()).isNotEmpty();

            // the most important thing is that the above code ran through as it crashed before
        });
    }

    @Test
    void testAvailableFilters_noFilter() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var today = OffsetDateTime.now().withNano(0);

            createConnector(dsl, today, "http://my-connector/ids/data");
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset-1",
                    AssetProperty.DATA_CATEGORY, "my-category-1",
                    AssetProperty.TRANSPORT_MODE, "MY-TRANSPORT-MODE-1",
                    AssetProperty.DATA_SUBCATEGORY, "MY-SUBCATEGORY-2"
            ), "http://my-connector/ids/data");
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset-2",
                    AssetProperty.DATA_CATEGORY, "my-category-1",
                    AssetProperty.TRANSPORT_MODE, "my-transport-mode-2",
                    AssetProperty.DATA_SUBCATEGORY, "MY-SUBCATEGORY-2"
            ), "http://my-connector/ids/data");
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset-3",
                    AssetProperty.DATA_CATEGORY, "my-category-1",
                    AssetProperty.TRANSPORT_MODE, "MY-TRANSPORT-MODE-1",
                    AssetProperty.DATA_SUBCATEGORY, "my-subcategory-1"
            ), "http://my-connector/ids/data");
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset-4",
                    AssetProperty.DATA_CATEGORY, "my-category-1",
                    AssetProperty.TRANSPORT_MODE, ""
            ), "http://my-connector/ids/data");


            var result = brokerServerClient().brokerServerApi().catalogPage(new CatalogPageQuery());

            assertThat(result.getAvailableFilters().getFields())
                    .extracting(CnfFilterAttribute::getId)
                    .containsExactly(
                            "dataSpace",
                            AssetProperty.DATA_CATEGORY,
                            AssetProperty.DATA_SUBCATEGORY,
                            AssetProperty.DATA_MODEL,
                            AssetProperty.TRANSPORT_MODE,
                            AssetProperty.GEO_REFERENCE_METHOD
                    );

            assertThat(result.getAvailableFilters().getFields())
                    .extracting(CnfFilterAttribute::getTitle)
                    .containsExactly(
                            "Data Space",
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


    /**
     * Regression Test against bug where asset names with capital letters were not hit by search.
     * <br>
     * It was caused by search terms getting lower cased while the LIKE operation being case-sensitive.
     */
    @Test
    void testSearchCaseInsensitive() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var today = OffsetDateTime.now().withNano(0);

            createConnector(dsl, today, "http://my-connector/ids/data");
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "123",
                    AssetProperty.ASSET_NAME, "Hello"
            ), "http://my-connector/ids/data");


            // act
            var query = new CatalogPageQuery();
            query.setSearchQuery("Hello");
            var result = brokerServerClient().brokerServerApi().catalogPage(query);

            // assert
            assertThat(result.getDataOffers()).extracting(CatalogDataOffer::getAssetId).containsExactly("123");
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

            createConnector(dsl, today, "http://my-connector/ids/data");
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset-1",
                    AssetProperty.DATA_CATEGORY, "my-category",
                    AssetProperty.DATA_SUBCATEGORY, "my-subcategory"
            ), "http://my-connector/ids/data");
            createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset-2",
                    AssetProperty.DATA_SUBCATEGORY, "my-other-subcategory"
            ), "http://my-connector/ids/data");


            var query = new CatalogPageQuery();
            query.setFilter(new CnfFilterValue(List.of(
                    new CnfFilterValueAttribute(AssetProperty.DATA_CATEGORY, List.of(""))
            )));

            var result = brokerServerClient().brokerServerApi().catalogPage(query);

            var dataCategory = getAvailableFilter(result, AssetProperty.DATA_CATEGORY);
            assertThat(dataCategory.getId()).isEqualTo(AssetProperty.DATA_CATEGORY);
            assertThat(dataCategory.getTitle()).isEqualTo("Data Category");
            assertThat(dataCategory.getValues()).extracting(CnfFilterItem::getId).containsExactly("my-category", "");
            assertThat(dataCategory.getValues()).extracting(CnfFilterItem::getTitle).containsExactly("my-category", "");

            var dataSubcategory = getAvailableFilter(result, AssetProperty.DATA_SUBCATEGORY);
            assertThat(dataSubcategory.getId()).isEqualTo(AssetProperty.DATA_SUBCATEGORY);
            assertThat(dataSubcategory.getTitle()).isEqualTo("Data Subcategory");
            assertThat(dataSubcategory.getValues()).extracting(CnfFilterItem::getId).containsExactly("my-other-subcategory");
            assertThat(dataSubcategory.getValues()).extracting(CnfFilterItem::getTitle).containsExactly("my-other-subcategory");
        });
    }

    @Test
    void testPagination_firstPage() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var today = OffsetDateTime.now().withNano(0);

            createConnector(dsl, today, "http://my-connector/ids/data");
            range(0, 15).forEach(i -> createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset-%d".formatted(i)
            ), "http://my-connector/ids/data"));
            range(0, 15).forEach(i -> createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:some-other-asset-%d".formatted(i)
            ), "http://my-connector/ids/data"));


            var query = new CatalogPageQuery();
            query.setSearchQuery("my-asset");
            query.setSorting(CatalogPageQuery.SortingEnum.TITLE);

            var result = brokerServerClient().brokerServerApi().catalogPage(query);
            assertThat(result.getDataOffers()).extracting(CatalogDataOffer::getAssetId)
                    .isEqualTo(range(0, 10).mapToObj("urn:artifact:my-asset-%d"::formatted).toList());

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

            createConnector(dsl, today, "http://my-connector/ids/data");
            range(0, 15).forEach(i -> createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:my-asset-%d".formatted(i)
            ), "http://my-connector/ids/data"));
            range(0, 15).forEach(i -> createDataOffer(dsl, today, Map.of(
                    AssetProperty.ASSET_ID, "urn:artifact:some-other-asset-%d".formatted(i)
            ), "http://my-connector/ids/data"));


            var query = new CatalogPageQuery();
            query.setSearchQuery("my-asset");
            query.setPageOneBased(2);
            query.setSorting(CatalogPageQuery.SortingEnum.TITLE);

            var result = brokerServerClient().brokerServerApi().catalogPage(query);

            assertThat(result.getDataOffers()).extracting(CatalogDataOffer::getAssetId)
                    .isEqualTo(range(10, 15).mapToObj("urn:artifact:my-asset-%d"::formatted).toList());

            var actual = result.getPaginationMetadata();
            assertThat(actual.getPageOneBased()).isEqualTo(2);
            assertThat(actual.getPageSize()).isEqualTo(10);
            assertThat(actual.getNumVisible()).isEqualTo(5);
            assertThat(actual.getNumTotal()).isEqualTo(15);
        });
    }

    @Test
    void testSortingByPopularity() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var today = OffsetDateTime.now().withNano(0);

            var endpoint = "http://my-connector/ids/data";
            createConnector(dsl, today, endpoint);
            createDataOffer(dsl, today, Map.of(AssetProperty.ASSET_ID, "urn:artifact:asset-1"), endpoint);
            createDataOffer(dsl, today, Map.of(AssetProperty.ASSET_ID, "urn:artifact:asset-2"), endpoint);
            createDataOffer(dsl, today, Map.of(AssetProperty.ASSET_ID, "urn:artifact:asset-3"), endpoint);

            range(0, 3).forEach(i -> dataOfferDetails(endpoint, "urn:artifact:asset-1"));
            range(0, 5).forEach(i -> dataOfferDetails(endpoint, "urn:artifact:asset-2"));


            var query = new CatalogPageQuery();
            query.setSorting(CatalogPageQuery.SortingEnum.VIEW_COUNT);

            var result = brokerServerClient().brokerServerApi().catalogPage(query);
            assertThat(result.getDataOffers()).extracting(CatalogDataOffer::getAssetId).containsExactly(
                    "urn:artifact:asset-2",
                    "urn:artifact:asset-1",
                    "urn:artifact:asset-3"
            );
        });
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

    private Policy dummyPolicy() {
        return Policy.Builder.newInstance()
                .type(PolicyType.SET)
                .build();
    }

    private DataOfferDetailPageResult dataOfferDetails(String endpoint, String assetId) {
        var query = DataOfferDetailPageQuery.builder()
                .connectorEndpoint(endpoint)
                .assetId(assetId)
                .build();
        return brokerServerClient().brokerServerApi().dataOfferDetailPage(query);
    }

    private String policyToJson(Policy policy) {
        return toJson(policy);
    }

    @SneakyThrows
    private String toJson(Object o) {
        return new ObjectMapper().writeValueAsString(o);
    }

    @SneakyThrows
    private String assetProperties(Map<String, String> assetProperties) {
        return new ObjectMapper().writeValueAsString(assetProperties);
    }
}
