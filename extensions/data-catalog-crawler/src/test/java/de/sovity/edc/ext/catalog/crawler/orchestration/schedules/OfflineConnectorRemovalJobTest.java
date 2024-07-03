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
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.catalog.crawler.orchestration.schedules;

import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorQueries;
import de.sovity.edc.ext.catalog.crawler.config.FlywayTestUtils;
import de.sovity.edc.ext.catalog.crawler.config.TestDatabase;
import de.sovity.edc.ext.catalog.crawler.config.TestDatabaseFactory;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.catalog.crawler.dao.CatalogCleaner;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorStatusUpdater;
import de.sovity.edc.ext.catalog.crawler.crawling.OfflineConnectorCleaner;
import de.sovity.edc.ext.catalog.crawler.orchestration.config.CrawlerConfig;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventLogger;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.Duration;
import java.time.OffsetDateTime;

import static de.sovity.edc.ext.catalog.crawler.db.jooq.tables.Connector.CONNECTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OfflineConnectorRemovalJobTest {

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    CrawlerConfig crawlerConfig;
    OfflineConnectorCleaner offlineConnectorCleaner;
    ConnectorQueries connectorQueries;

    @BeforeAll
    static void beforeAll() {
        FlywayTestUtils.migrate(TEST_DATABASE);
    }

    @BeforeEach
    void beforeEach() {
        crawlerConfig = mock(CrawlerConfig.class);
        connectorQueries = new ConnectorQueries();
        offlineConnectorCleaner = new OfflineConnectorCleaner(
                crawlerConfig,
                new ConnectorQueries(),
                new CrawlerEventLogger(),
                new ConnectorStatusUpdater(),
                new CatalogCleaner(connectorQueries)
        );
    }

    @Test
    void test_offlineConnectorKiller_should_be_dead() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            when(crawlerConfig.getKillOfflineConnectorsAfter()).thenReturn(Duration.ofDays(5));
            createConnector(dsl, 6);

            // act
            offlineConnectorCleaner.cleanConnectorsIfOfflineTooLong(dsl);

            // assert
            dsl.select().from(CONNECTOR).fetch().forEach(record -> {
                assertThat(record.get(CONNECTOR.ENDPOINT_URL)).isEqualTo("https://my-connector/api/dsp");
                assertThat(record.get(CONNECTOR.ONLINE_STATUS)).isEqualTo(ConnectorOnlineStatus.DEAD);
            });
        });
    }

    @Test
    void test_offlineConnectorKiller_should_not_be_dead() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            when(crawlerConfig.getKillOfflineConnectorsAfter()).thenReturn(Duration.ofDays(5));
            createConnector(dsl, 2);

            // act
            offlineConnectorCleaner.cleanConnectorsIfOfflineTooLong(dsl);

            // assert
            dsl.select().from(CONNECTOR).fetch().forEach(record -> {
                assertThat(record.get(CONNECTOR.ENDPOINT_URL)).isEqualTo("https://my-connector/api/dsp");
                assertThat(record.get(CONNECTOR.ONLINE_STATUS)).isNotEqualTo(ConnectorOnlineStatus.DEAD);
            });
        });
    }

    private static void createConnector(DSLContext dsl, int createdDaysAgo) {
        dsl.insertInto(CONNECTOR)
                .set(CONNECTOR.ENDPOINT_URL, "https://my-connector/api/dsp")
                .set(CONNECTOR.CONNECTOR_ID, "my-connector")
                .set(CONNECTOR.ONLINE_STATUS, ConnectorOnlineStatus.OFFLINE)
                .set(CONNECTOR.LAST_SUCCESSFUL_REFRESH_AT, OffsetDateTime.now().minusDays(createdDaysAgo))
                .set(CONNECTOR.CREATED_AT, OffsetDateTime.now().minusDays(6))
                .set(CONNECTOR.DATA_OFFERS_EXCEEDED, ConnectorDataOffersExceeded.OK)
                .set(CONNECTOR.CONTRACT_OFFERS_EXCEEDED, ConnectorContractOffersExceeded.OK).execute();
    }
}
