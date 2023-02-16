/*
 *  Copyright (c) 2022 sovity GmbH
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
import io.restassured.response.ValidatableResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;

public class TestUtils {

    private static final int DATA_PORT = getFreePort();
    private static final String AUTH_KEY = "123456";

    @NotNull
    static Map<String, String> createConfiguration(String commitInfo) {
        return Map.of(
                "web.http.port", String.valueOf(getFreePort()),
                "web.http.path", "/api",
                "web.http.data.port", String.valueOf(DATA_PORT),
                "web.http.data.path", "/api/v1/data",
                "edc.api.auth.key", AUTH_KEY,
                "edc.last.commit.info", commitInfo);
    }

    static ValidatableResponse mockRequest() {
        return given()
                .baseUri("http://localhost:" + DATA_PORT)
                .basePath("/api/v1/data")
                .header("x-api-key", AUTH_KEY)
                .when()
                .contentType(ContentType.TEXT)
                .get(String.format("/last-commit-info"))
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }
}
