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

package de.sovity.edc.ext.brokerserver.services.logging;

import de.sovity.edc.ext.brokerserver.db.FlywayTestUtils;
import de.sovity.edc.ext.brokerserver.db.TestDatabase;
import de.sovity.edc.ext.brokerserver.db.TestDatabaseFactory;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BrokerEventLoggerTest {
    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    @BeforeAll
    static void setup() {
        FlywayTestUtils.migrate(TEST_DATABASE);
    }

    @Test
    void testDataOfferWriter_allSortsOfUpdates() {
        TEST_DATABASE.testTransaction(dsl -> {
            var brokerEventLogger = new BrokerEventLogger();

            // Test that insertions insert required fields and don't cause DB errors
            String endpoint = "https://example.com/api/dsp";
            brokerEventLogger.logConnectorUpdated(dsl, endpoint, new ConnectorChangeTracker());
            brokerEventLogger.logConnectorOnline(dsl, endpoint);
            brokerEventLogger.logConnectorOffline(dsl, endpoint, new BrokerEventErrorMessage("Message", "Stacktrace"));
            brokerEventLogger.logConnectorUpdateContractOfferLimitExceeded(dsl, 10, endpoint);
            brokerEventLogger.logConnectorUpdateContractOfferLimitOk(dsl, endpoint);
            brokerEventLogger.logConnectorUpdateDataOfferLimitExceeded(dsl, 10, endpoint);
            brokerEventLogger.logConnectorUpdateDataOfferLimitOk(dsl, endpoint);
            brokerEventLogger.logConnectorsDeleted(dsl, Set.of(endpoint));

            assertThat(numLogEntries(dsl)).isEqualTo(8);
        });
    }

    private Integer numLogEntries(DSLContext dsl) {
        return dsl.selectCount().from(Tables.BROKER_EVENT_LOG).fetchOne().component1();
    }
}
