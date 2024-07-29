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
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.ext.catalog.crawler;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.DataSourceType;
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateDto;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.client.gen.model.UiDataSource;
import de.sovity.edc.client.gen.model.UiDataSourceHttpData;
import de.sovity.edc.client.gen.model.UiPolicyConstraint;
import de.sovity.edc.client.gen.model.UiPolicyExpression;
import de.sovity.edc.client.gen.model.UiPolicyExpressionType;
import de.sovity.edc.client.gen.model.UiPolicyLiteral;
import de.sovity.edc.client.gen.model.UiPolicyLiteralType;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.db.jooq.Tables;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.catalog.crawler.utils.CrawlerDbAccess;
import de.sovity.edc.ext.catalog.crawler.utils.TestData;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.db.EdcRuntimeExtensionWithTestDatabase;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.getFreePortRange;
import static de.sovity.edc.extension.e2e.connector.config.api.EdcApiConfigFactory.configureApi;
import static org.assertj.core.api.Assertions.assertThat;

class CrawlerE2eTest {
    private static ConnectorConfig connectorConfig;
    private static EdcClient connectorClient;

    @RegisterExtension
    private static EdcRuntimeExtensionWithTestDatabase providerExtension = new EdcRuntimeExtensionWithTestDatabase(
            ":launchers:connectors:sovity-dev",
            "provider",
            testDatabase -> {
                connectorConfig = forTestDatabase("MDSL1234XX.C1234XX", testDatabase);
                connectorClient = EdcClient.builder()
                        .managementApiUrl(connectorConfig.getManagementEndpoint().getUri().toString())
                        .managementApiKey(connectorConfig.getProperties().get("edc.api.auth.key"))
                        .build();
                return connectorConfig.getProperties();
            }
    );

    @RegisterExtension
    static EdcRuntimeExtensionWithTestDatabase crawlerExtension = new EdcRuntimeExtensionWithTestDatabase(
            ":launchers:connectors:catalog-crawler-dev",
            "crawler",
            testDatabase -> {
                var firstPort = getFreePortRange(5);

                var props = new HashMap<String, String>();
                props.put("edc.participant.id", "broker");
                props.put(CrawlerExtension.EXTENSION_ENABLED, "true");
                props.put(CrawlerExtension.ENVIRONMENT_ID, "test");
                props.put(CrawlerExtension.JDBC_URL, testDatabase.getJdbcCredentials().jdbcUrl());
                props.put(CrawlerExtension.JDBC_USER, testDatabase.getJdbcCredentials().jdbcUser());
                props.put(CrawlerExtension.JDBC_PASSWORD, testDatabase.getJdbcCredentials().jdbcPassword());
                props.put(CrawlerExtension.DB_CONNECTION_POOL_SIZE, "30");
                props.put(CrawlerExtension.DB_CONNECTION_TIMEOUT_IN_MS, "1000");
                props.put(CrawlerExtension.DB_MIGRATE, "true");
                props.put(CrawlerExtension.DB_CLEAN, "true");
                props.put(CrawlerExtension.DB_CLEAN_ENABLED, "true");
                props.put(CrawlerExtension.DB_ADDITIONAL_FLYWAY_MIGRATION_LOCATIONS, "classpath:db-crawler/migration-test-utils");
                props.put(CrawlerExtension.NUM_THREADS, "2");
                props.put(CrawlerExtension.MAX_DATA_OFFERS_PER_CONNECTOR, "100");
                props.put(CrawlerExtension.MAX_CONTRACT_OFFERS_PER_DATA_OFFER, "100");
                props.putAll(configureApi(firstPort, "managementApiKey").getProperties());

                var everySeconds = "* * * * * ?";
                props.put(CrawlerExtension.CRON_ONLINE_CONNECTOR_REFRESH, everySeconds);
                props.put(CrawlerExtension.CRON_OFFLINE_CONNECTOR_REFRESH, everySeconds);
                props.put(CrawlerExtension.CRON_DEAD_CONNECTOR_REFRESH, everySeconds);
                props.put(CrawlerExtension.SCHEDULED_KILL_OFFLINE_CONNECTORS, everySeconds);
                props.put(CrawlerExtension.KILL_OFFLINE_CONNECTORS_AFTER, "P1D");
                props.put("my.edc.datasource.placeholder.baseurl", "http://example.com/edc/backend");

                return props;
            }
    );

    private final String dataOfferId = "my-data-offer";

