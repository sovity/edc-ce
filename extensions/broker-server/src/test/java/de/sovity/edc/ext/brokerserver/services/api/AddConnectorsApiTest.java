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

import de.sovity.edc.ext.brokerserver.BrokerServerExtension;
import de.sovity.edc.ext.brokerserver.client.gen.ApiException;
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
import static de.sovity.edc.ext.brokerserver.TestUtils.createConfiguration;
import static de.sovity.edc.ext.brokerserver.TestUtils.brokerServerClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ApiTest
@ExtendWith(EdcExtension.class)
class AddConnectorsApiTest {

    @RegisterExtension
    private static final TestDatabase TEST_DATABASE = TestDatabaseFactory.getTestDatabase();

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration(TEST_DATABASE, Map.of(
            BrokerServerExtension.CATALOG_PAGE_PAGE_SIZE, "10",
            BrokerServerExtension.DEFAULT_CONNECTOR_DATASPACE, "MDS",
            BrokerServerExtension.KNOWN_DATASPACE_CONNECTORS, "Example1=http://my-connector2/ids/data,Example2=http://my-connector3/ids/data"
        )));
    }

    @Test
    void testAddAndMerge() {
        TEST_DATABASE.testTransaction(dsl -> {
            var client = brokerServerClient();

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
    void testWrongApiKey() {
        TEST_DATABASE.testTransaction(dsl -> {
            var client = brokerServerClient();

            assertThatThrownBy(() -> client.brokerServerApi().addConnectors("wrong-api-key", List.of()))
                    .isInstanceOf(ApiException.class)
                    .satisfies(ex -> {
                        var apiException = (ApiException) ex;
                        assertThat(apiException.getCode()).isEqualTo(401);
                    });
        });
    }
}
