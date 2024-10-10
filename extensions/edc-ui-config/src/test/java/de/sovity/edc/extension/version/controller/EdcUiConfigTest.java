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

import de.sovity.edc.extension.e2e.connector.config.ConnectorBootConfig;
import de.sovity.edc.extension.e2e.junit.CeIntegrationTestExtension;
import de.sovity.edc.extension.e2e.junit.edc.RuntimeExtensionFixed;
import de.sovity.edc.utils.config.ConfigUtils;
import io.restassured.http.ContentType;
import org.eclipse.edc.connector.dataplane.selector.spi.store.DataPlaneInstanceStore;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.protocol.ProtocolWebhook;
import org.eclipse.edc.spi.system.configuration.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

@ApiTest
class EdcUiConfigTest {
    private static final String SOME_EXAMPLE_PROP = "this should also be passed through";

    @RegisterExtension
    static CeIntegrationTestExtension extension = CeIntegrationTestExtension.builder()
        .configOverrides(config -> config.property("edc.ui.some.example.prop", SOME_EXAMPLE_PROP))
        .skipDb(true)
        .build();

    @BeforeEach
    void setUp(RuntimeExtensionFixed extension) {
        extension.registerServiceMock(ProtocolWebhook.class, mock(ProtocolWebhook.class));
        extension.registerServiceMock(JsonLd.class, mock(JsonLd.class));
        extension.registerServiceMock(DataPlaneInstanceStore.class, mock(DataPlaneInstanceStore.class));
    }

    @Test
    void testEdcUiConfigWithEverythingSet(Config config) {
        var request = given()
            .baseUri(ConfigUtils.getManagementApiUrl(config))
            .header("X-Api-Key", ConfigUtils.getManagementApiKey(config))
            .when()
            .contentType(ContentType.JSON)
            .get("/edc-ui-config")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON);

        request.assertThat()
            .body("EDC_UI_SOME_EXAMPLE_PROP", equalTo(SOME_EXAMPLE_PROP));
    }
}
