/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.extension.e2e.connector;

import jakarta.json.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static jakarta.json.Json.createObjectBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;

@SuppressWarnings("java:S5960")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataTransferTestUtil {

    public static final Duration TIMEOUT = Duration.ofSeconds(30);

    public static JsonObject buildDataAddressJsonLd(String baseUrl, String method) {
        return createObjectBuilder()
                .add(TYPE, EDC_NAMESPACE + "DataAddress")
                .add(EDC_NAMESPACE + "type", "HttpData")
                .add(EDC_NAMESPACE + "properties", createObjectBuilder()
                        .add(EDC_NAMESPACE + "baseUrl", baseUrl)
                        .add(EDC_NAMESPACE + "method", method)
                        .build())
                .build();
    }

    public static Map<String, String> buildDataAddressProperties(String baseUrl, String method) {
        return Map.of(
                EDC_NAMESPACE + "type", "HttpData",
                EDC_NAMESPACE + "baseUrl", baseUrl,
                EDC_NAMESPACE + "method", method
        );
    }


    public static void validateDataTransferred(String checkUrl, String expectedData) {
        await().atMost(TIMEOUT).untilAsserted(() -> {
            var actual =
                    when()
                            .get(checkUrl)
                            .then()
                            .statusCode(200)
                            .extract().body().asString();
            assertThat(actual).isEqualTo(expectedData);
        });
    }

    public static void validateDataTransferred(String checkUrl, Map<String, String> params, String expected) {
        await().atMost(TIMEOUT).untilAsserted(() -> {
            var actual =
                    given().params(params).when()
                            .get(checkUrl)
                            .then()
                            .statusCode(200)
                            .extract().body().asString();
            assertThat(actual).isEqualTo(expected);
        });
    }
}
