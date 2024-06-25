/*
 *  Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 */

package de.sovity.edc.extension.contactcancellation.controller;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.extension.db.directaccess.DatabaseDirectAccessExtension;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseViaTestcontainers;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import lombok.val;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.protocol.ProtocolWebhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.Map;

import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static io.restassured.RestAssured.given;
import static jakarta.json.Json.createObjectBuilder;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;
import static org.mockito.Mockito.mock;

class ContractCancellationControllerTest {

    private static final String PROVIDER_PARTICIPANT_ID = "provider";

    @RegisterExtension
    static EdcExtension providerEdcContext = new EdcExtension();
    @RegisterExtension
    static EdcExtension consumerEdcContext = new EdcExtension();

    // TODO: use transactions to save containers boot time
    @RegisterExtension
    static final TestDatabase PROVIDER_DATABASE = new TestDatabaseViaTestcontainers();
    @RegisterExtension
    static final TestDatabase CONSUMER_DATABASE = new TestDatabaseViaTestcontainers();

    private ConnectorRemote providerConnector;
    private ConnectorRemote consumerConnector;

    private ConnectorConfig providerConfig;
    private ConnectorConfig consumerConfig;

    private EdcClient providerClient;
    private EdcClient consumerClient;

    @BeforeEach
    void setup() {
        providerEdcContext.registerServiceMock(ProtocolWebhook.class, mock(ProtocolWebhook.class));

        providerConfig = forTestDatabase(PROVIDER_PARTICIPANT_ID, 21000, PROVIDER_DATABASE);
        providerConfig.setProperty(DatabaseDirectAccessExtension.EDC_SERVER_DB_CONNECTION_TIMEOUT_IN_MS, "1000");
        providerEdcContext.setConfiguration(providerConfig.getProperties());
        providerConnector = new ConnectorRemote(fromConnectorConfig(providerConfig));
        providerClient = EdcClient.builder()
            .managementApiUrl(providerConfig.getManagementEndpoint().getUri().toString())
            .managementApiKey(providerConfig.getProperties().get("edc.api.auth.key"))
            .build();


        consumerEdcContext.registerServiceMock(ProtocolWebhook.class, mock(ProtocolWebhook.class));

        consumerConfig = forTestDatabase(PROVIDER_PARTICIPANT_ID, 23000, PROVIDER_DATABASE);
        consumerConfig.setProperty(DatabaseDirectAccessExtension.EDC_SERVER_DB_CONNECTION_TIMEOUT_IN_MS, "1000");
        consumerEdcContext.setConfiguration(consumerConfig.getProperties());
        consumerConnector = new ConnectorRemote(fromConnectorConfig(consumerConfig));
        consumerClient = EdcClient.builder()
            .managementApiUrl(consumerConfig.getManagementEndpoint().getUri().toString())
            .managementApiKey(consumerConfig.getProperties().get("edc.api.auth.key")) // TODO: add get API auth key in the config class
            .build();
    }

    @Test
    void cancel_whenNoCancellationExists_shouldReturn200AndCancelContract() {

        val assetId = createAsset(providerClient);
        val policy = createObjectBuilder().add(TYPE, "use").build();
        val noPolicyId = providerConnector.createPolicy(policy);
        providerConnector.createContractDefinition(assetId, "contractDefinition1", noPolicyId, noPolicyId);

        val counterPartyAddress = providerConfig.getProtocolEndpoint().getUri().toString();
        consumerClient.uiApi().initiateContractNegotiation(ContractNegotiationRequest.builder()
                .contractOfferId("contractDefinition1")
                .assetId(assetId)
                .counterPartyAddress(counterPartyAddress)
                .policyJsonLd(JsonUtils.toJson(policy))
            .build());

        // TODO PSQLException: ERROR: relation "edc_transfer_process" does not exist
        //  Is this related to EDC extensions loading order?
        //  How to change that?

        // act
        given()
            // TODO: add validators
            .body("""
                {
                    "contractId" : "contract-1",
                    "reason" : "some reason 1",
                    "detail" : "some detail 1"
                }""")
            // TODO: is sovity the best place? Any precedent?
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .post(providerConfig.getManagementEndpoint().getUri() + "/sovity/contract/cancel")
            .then()

            // assert
            .statusCode(Response.Status.OK.getStatusCode());

    }

    private String createAsset(EdcClient client) {
        // TODO: remove unnecessary parts
        return client.uiApi().createAsset(UiAssetCreateRequest.builder()
            .id("asset-1")
            .title("AssetName")
            .description("AssetDescription")
            .licenseUrl("https://license-url")
            .version("1.0.0")
            .language("en")
            .mediaType("application/json")
            .dataCategory("dataCategory")
            .dataSubcategory("dataSubcategory")
            .dataModel("dataModel")
            .geoReferenceMethod("geoReferenceMethod")
            .transportMode("transportMode")
            .keywords(List.of("keyword1", "keyword2"))
            .publisherHomepage("publisherHomepage")
            .dataAddressProperties(Map.of(
                Prop.Edc.TYPE, "HttpData",
                Prop.Edc.METHOD, "GET",
                Prop.Edc.BASE_URL, "http://some.url"
            ))
            .customJsonAsString("{\"a\":\"x\"}")
            .customJsonLdAsString("{\"http://unknown/b\":{\"http://unknown/c\":\"y\"}}")
            .privateCustomJsonAsString("{\"a-private\":\"x-private\"}")
            .privateCustomJsonLdAsString("{\"http://unknown/b-private\":{\"http://unknown/c-private\":\"y-private\"}}")
            .build()).getId();
    }

    // TODO: don't double cancel a contract
}
