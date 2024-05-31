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

import io.restassured.http.ContentType;
import org.eclipse.edc.connector.dataplane.selector.spi.store.DataPlaneInstanceStore;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.protocol.ProtocolWebhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

@ApiTest
@ExtendWith(EdcExtension.class)
class LastCommitInfoTest {

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.registerServiceMock(ProtocolWebhook.class, mock(ProtocolWebhook.class));
        extension.registerServiceMock(JsonLd.class, mock(JsonLd.class));
        extension.registerServiceMock(
                DataPlaneInstanceStore.class,
                mock(DataPlaneInstanceStore.class));
        extension.setConfiguration(Map.of(
                "web.http.port", String.valueOf(getFreePort()),
                "web.http.path", "/api",
                "web.http.management.port", String.valueOf(TestUtils.DATA_PORT),
                "web.http.management.path", "/api/v1/data",
                "edc.api.auth.key", TestUtils.AUTH_KEY,
                "edc.last.commit.info", "test env commit message",
                "edc.build.date", "2023-05-08T15:30:00Z"));
    }

    @Test
    void testEnvAndJar() {
        var request = given()
                .baseUri("http://localhost:" + TestUtils.DATA_PORT)
                .basePath("/api/v1/data")
                .header("X-Api-Key", TestUtils.AUTH_KEY)
                .when()
                .contentType(ContentType.JSON)
                .get("/last-commit-info")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);

        request.assertThat().body("envLastCommitInfo", equalTo("test env commit message"))
                .body("envBuildDate", equalTo("2023-05-08T15:30:00Z"))
                .body("jarLastCommitInfo", equalTo("test jar commit message"))
                .body("jarBuildDate", equalTo("2023-05-09T15:30:00Z"));

    }
}
