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

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;

public class TestUtils {

    private static final int DATA_PORT = getFreePort();
    private static final String API_KEY = "123456";

    @NotNull
    static Map<String, String> createConfiguration(Map<String, String> additionalProps) {
        Map<String, String> props = new HashMap<>();
        props.put("web.http.port", String.valueOf(getFreePort()));
        props.put("web.http.path", "/api");
        props.put("web.http.management.port", String.valueOf(DATA_PORT));
        props.put("web.http.management.path", "/api/v1/data");
        props.put("edc.api.auth.key", API_KEY);
        props.putAll(additionalProps);
        return props;
    }

    static ValidatableResponse mockRequest() {
        return given()
                .baseUri("http://localhost:" + DATA_PORT)
                .basePath("/api/v1/data")
                .header("X-Api-Key", API_KEY)
                .when()
                .contentType(ContentType.TEXT)
                .get("/edc-ui-config")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }
}
