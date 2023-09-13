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

package de.sovity.edc.e2e;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.MockDataAddressRemote;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseFactory;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URI;
import java.util.UUID;

import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtil.validateDataTransferred;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static io.restassured.http.ContentType.JSON;
import static jakarta.json.Json.createObjectBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates.FINALIZED;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.CONTEXT;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;
import static org.eclipse.edc.jsonld.spi.PropertyAndTypeNames.ODRL_POLICY_ATTRIBUTE;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;
import static org.eclipse.edc.spi.CoreConstants.EDC_PREFIX;

class ApiWrapperTest {

    private static final String PROVIDER_PARTICIPANT_ID = "provider";
    private static final String CONSUMER_PARTICIPANT_ID = "consumer";
    private static final String TEST_BACKEND_TEST_DATA = UUID.randomUUID().toString();

    @RegisterExtension
    static EdcExtension providerEdcContext = new EdcExtension();
    @RegisterExtension
    static EdcExtension consumerEdcContext = new EdcExtension();

    @RegisterExtension
    static final TestDatabase PROVIDER_DATABASE = TestDatabaseFactory.getTestDatabase(1);
    @RegisterExtension
    static final TestDatabase CONSUMER_DATABASE = TestDatabaseFactory.getTestDatabase(2);

    private ConnectorRemote providerConnector;
    private ConnectorRemote consumerConnector;

    private EdcClient consumerClient;
    private MockDataAddressRemote dataAddress;

    @BeforeEach
    void setup() {
        var providerConfig = forTestDatabase(PROVIDER_PARTICIPANT_ID, 22000, PROVIDER_DATABASE);
        providerEdcContext.setConfiguration(providerConfig.getProperties());
        providerConnector = new ConnectorRemote(fromConnectorConfig(providerConfig));

        var consumerConfig = forTestDatabase(CONSUMER_PARTICIPANT_ID, 23000, CONSUMER_DATABASE);
        consumerEdcContext.setConfiguration(consumerConfig.getProperties());
        consumerConnector = new ConnectorRemote(fromConnectorConfig(consumerConfig));

        consumerClient = EdcClient.builder()
                .managementApiUrl(consumerConfig.getManagementEndpoint().toString())
                .managementApiKey(consumerConfig.getProperties().get("edc.api.auth.key"))
                .build();

        // We use the provider EDC as data sink / data source (it has the test-backend-controller extension)
        dataAddress = new MockDataAddressRemote(providerConnector.getConfig().getDefaultEndpoint());
    }
    @Test
    void testDataTransfer_freshDataOffer__negotiationViaUiApi() {
        // arrange
        var providerEndpoint = providerConnector.getConfig().getManagementEndpoint().toString();
        var data = "expected data 123";
        var assetId = UUID.randomUUID().toString();
        providerConnector.createDataOffer(assetId, dataAddress.getDataSourceUrl(data));

        // act
        var catalog = consumerClient.uiApi().catalogPageDataOffers(providerEndpoint);
        var negotiation = consumerClient.uiApi().initiate^
        String providerId = providerConnector.getParticipantId();
        URI providerProtocolApi = providerConnector.getConfig().getProtocolEndpoint().getUri();
        JsonObject destination = dataAddress.getDataSinkJsonLd();
        var dataset = consumerConnector.getDatasetForAsset(assetId, providerProtocolApi);
        var contractId = consumerConnector.getDatasetContractId(dataset);
        var policy = dataset.getJsonArray(ODRL_POLICY_ATTRIBUTE).get(0).asJsonObject();

        var requestBody = createObjectBuilder()
                .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
                .add(TYPE, "NegotiationInitiateRequestDto")
                .add("connectorId", providerId)
                .add("consumerId", consumerConnector.config.getParticipantId())
                .add("providerId", providerId)
                .add("connectorAddress", providerProtocolApi.toString())
                .add("protocol", "dataspace-protocol-http")
                .add("offer", createObjectBuilder()
                        .add("offerId", contractId.toString())
                        .add("assetId", contractId.assetIdPart())
                        .add("policy", consumerConnector.jsonLd.compact(policy).getContent())
                )
                .build();

        var negotiationId = consumerConnector.prepareManagementApiCall()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/v2/contractnegotiations")
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getString(ID);

        Awaitility.await().atMost(consumerConnector.timeout).untilAsserted(() -> {
            var state = consumerConnector.getContractNegotiationState(negotiationId);
            assertThat(state).isEqualTo(FINALIZED.name());
        });

        var contractAgreementId = consumerConnector.getContractAgreementId(negotiationId);

        var requestBody1 = createObjectBuilder()
                .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
                .add(TYPE, "TransferRequestDto")
                .add("dataDestination", destination)
                .add("protocol", "dataspace-protocol-http")
                .add("managedResources", false)
                .add("assetId", assetId)
                .add("contractId", contractAgreementId)
                .add("connectorAddress", providerProtocolApi.toString())
                .add("privateProperties", Json.createObjectBuilder().build())
                .add("connectorId", consumerConnector.config.getParticipantId())
                .build();

        var transferProcessId = consumerConnector.prepareManagementApiCall()
                .contentType(ContentType.JSON)
                .body(requestBody1)
                .when()
                .post("/v2/transferprocesses")
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getString(ID);

        assertThat(transferProcessId).isNotNull();

        // assert
        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), data);
    }
}
