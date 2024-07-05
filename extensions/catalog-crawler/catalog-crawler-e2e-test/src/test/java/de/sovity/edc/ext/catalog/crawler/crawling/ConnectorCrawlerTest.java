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

package de.sovity.edc.ext.catalog.crawler.crawling;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.ext.catalog.crawler.AssertionUtils;
import de.sovity.edc.ext.catalog.crawler.CrawlerExtensionContext;
import de.sovity.edc.ext.catalog.crawler.TestPolicy;
import de.sovity.edc.ext.catalog.crawler.TestUtils;
import de.sovity.edc.ext.catalog.crawler.client.gen.model.CatalogPageQuery;
import de.sovity.edc.ext.catalog.crawler.config.TestDatabase;
import de.sovity.edc.ext.catalog.crawler.config.TestDatabaseFactory;
import de.sovity.edc.ext.catalog.crawler.db.jooq.Tables;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.junit.extensions.EdcRuntimeExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@ApiTest
class ConnectorCrawlerTest {

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    @RegisterExtension
    static EdcExtension crawlerEdcContext = new EdcExtension();

    @RegisterExtension
    static EdcRuntimeExtension providerEdcContext = new EdcClassRuntimesExtension(
            new EdcRuntimeExtension(
                    ":system-tests:e2e-transfer-test:control-plane",
                    "consumer-control-plane",
                    CONSUMER.controlPlaneConfiguration()
            ),
            new EdcRuntimeExtension(
                    ":system-tests:e2e-transfer-test:backend-service",
                    "consumer-backend-service",
                    new HashMap<>() {
                        {
                            put("web.http.port", String.valueOf(CONSUMER.backendService().getPort()));
                        }
                    }
            ),
            new EdcRuntimeExtension(
                    ":system-tests:e2e-transfer-test:data-plane",
                    "provider-data-plane",
                    PROVIDER.dataPlaneConfiguration()
            ),
            new EdcRuntimeExtension(
                    ":system-tests:e2e-transfer-test:control-plane",
                    "provider-control-plane",
                    PROVIDER.controlPlaneConfiguration()
            ),
            new EdcRuntimeExtension(
                    ":system-tests:e2e-transfer-test:backend-service",
                    "provider-backend-service",
                    new HashMap<>() {
                        {
                            put("web.http.port", String.valueOf(PROVIDER.backendService().getPort()));
                        }
                    }
            )
    );

    private EdcClient providerClient;

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(TestUtils.createConfiguration(TEST_DATABASE, Map.of()));

