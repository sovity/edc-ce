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
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.e2e;

import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.extension.Consumer;
import de.sovity.edc.extension.e2e.extension.E2eTestExtension;
import de.sovity.edc.extension.e2e.extension.Provider;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Map;
import java.util.UUID;

import static de.sovity.edc.extension.e2e.extension.Helpers.defaultE2eTestExtension;
import static io.restassured.http.ContentType.JSON;
import static jakarta.json.Json.createObjectBuilder;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.CONTEXT;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;
import static org.eclipse.edc.spi.CoreConstants.EDC_PREFIX;
import static org.hamcrest.Matchers.equalTo;

class DatasetTest {

    @RegisterExtension
    private static E2eTestExtension e2eTestExtension = defaultE2eTestExtension();

    @Test
    void canRetrieveOfferedDataset(
            @Consumer ConnectorRemote consumerConnector,
            @Provider ConnectorConfig providerConfig,
            @Provider ConnectorRemote providerConnector) {
        // arrange
        var assetId = UUID.randomUUID().toString();
        providerConnector.createDataOffer(assetId, "http://example.com");

        // act & assert
        prepareDatasetApiCall(
                consumerConnector,
                providerConfig.getProtocolEndpoint().getUri().toString(),
                assetId)
                .then()
                .statusCode(200)
                .body("'edc:description'", equalTo("description"));
    }

    @Test
    void canNotRetrieveNotOfferedDataset(
            @Consumer ConnectorRemote consumerConnector,
            @Provider ConnectorConfig providerConfig,
            @Provider ConnectorRemote providerConnector) {
        // arrange
        Map<String, Object> dataSource = Map.of(
                EDC_NAMESPACE + "type", "HttpData",
                EDC_NAMESPACE + "baseUrl", "http://localhost");
        var assetId = UUID.randomUUID().toString();
        providerConnector.createAsset(assetId, dataSource);

        // act & assert
        prepareDatasetApiCall(
                consumerConnector,
                providerConfig.getProtocolEndpoint().getUri().toString(),
                assetId)
                .then()
                .statusCode(502);
    }

    @Test
    void canNotRetrieveNotOfferedDatasetIfValidOfferAvailable(
            @Consumer ConnectorRemote consumerConnector,
            @Provider ConnectorConfig providerConfig,
            @Provider ConnectorRemote providerConnector) {
        // arrange
        Map<String, Object> dataSource = Map.of(
                EDC_NAMESPACE + "type", "HttpData",
                EDC_NAMESPACE + "baseUrl", "http://localhost");
        var notOfferedAssetId = UUID.randomUUID().toString();
        var offeredAssetId = UUID.randomUUID().toString();
        providerConnector.createAsset(notOfferedAssetId, dataSource);
        providerConnector.createDataOffer(offeredAssetId, "http://localhost");

        // act & assert
        prepareDatasetApiCall(
                consumerConnector,
                providerConfig.getProtocolEndpoint().getUri().toString(),
                notOfferedAssetId)
                .then()
                .statusCode(502);

        prepareDatasetApiCall(
                consumerConnector,
                providerConfig.getProtocolEndpoint().getUri().toString(),
                offeredAssetId)
                .then()
                .statusCode(200)
                .body("'edc:description'", equalTo("description"));
    }

    private static Response prepareDatasetApiCall(
            ConnectorRemote consumerConnector,
            String providerProtocolUrl,
            String assetId) {
        var requestBody = createObjectBuilder()
                .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
                .add(EDC_NAMESPACE + "counterPartyAddress", providerProtocolUrl)
                .add(EDC_NAMESPACE + "protocol", "dataspace-protocol-http")
                .add("@id", assetId)
                .build();
        return consumerConnector.prepareManagementApiCall()
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post("/v2/catalog/dataset/request");
    }
}
