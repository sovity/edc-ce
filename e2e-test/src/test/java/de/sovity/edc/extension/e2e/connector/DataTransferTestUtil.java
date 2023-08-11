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
import org.assertj.core.api.Assertions;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.when;
import static jakarta.json.Json.createObjectBuilder;
import static org.awaitility.Awaitility.await;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.CONTEXT;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;

public class DataTransferTestUtil {

    public static final String MIGRATED_M8_ASSET_ID = "test-1.0";
    public static final Duration TIMEOUT = Duration.ofSeconds(60);

    private DataTransferTestUtil() {
    }

    public static void createTestOffer(
            ConnectorRemote connector,
            String assetId,
            String targetUrl) {
        var contractDefinitionId = UUID.randomUUID().toString();
        connector.createAsset(assetId, httpDataAddressProperties(targetUrl));
        var noConstraintPolicyId = connector.createPolicy(noConstraintPolicy());
        connector.createContractDefinition(
                assetId,
                contractDefinitionId,
                noConstraintPolicyId,
                noConstraintPolicyId);
    }

    private static JsonObject noConstraintPolicy() {
        return createObjectBuilder()
                .add(CONTEXT, "https://www.w3.org/ns/odrl.jsonld")
                .add(TYPE, "use")
                .build();
    }

    private static Map<String, Object> httpDataAddressProperties(String targetUrl) {
        return Map.of(
                "name", "transfer-test",
                "baseUrl", targetUrl,
                "type", "HttpData",
                "proxyQueryParams", "true"
        );
    }

    public static void validateDataTransferred(String checkUrl, String expectedData) {
        await().atMost(TIMEOUT).untilAsserted(() -> {
            Assertions.assertThat(
                            when()
                                    .get(checkUrl)
                                    .then()
                                    .statusCode(200)
                                    .extract().body().asString())
                    .isEqualTo(expectedData);
        });
    }
}
