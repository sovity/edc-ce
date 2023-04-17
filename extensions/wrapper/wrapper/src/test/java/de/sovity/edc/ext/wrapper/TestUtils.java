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

package de.sovity.edc.ext.wrapper;

import io.restassured.specification.RequestSpecification;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;

public class TestUtils {

    private static final int DATA_PORT = getFreePort();
    private static final String AUTH_KEY = "123456";
    public static final String IDS_ENDPOINT = "http://localhost:" + DATA_PORT + "/api/v1/data/ids";

    @NotNull
    public static Map<String, String> createConfiguration() {
        return Map.of(
                "web.http.port", String.valueOf(getFreePort()),
                "web.http.path", "/api",
                "web.http.management.port", String.valueOf(DATA_PORT),
                "web.http.management.path", "/api/v1/data",
                "edc.api.auth.key", AUTH_KEY,
                "edc.ids.endpoint", IDS_ENDPOINT);
    }

    public static RequestSpecification givenManagementEndpoint() {
        return given()
                .baseUri("http://localhost:" + DATA_PORT)
                .basePath("/api/v1/data")
                .header("x-api-key", AUTH_KEY);
    }
}
