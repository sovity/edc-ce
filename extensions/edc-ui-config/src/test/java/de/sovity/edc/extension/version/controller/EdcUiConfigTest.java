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

package de.sovity.edc.extension.version.controller;

import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory;
import de.sovity.edc.extension.e2e.db.JdbcCredentials;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import io.restassured.http.ContentType;
import org.eclipse.edc.connector.dataplane.selector.spi.store.DataPlaneInstanceStore;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.protocol.ProtocolWebhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ApiTest
@ExtendWith(EdcExtension.class)
class EdcUiConfigTest {
    private static final String SOME_EXAMPLE_PROP = "this should also be passed through";

    private ConnectorConfig config;

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.registerServiceMock(ProtocolWebhook.class, mock(ProtocolWebhook.class));
        extension.registerServiceMock(JsonLd.class, mock(JsonLd.class));
        extension.registerServiceMock(DataPlaneInstanceStore.class, mock(DataPlaneInstanceStore.class));

        var testDatabase = mock(TestDatabase.class);
        when(testDatabase.getJdbcCredentials()).thenReturn(new JdbcCredentials("unused", "unused", "unused"));

        config = ConnectorConfigFactory.forTestDatabase("provider", testDatabase);
        config.setProperty("edc.ui.some.example.prop", SOME_EXAMPLE_PROP);

        extension.setConfiguration(config.getProperties());
    }

    @Test
    void testEdcUiConfigWithEverythingSet() {
        var request = given()
            .baseUri(config.getManagementApiUrl())
            .header("X-Api-Key", config.getManagementApiKey())
            .when()
            .contentType(ContentType.JSON)
            .get("/edc-ui-config")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON);

        request.assertThat()
            .body("EDC_UI_SOME_EXAMPLE_PROP", equalTo(SOME_EXAMPLE_PROP));
        ;

    }
}
