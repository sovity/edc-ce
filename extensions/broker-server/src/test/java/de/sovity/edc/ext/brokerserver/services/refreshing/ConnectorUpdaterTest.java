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

package de.sovity.edc.ext.brokerserver.services.refreshing;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.ext.brokerserver.AssertionUtils;
import de.sovity.edc.ext.brokerserver.BrokerServerExtensionContext;
import de.sovity.edc.ext.brokerserver.TestUtils;
import de.sovity.edc.ext.brokerserver.client.BrokerServerClient;
import de.sovity.edc.ext.brokerserver.client.gen.model.CatalogPageQuery;
import de.sovity.edc.ext.brokerserver.client.gen.model.ConnectorListEntry;
import de.sovity.edc.ext.brokerserver.client.gen.model.ConnectorPageQuery;
import de.sovity.edc.ext.brokerserver.db.TestDatabase;
import de.sovity.edc.ext.brokerserver.db.TestDatabaseFactory;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static de.sovity.edc.ext.brokerserver.TestPolicy.createAfterYesterdayConstraint;
import static de.sovity.edc.ext.brokerserver.TestPolicy.createAfterYesterdayPolicyEdcGen;
import static de.sovity.edc.ext.brokerserver.TestUtils.createConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@ApiTest
class ConnectorUpdaterTest {

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    @RegisterExtension
    static EdcExtension consumerEdcContext = new EdcExtension();

    private EdcClient providerClient;

    private BrokerServerClient brokerServerClient;

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration(TEST_DATABASE, Map.of()));

        providerClient = EdcClient.builder()
            .managementApiUrl(TestUtils.MANAGEMENT_ENDPOINT)
            .managementApiKey(TestUtils.MANAGEMENT_API_KEY)
            .build();

        brokerServerClient = BrokerServerClient.builder()
            .managementApiUrl(TestUtils.MANAGEMENT_ENDPOINT)
            .managementApiKey(TestUtils.MANAGEMENT_API_KEY)
            .build();
    }

    @Test
    void testConnectorUpdate() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            var connectorUpdater = BrokerServerExtensionContext.instance.connectorUpdater();
            var connectorCreator = BrokerServerExtensionContext.instance.connectorCreator();
            String connectorEndpoint = TestUtils.PROTOCOL_ENDPOINT;

            var policyId = createPolicyDefinition();
            var assetId = createAsset();
            createContractDefinition(policyId, assetId);
            connectorCreator.addConnector(dsl, connectorEndpoint);

            // act
            connectorUpdater.updateConnector(connectorEndpoint);
            var connectorPage = brokerServerClient.brokerServerApi().connectorPage(new ConnectorPageQuery());

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
            assertThat(asset.getAdditionalProperties())
                    .containsExactlyEntriesOf(Map.of("http://unknown/a", "x"));
            assertThat(dataOffer.getAsset().getAdditionalJsonProperties())
                    .containsExactlyEntriesOf(Map.of("http://unknown/b", "{\"http://unknown/c\":\"y\"}"));
            assertThat(dataOffer.getAsset().getPrivateProperties()).isEmpty();
            assertThat(dataOffer.getAsset().getPrivateJsonProperties()).isEmpty();
            var policy = contractOffer.getContractPolicy();
            assertThat(policy.getConstraints()).hasSize(1);
            AssertionUtils.assertEqualUsingJson(policy.getConstraints().get(0), createAfterYesterdayConstraint());

            var connector = connectorPage.getConnectors().get(0);
            assertThat(connector.getOnlineStatus()).isEqualTo(ConnectorListEntry.OnlineStatusEnum.ONLINE);
            assertThat(connector.getParticipantId()).isEqualTo(TestUtils.PARTICIPANT_ID);
            assertThat(connector.getOrganizationName()).isEqualTo("Unknown");
            assertThat(connector.getLastRefreshAttemptAt()).isCloseTo(OffsetDateTime.now(), within(1, ChronoUnit.SECONDS));

            var connectorRecord = dsl.selectFrom(Tables.CONNECTOR).fetchOne();
            assertThat(connectorRecord.getMdsId()).isEqualTo("MDSL1234ZZ");
        });
    }

    private String createPolicyDefinition() {
        var policyDefinition = PolicyDefinitionCreateRequest.builder()
                .policyDefinitionId("policy-1")
                .policy(createAfterYesterdayPolicyEdcGen())
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
            .additionalProperties(Map.of("http://unknown/a", "x"))
            .additionalJsonProperties(Map.of("http://unknown/b", "{\"http://unknown/c\":\"y\"}"))
            .privateProperties(Map.of("http://unknown/a-private", "x-private"))
            .privateJsonProperties(Map.of("http://unknown/b-private", "{\"http://unknown/c-private\":\"y-private\"}"))
            .build()).getId();
    }
}
