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

import de.sovity.edc.ext.brokerserver.TestUtils;
import de.sovity.edc.ext.brokerserver.client.BrokerServerClient;
import de.sovity.edc.ext.brokerserver.client.gen.model.ConnectorListEntry;
import de.sovity.edc.ext.brokerserver.client.gen.model.ConnectorPageQuery;
import de.sovity.edc.ext.brokerserver.db.TestDatabase;
import de.sovity.edc.ext.brokerserver.db.TestDatabaseFactory;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static de.sovity.edc.ext.brokerserver.TestUtils.ADMIN_API_KEY;
import static de.sovity.edc.ext.brokerserver.TestUtils.brokerServerClient;
import static de.sovity.edc.ext.brokerserver.TestUtils.createConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class AddConnectorsApiTest {
    BrokerServerClient client;

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration(TEST_DATABASE, Map.of()));
        client = brokerServerClient();
    }

    @Test
    void testAddConnectors() {
        TEST_DATABASE.testTransaction(dsl -> {
            client.brokerServerApi().addConnectors(ADMIN_API_KEY, List.of());

            client.brokerServerApi().addConnectors(ADMIN_API_KEY, Arrays.asList(
                    null,
                    "",
                    "  ",
                    "\t",
                    "http://a",
                    "http://b"
            ));

            assertThat(client.brokerServerApi().connectorPage(new ConnectorPageQuery()).getConnectors())
                    .extracting(ConnectorListEntry::getEndpoint)
                    .containsExactlyInAnyOrder("http://a", "http://b");

            client.brokerServerApi().addConnectors(ADMIN_API_KEY, Arrays.asList(
                    "http://b",
                    " http://b\r\n",
                    "http://c"
            ));

            assertThat(client.brokerServerApi().connectorPage(new ConnectorPageQuery()).getConnectors())
                    .extracting(ConnectorListEntry::getEndpoint)
                    .containsExactlyInAnyOrder("http://a", "http://b", "http://c");
        });
    }

    @Test
    void testAddWrongApiKey() {
        TEST_DATABASE.testTransaction(dsl -> TestUtils.assertIs401(() ->
                client.brokerServerApi().addConnectors("wrong-api-key", List.of())));
    }
}