    @Test
    void crawlSingleDataOffer() {
        // arrange
        createPolicy();
        createAsset();
        createContractDefinition();

        var connectorRef = new ConnectorRef(
                "MDSL1234XX.C1234XX",
                "test",
                "My Org",
                "MDSL1234XX",
                connectorConfig.getProtocolEndpoint().getUri().toString()
        );

        crawlerTransaction(dsl -> {
            TestData.insertConnector(dsl, connectorRef, connectorRecord -> {
                connectorRecord.setOnlineStatus(ConnectorOnlineStatus.OFFLINE);
                connectorRecord.setCreatedAt(OffsetDateTime.now());
            });
        });

        // act / await crawl
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(mapExceptionsToAssertionError(() ->
                        crawlerTransaction(dsl -> assertDataOfferInCatalog(dsl, connectorRef))));
    }

    private void assertDataOfferInCatalog(DSLContext dsl, ConnectorRef connectorRef) {
        var c = Tables.CONNECTOR;
        var connector = dsl.fetchOne(c, c.CONNECTOR_ID.eq(connectorRef.getConnectorId()));
        assertThat(connector).isNotNull();
        assertThat(connector.getOnlineStatus()).isEqualTo(ConnectorOnlineStatus.ONLINE);

        var d = Tables.DATA_OFFER;
        var dataOffers = dsl.fetch(d, d.CONNECTOR_ID.eq(connectorRef.getConnectorId()));
        assertThat(dataOffers).hasSize(1);
        assertThat(dataOffers.get(0).getAssetId()).isEqualTo(dataOfferId);
        assertThat(dataOffers.get(0).getAssetTitle()).isEqualTo("My Data Offer");
    }

    private void createAsset() {
        var asset = UiAssetCreateRequest.builder()
                .id(dataOfferId)
                .title("My Data Offer")
                .description("Example Data Offer.")
                .version("2023-11")
                .language("EN")
                .publisherHomepage("https://my-department.my-org.com/my-data-offer")
                .licenseUrl("https://my-department.my-org.com/my-data-offer#license")
                .dataSource(UiDataSource.builder()
                        .type(DataSourceType.HTTP_DATA)
                        .httpData(UiDataSourceHttpData.builder()
                                .baseUrl("http://0.0.0.0")
                                .build())
                        .build())
                .build();

        connectorClient.uiApi().createAsset(asset);
    }

    private void createPolicy() {
        var afterYesterday = UiPolicyConstraint.builder()
                .left("POLICY_EVALUATION_TIME")
                .operator(OperatorDto.GT)
                .right(UiPolicyLiteral.builder()
                        .type(UiPolicyLiteralType.STRING)
                        .value(OffsetDateTime.now().minusDays(1).toString())
                        .build())
                .build();

        var beforeTomorrow = UiPolicyConstraint.builder()
                .left("POLICY_EVALUATION_TIME")
                .operator(OperatorDto.LT)
                .right(UiPolicyLiteral.builder()
                        .type(UiPolicyLiteralType.STRING)
                        .value(OffsetDateTime.now().plusDays(1).toString())
                        .build())
                .build();

        var expression = UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.AND)
            .expressions(Stream.of(afterYesterday, beforeTomorrow)
                .map(it -> UiPolicyExpression.builder()
                    .type(UiPolicyExpressionType.CONSTRAINT)
                    .constraint(it)
                    .build())
                .toList())
            .build();

        var policyDefinition = PolicyDefinitionCreateDto.builder()
                .policyDefinitionId(dataOfferId)
                .expression(expression)
                .build();

        connectorClient.uiApi().createPolicyDefinitionV2(policyDefinition);
    }

    private void createContractDefinition() {
        var contractDefinition = ContractDefinitionRequest.builder()
                .contractDefinitionId(dataOfferId)
                .accessPolicyId(dataOfferId)
                .contractPolicyId(dataOfferId)
                .assetSelector(List.of(UiCriterion.builder()
                        .operandLeft(Prop.Edc.ID)
                        .operator(UiCriterionOperator.EQ)
                        .operandRight(UiCriterionLiteral.builder()
                                .type(UiCriterionLiteralType.VALUE)
                                .value(dataOfferId)
                                .build())
                        .build()))
                .build();

        connectorClient.uiApi().createContractDefinition(contractDefinition);
    }

    private void crawlerTransaction(Consumer<DSLContext> withDsl) {
        CrawlerDbAccess.transaction(crawlerExtension.getTestDatabase(), withDsl);
    }

    private ThrowingRunnable mapExceptionsToAssertionError(ThrowingRunnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        };
    }
}
