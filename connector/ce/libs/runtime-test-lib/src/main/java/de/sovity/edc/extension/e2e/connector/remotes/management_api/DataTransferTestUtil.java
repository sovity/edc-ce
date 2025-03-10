/*
 * Copyright 2023 Fraunhofer-Institut für Software- und Systemtechnik ISST
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     Fraunhofer ISST - contributions to the Eclipse EDC 0.2.0 migration
 *     sovity - continued development
 */
package de.sovity.edc.extension.e2e.connector.remotes.management_api;

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
import static org.eclipse.edc.spi.constants.CoreConstants.EDC_NAMESPACE;

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
