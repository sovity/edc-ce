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

import de.sovity.edc.ext.brokerserver.BrokerServerExtension;
import de.sovity.edc.ext.brokerserver.db.TestDatabase;
import de.sovity.edc.ext.brokerserver.db.TestDatabaseFactory;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Map;

import static de.sovity.edc.ext.brokerserver.TestUtils.createConfiguration;

@ApiTest
@ExtendWith(EdcExtension.class)
public class BrokerEventLoggerTest {

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration(TEST_DATABASE, Map.of(
                BrokerServerExtension.KNOWN_CONNECTORS, "https://example.com/ids/data",
                BrokerServerExtension.NUM_THREADS, "0"
        )));
    }

    @Test
    void testDataOfferWriter_allSortsOfUpdates() {
        TEST_DATABASE.testTransaction(dsl -> {
            var brokerEventLogger = new BrokerEventLogger();

            // Test that insertions insert required fields and don't cause DB errors
            brokerEventLogger.logConnectorUpdateSuccess(dsl, "https://example.com/ids/data", new ConnectorChangeTracker());
            brokerEventLogger.logConnectorUpdateFailure(dsl, "https://example.com/ids/data", new BrokerEventErrorMessage("Message", "Stacktrace"));
            brokerEventLogger.logConnectorUpdateStatusChange(dsl, "https://example.com/ids/data", ConnectorOnlineStatus.ONLINE);
            brokerEventLogger.logConnectorUpdateStatusChange(dsl, "https://example.com/ids/data", ConnectorOnlineStatus.OFFLINE);
        });
    }
}
