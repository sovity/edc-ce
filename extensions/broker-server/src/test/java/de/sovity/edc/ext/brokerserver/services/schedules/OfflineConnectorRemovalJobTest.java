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

package de.sovity.edc.ext.brokerserver.services.schedules;

import de.sovity.edc.ext.brokerserver.TestUtils;
import de.sovity.edc.ext.brokerserver.dao.ConnectorQueries;
import de.sovity.edc.ext.brokerserver.db.FlywayTestUtils;
import de.sovity.edc.ext.brokerserver.db.TestDatabase;
import de.sovity.edc.ext.brokerserver.db.TestDatabaseFactory;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.brokerserver.services.ConnectorCleaner;
import de.sovity.edc.ext.brokerserver.services.ConnectorKiller;
import de.sovity.edc.ext.brokerserver.services.OfflineConnectorKiller;
import de.sovity.edc.ext.brokerserver.services.config.BrokerServerSettings;
import de.sovity.edc.ext.brokerserver.services.logging.BrokerEventLogger;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.Duration;
import java.time.OffsetDateTime;

import static de.sovity.edc.ext.brokerserver.db.jooq.tables.Connector.CONNECTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OfflineConnectorRemovalJobTest {

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    BrokerServerSettings brokerServerSettings;
    OfflineConnectorKiller offlineConnectorKiller;

    @BeforeAll
    static void beforeAll() {
        FlywayTestUtils.migrate(TEST_DATABASE);
    }

    @BeforeEach
    void beforeEach() {
        brokerServerSettings = mock(BrokerServerSettings.class);
        offlineConnectorKiller = new OfflineConnectorKiller(
                brokerServerSettings,
                new ConnectorQueries(),
                new BrokerEventLogger(),
                new ConnectorKiller(),
                new ConnectorCleaner()
        );
    }

    @Test
    void test_offlineConnectorKiller_should_be_dead() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            when(brokerServerSettings.getKillOfflineConnectorsAfter()).thenReturn(Duration.ofDays(5));
            createConnector(dsl, 6);

            // act
            offlineConnectorKiller.killIfOfflineTooLong(dsl);

            // assert
            dsl.select().from(CONNECTOR).fetch().forEach(record -> {
                assertThat(record.get(CONNECTOR.CONNECTOR_ID)).isEqualTo("http://example.org");
                assertThat(record.get(CONNECTOR.ONLINE_STATUS)).isEqualTo(ConnectorOnlineStatus.DEAD);
            });
        });
    }

    @Test
    void test_offlineConnectorKiller_should_not_be_dead() {
        TEST_DATABASE.testTransaction(dsl -> {
            // arrange
            when(brokerServerSettings.getKillOfflineConnectorsAfter()).thenReturn(Duration.ofDays(5));
            createConnector(dsl, 2);

            // act
            offlineConnectorKiller.killIfOfflineTooLong(dsl);

            // assert
            dsl.select().from(CONNECTOR).fetch().forEach(record -> {
                assertThat(record.get(CONNECTOR.CONNECTOR_ID)).isEqualTo("http://example.org");
                assertThat(record.get(CONNECTOR.ONLINE_STATUS)).isNotEqualTo(ConnectorOnlineStatus.DEAD);
            });
        });
    }

    private static void createConnector(DSLContext dsl, int createdDaysAgo) {
        dsl.insertInto(CONNECTOR)
            .set(CONNECTOR.CONNECTOR_ID, "http://example.org")
            .set(CONNECTOR.ENDPOINT, TestUtils.MANAGEMENT_ENDPOINT)
            .set(CONNECTOR.ONLINE_STATUS, ConnectorOnlineStatus.OFFLINE)
            .set(CONNECTOR.LAST_SUCCESSFUL_REFRESH_AT, OffsetDateTime.now().minusDays(createdDaysAgo))
            .set(CONNECTOR.CREATED_AT, OffsetDateTime.now().minusDays(6))
            .set(CONNECTOR.DATA_OFFERS_EXCEEDED, ConnectorDataOffersExceeded.OK)
            .set(CONNECTOR.CONTRACT_OFFERS_EXCEEDED, ConnectorContractOffersExceeded.OK).execute();
    }
}
