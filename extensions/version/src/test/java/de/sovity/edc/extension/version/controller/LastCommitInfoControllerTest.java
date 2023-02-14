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
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.extension.version.controller;

import io.restassured.http.ContentType;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;
import static org.hamcrest.Matchers.containsString;

@ApiTest
@ExtendWith(EdcExtension.class)
public class LastCommitInfoControllerTest {

    private final int dataPort = getFreePort();
    private final String authKey = "123456";

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(Map.of(
                "web.http.port", String.valueOf(getFreePort()),
                "web.http.path", "/api",
                "web.http.data.port", String.valueOf(dataPort),
                "web.http.data.path", "/api/v1/data",
                "edc.api.auth.key", authKey,
                "edc.last.commit.info.env", "Will be set by pipeline."));
    }

    @Test
    void testJarLastCommitInfo() {
        var request = given()
                .baseUri("http://localhost:" + dataPort)
                .basePath("/api/v1/data")
                .header("x-api-key", authKey)
                .when()
                .contentType(ContentType.TEXT)
                .get(String.format("/last-commit-info"))
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
        request.assertThat().body(containsString("Pipeline"));
    }

    @Test
    void testEnvLastCommitInfo(){
        var request = given()
                .baseUri("http://localhost:" + dataPort)
                .basePath("/api/v1/data")
                .header("x-api-key", authKey)
                .when()
                .contentType(ContentType.TEXT)
                .get(String.format("/last-commit-info/env"))
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
        request.assertThat().body(containsString("pipeline"));
    }
}