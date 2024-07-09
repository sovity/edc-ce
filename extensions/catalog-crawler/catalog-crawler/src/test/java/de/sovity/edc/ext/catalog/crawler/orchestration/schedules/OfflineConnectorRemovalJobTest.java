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

import de.sovity.edc.ext.catalog.crawler.CrawlerTestDb;
import de.sovity.edc.ext.catalog.crawler.TestData;
import de.sovity.edc.ext.catalog.crawler.crawling.OfflineConnectorCleaner;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventLogger;
import de.sovity.edc.ext.catalog.crawler.dao.CatalogCleaner;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorQueries;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorStatusUpdater;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.catalog.crawler.orchestration.config.CrawlerConfig;
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
    private static final CrawlerTestDb TEST_DATABASE = new CrawlerTestDb();

    ConnectorRef connectorRef = TestData.connectorRef;

    CrawlerConfig crawlerConfig;
    OfflineConnectorCleaner offlineConnectorCleaner;
    ConnectorQueries connectorQueries;

    @BeforeEach
    void beforeEach() {
        crawlerConfig = mock(CrawlerConfig.class);
        connectorQueries = new ConnectorQueries(crawlerConfig);
        offlineConnectorCleaner = new OfflineConnectorCleaner(
                crawlerConfig,
                new ConnectorQueries(crawlerConfig),
                new CrawlerEventLogger(),
                new ConnectorStatusUpdater(),
                new CatalogCleaner()
        );
        when(crawlerConfig.getEnvironmentId()).thenReturn(connectorRef.getEnvironmentId());
    }

    @Test
    void test_offlineConnectorCleaner_should_be_dead() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            when(crawlerConfig.getKillOfflineConnectorsAfter()).thenReturn(Duration.ofDays(5));
            TestData.insertConnector(dsl, connectorRef, record -> {
                record.setOnlineStatus(ConnectorOnlineStatus.OFFLINE);
                record.setLastSuccessfulRefreshAt(OffsetDateTime.now().minusDays(6));
            });

            // act
            offlineConnectorCleaner.cleanConnectorsIfOfflineTooLong(dsl);

            // assert
            var connector = dsl.fetchOne(CONNECTOR, CONNECTOR.CONNECTOR_ID.eq(connectorRef.getConnectorId()));
            assertThat(connector.getOnlineStatus()).isEqualTo(ConnectorOnlineStatus.DEAD);
        });
    }

    @Test
    void test_offlineConnectorCleaner_should_not_be_dead() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            when(crawlerConfig.getKillOfflineConnectorsAfter()).thenReturn(Duration.ofDays(5));
            TestData.insertConnector(dsl, connectorRef, record -> {
                record.setOnlineStatus(ConnectorOnlineStatus.OFFLINE);
                record.setLastSuccessfulRefreshAt(OffsetDateTime.now().minusDays(2));
            });

            // act
            offlineConnectorCleaner.cleanConnectorsIfOfflineTooLong(dsl);

            // assert
            var connector = dsl.fetchOne(CONNECTOR, CONNECTOR.CONNECTOR_ID.eq(connectorRef.getConnectorId()));
            assertThat(connector.getOnlineStatus()).isEqualTo(ConnectorOnlineStatus.OFFLINE);
        });
    }

}
