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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

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
            brokerEventLogger.logConnectorUpdated(dsl, "https://example.com/ids/data", new ConnectorChangeTracker());
            brokerEventLogger.logConnectorOnline(dsl, "https://example.com/ids/data");
            brokerEventLogger.logConnectorOffline(dsl, "https://example.com/ids/data", new BrokerEventErrorMessage("Message", "Stacktrace"));
        });
    }
}