        providerClient = EdcClient.builder()
                .managementApiUrl(TestUtils.MANAGEMENT_ENDPOINT)
                .managementApiKey(TestUtils.MANAGEMENT_API_KEY)
                .build();
    }

    @Test
    void testConnectorUpdate() {
        var policyId = createPolicyDefinition();
        var assetId = createAsset();
        createContractDefinition(policyId, assetId);
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var connectorCrawler = CrawlerExtensionContext.instance.connectorCrawler();
            var connectorRef = TestUtils.CONNECTOR_REF;

            // act
            connectorCrawler.crawlConnector(connectorRef);

            var c = Tables.CONNECTOR;
            var connector = dsl.fetchOne(c, c.MDS_ID.eq(connectorRef.getConnectorId()));
            assertThat(connector).isNotNull();
            assertThat(connector.getOnlineStatus()).isEqualTo(ConnectorOnlineStatus.ONLINE);
            assertThat(connector.getLastRefreshAttemptAt()).isCloseTo(OffsetDateTime.now(), within(1, ChronoUnit.SECONDS));
            assertThat(connector.getLastSuccessfulRefreshAt()).isCloseTo(OffsetDateTime.now(), within(1, ChronoUnit.SECONDS));
            assertThat(connector.getDataOffersExceeded()).isEqualTo(ConnectorDataOffersExceeded.OK);
            assertThat(connector.getContractOffersExceeded()).isEqualTo(ConnectorContractOffersExceeded.OK);

            var d = Tables.DATA_OFFER;
            var dataOffer = dsl.fetchOne(d, d.CONNECTOR_ID.eq(connector.getConnectorId()));
            assertThat(dataOffer).isNotNull();
            assertThat(dataOffer.getAssetId()).isEqualTo(assetId);
            assertThat(dataOffer.getConnectorId()).isEqualTo(connector.getConnectorId());
            assertThat(dataOffer.getCuratorOrganizationName()).isEqualTo(connectorRef.getOrganizationLegalName());
            assertThat(dataOffer.getAssetTitle()).isEqualTo("AssetName");
            assertThat(dataOffer.getDescription()).isEqualTo("AssetDescription");
            assertThat(dataOffer.getDataCategory()).isEqualTo("dataCategory");
            assertThat(dataOffer.getDataModel()).isEqualTo("dataModel");
            assertThat(dataOffer.getDataSubcategory()).isEqualTo("dataSubcategory");
            assertThat(dataOffer.getGeoReferenceMethod()).isEqualTo("geoReferenceMethod");
            assertThat(dataOffer.getKeywords()).containsExactly("keyword1", "keyword2");
            assertThat(dataOffer.getKeywordsCommaJoined()).isEqualTo("keyword1, keyword2");
            assertThat(dataOffer.getTransportMode()).isEqualTo("transportMode");
            assertThat(dataOffer.getVersion()).isEqualTo("");
            assertThat(dataOffer.getUpdatedAt()).isCloseTo(OffsetDateTime.now(), within(1, ChronoUnit.SECONDS));
            assertThat(dataOffer.getCreatedAt()).isCloseTo(OffsetDateTime.now(), within(1, ChronoUnit.SECONDS));
            assertThat(dataOffer.getUiAssetJson()).isEqualTo("");


            // assert
            var catalog = brokerServerClient.brokerServerApi().catalogPage(new CatalogPageQuery());
            assertThat(catalog.getDataOffers()).hasSize(1);
            var dataOffer = catalog.getDataOffers().get(0);
            assertThat(dataOffer.getContractOffers()).hasSize(1);
            var contractOffer = dataOffer.getContractOffers().get(0);
            var asset = dataOffer.getAsset();
            assertThat(asset.getAssetId()).isEqualTo(assetId);
            assertThat(asset.getTitle()).isEqualTo("AssetName");
            assertThat(asset.getConnectorEndpoint()).isEqualTo(TestUtils.PROTOCOL_ENDPOINT);
            assertThat(asset.getParticipantId()).isEqualTo(TestUtils.PARTICIPANT_ID);
            assertThat(asset.getKeywords()).isEqualTo(List.of("keyword1", "keyword2"));
            assertThat(asset.getDescription()).isEqualTo("AssetDescription");
            assertThat(asset.getVersion()).isEqualTo("1.0.0");
            assertThat(asset.getLanguage()).isEqualTo("en");
            assertThat(asset.getMediaType()).isEqualTo("application/json");
            assertThat(asset.getDataCategory()).isEqualTo("dataCategory");
            assertThat(asset.getDataSubcategory()).isEqualTo("dataSubcategory");
            assertThat(asset.getDataModel()).isEqualTo("dataModel");
            assertThat(asset.getGeoReferenceMethod()).isEqualTo("geoReferenceMethod");
            assertThat(asset.getTransportMode()).isEqualTo("transportMode");
            assertThat(asset.getLicenseUrl()).isEqualTo("https://license-url");
            assertThat(asset.getKeywords()).isEqualTo(List.of("keyword1", "keyword2"));
            assertThat(asset.getCreatorOrganizationName()).isEqualTo("Unknown");
            assertThat(asset.getPublisherHomepage()).isEqualTo("publisherHomepage");
            assertThat(asset.getHttpDatasourceHintsProxyMethod()).isFalse();
            assertThat(asset.getHttpDatasourceHintsProxyPath()).isFalse();
            assertThat(asset.getHttpDatasourceHintsProxyQueryParams()).isFalse();
            assertThat(asset.getHttpDatasourceHintsProxyBody()).isFalse();
            assertThat(asset.getCustomJsonAsString())
                    .isEqualTo("{\"a\":\"x\"}");
            assertThat(dataOffer.getAsset().getCustomJsonLdAsString())
                    .isEqualTo("{\"http://unknown/b\":{\"http://unknown/c\":\"y\"}}");
            assertThat(dataOffer.getAsset().getPrivateCustomJsonAsString()).isNull();
            assertThat(dataOffer.getAsset().getPrivateCustomJsonLdAsString()).isEqualTo("{}");
            var policy = contractOffer.getContractPolicy();
            assertThat(policy.getConstraints()).hasSize(1);
            AssertionUtils.assertEqualUsingJson(policy.getConstraints().get(0), TestPolicy.createAfterYesterdayConstraint());

            var connector = connectorPage.getConnectors().get(0);

            var connectorRecord = dsl.selectFrom(Tables.CONNECTOR).fetchOne();
            assertThat(connectorRecord.getMdsId()).isEqualTo("MDSL1234ZZ");
        });
    }

    private String createPolicyDefinition() {
        var policyDefinition = PolicyDefinitionCreateRequest.builder()
                .policyDefinitionId("policy-1")
                .policy(TestPolicy.createAfterYesterdayPolicyEdcGen())
                .build();

        return providerClient.uiApi().createPolicyDefinition(policyDefinition).getId();
    }

    public void createContractDefinition(String policyId, String assetId) {
        providerClient.uiApi().createContractDefinition(ContractDefinitionRequest.builder()
                .contractDefinitionId("cd-1")
                .accessPolicyId(policyId)
                .contractPolicyId(policyId)
                .assetSelector(List.of(UiCriterion.builder()
                        .operandLeft(Prop.Edc.ID)
                        .operator(UiCriterionOperator.EQ)
                        .operandRight(UiCriterionLiteral.builder()
                                .type(UiCriterionLiteralType.VALUE)
                                .value(assetId)
                                .build())
                        .build()))
                .build());
    }

    private String createAsset() {
        return providerClient.uiApi().createAsset(UiAssetCreateRequest.builder()
                .id("asset-1")
                .title("AssetName")
                .description("AssetDescription")
                .licenseUrl("https://license-url")
                .version("1.0.0")
                .language("en")
                .mediaType("application/json")
                .dataCategory("dataCategory")
                .dataSubcategory("dataSubcategory")
                .dataModel("dataModel")
                .geoReferenceMethod("geoReferenceMethod")
                .transportMode("transportMode")
                .keywords(List.of("keyword1", "keyword2"))
                .publisherHomepage("publisherHomepage")
                .dataAddressProperties(Map.of(
                        Prop.Edc.TYPE, "HttpData",
                        Prop.Edc.METHOD, "GET",
                        Prop.Edc.BASE_URL, "http://some.url"
                ))
                .customJsonAsString("{\"a\":\"x\"}")
                .customJsonLdAsString("{\"http://unknown/b\":{\"http://unknown/c\":\"y\"}}")
                .privateCustomJsonAsString("{\"a-private\":\"x-private\"}")
                .privateCustomJsonLdAsString("{\"http://unknown/b-private\":{\"http://unknown/c-private\":\"y-private\"}}")
                .build()).getId();
    }
}
