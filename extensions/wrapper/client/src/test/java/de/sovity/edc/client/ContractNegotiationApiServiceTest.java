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
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.client;

import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.MockDataAddressRemote;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.awaitility.Awaitility;
import org.eclipse.edc.jsonld.TitaniumJsonLd;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtil.validateDataTransferred;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.basicEdcConfig;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static io.restassured.http.ContentType.JSON;
import static jakarta.json.Json.createObjectBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates.FINALIZED;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.CONTEXT;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;
import static org.eclipse.edc.jsonld.spi.PropertyAndTypeNames.ODRL_POLICY_ATTRIBUTE;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;
import static org.eclipse.edc.spi.CoreConstants.EDC_PREFIX;

@ApiTest
@ExtendWith(EdcExtension.class)
class ContractNegotiationApiServiceTest {


    private static final String PROVIDER_PARTICIPANT_ID = "provider";
    private static final String CONSUMER_PARTICIPANT_ID = "consumer";
    private static final String TEST_BACKEND_TEST_DATA = UUID.randomUUID().toString();

    @RegisterExtension
    static EdcExtension providerEdcContext = new EdcExtension();
    @RegisterExtension
    static EdcExtension consumerEdcContext = new EdcExtension();

    private ConnectorRemote providerConnector;
    private ConnectorRemote consumerConnector;
    private MockDataAddressRemote dataAddress;

    private EdcClient consumerClient;
    private final JsonLd jsonLd = new TitaniumJsonLd(new ConsoleMonitor());

    @BeforeEach
    void setup() {
        var providerConfig = basicEdcConfig(PROVIDER_PARTICIPANT_ID, 24000);
        providerEdcContext.setConfiguration(providerConfig.getProperties());
        providerConnector = new ConnectorRemote(fromConnectorConfig(providerConfig));

        var consumerConfig = basicEdcConfig(CONSUMER_PARTICIPANT_ID, 25000);
        consumerEdcContext.setConfiguration(consumerConfig.getProperties());
        consumerConnector = new ConnectorRemote(fromConnectorConfig(consumerConfig));

        consumerClient = EdcClient.builder()
                .managementApiUrl(consumerConnector.getConfig().getManagementEndpoint().getUri().toString())
                .managementApiKey(consumerConfig.getProperties().get("edc.api.auth.key"))
                .build();

        // We use the provider EDC as data sink / data source (it has the test-backend-controller extension)
        dataAddress = new MockDataAddressRemote(providerConnector.getConfig().getDefaultEndpoint());
    }

    @Test
    void testContractNegotiation__usingManagementApiOnly__todo_remove() {
        // arrange
        var assetId = UUID.randomUUID().toString();
        providerConnector.createDataOffer(assetId, dataAddress.getDataSourceUrl(TEST_BACKEND_TEST_DATA));
        String providerId = providerConnector.getParticipantId();
        URI providerProtocolApi = providerConnector.getConfig().getProtocolEndpoint().getUri();
        JsonObject destination = dataAddress.getDataSinkJsonLd();
        var dataset = consumerConnector.getDatasetForAsset(assetId, providerProtocolApi);
        var contractId = consumerConnector.getDatasetContractId(dataset);

        var policyJsonLd = createObjectBuilder()
                .add(CONTEXT, "https://www.w3.org/ns/odrl.jsonld")
                .add(TYPE, "use")
                .build()
                .toString();

        // act
        var negotiationId = consumerConnector.prepareManagementApiCall()
                .contentType(JSON)
                .body(createObjectBuilder()
                        .add(CONTEXT, createObjectBuilder().add(EDC_PREFIX, EDC_NAMESPACE))
                        .add(TYPE, "NegotiationInitiateRequestDto")
                        .add("connectorId", providerId)
                        .add("consumerId", consumerConnector.getParticipantId())
                        .add("providerId", providerId)
                        .add("connectorAddress", providerProtocolApi.toString())
                        .add("protocol", "dataspace-protocol-http")
                        .add("offer", createObjectBuilder()
                                .add("offerId", contractId.toString())
                                .add("assetId", contractId.assetIdPart())
                                .add("policy", policyJsonLd.toString())
                        )
                        .build())
                .when()
                .post("/v2/contractnegotiations")
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getString(ID);

        Awaitility.await().atMost(Duration.ofSeconds(60)).untilAsserted(() -> {
            var state = consumerConnector.prepareManagementApiCall()
                    .contentType(JSON)
                    .when()
                    .get("/v2/contractnegotiations/{id}/state", negotiationId)
                    .then()
                    .statusCode(200)
                    .extract().body().jsonPath().getString("'edc:state'");
            assertThat(state).isEqualTo(FINALIZED.name());
        });

        var contractAgreementId = consumerConnector.getContractAgreementId(negotiationId);

        // assert
        var transferProcessId = consumerConnector.initiateTransfer(
                contractAgreementId,
                assetId,
                providerProtocolApi,
                destination);

        assertThat(transferProcessId).isNotNull();
        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), TEST_BACKEND_TEST_DATA);
    }

    @Test
    void testContractNegotiation() {
        // arrange
        var assetId = UUID.randomUUID().toString();
        providerConnector.createDataOffer(assetId, dataAddress.getDataSourceUrl(TEST_BACKEND_TEST_DATA));
        String providerId = providerConnector.getParticipantId();
        URI providerProtocolApi = providerConnector.getConfig().getProtocolEndpoint().getUri();
        JsonObject destination = dataAddress.getDataSinkJsonLd();
        var dataset = consumerConnector.getDatasetForAsset(assetId, providerProtocolApi);
        var contractId = consumerConnector.getDatasetContractId(dataset);
        var policyJsonLd = createObjectBuilder()
                .add(CONTEXT, "https://www.w3.org/ns/odrl.jsonld")
                .add(TYPE, Json.createObjectBuilder()
                        .add("@policytype", "set")
                        .build())
                .build()
                .toString();

        var contractNegotiationRequest = ContractNegotiationRequest.builder()
                .protocol("dataspace-protocol-http")
                .counterPartyAddress(providerProtocolApi.toString())
                .contractOfferId(contractId.toString())
                .assetId(contractId.assetIdPart())
                .policyJsonLd(policyJsonLd)
                .build();

        // act

        var contractNegotiationDto = consumerClient.uiApi().initiateContractNegotiation(contractNegotiationRequest);

        Awaitility.await().atMost(Duration.ofSeconds(60)).untilAsserted(() -> {
            var state = consumerConnector.prepareManagementApiCall()
                    .contentType(JSON)
                    .when()
                    .get("/v2/contractnegotiations/{id}/state", contractNegotiationDto.getContractNegotiationId())
                    .then()
                    .statusCode(200)
                    .extract().body().jsonPath().getString("'edc:state'");
            assertThat(state).isEqualTo(FINALIZED.name());
        });


        var existingContractNegotiationDto = consumerClient.uiApi().getContractNegotiation(contractNegotiationDto.getContractNegotiationId());
        var state = existingContractNegotiationDto.getStatus();


        // assert
        assertThat(state).isEqualTo(FINALIZED.name());
        var transferProcessId = consumerConnector.initiateTransfer(
                contractNegotiationDto.getContractAgreementId(),
                assetId,
                providerProtocolApi,
                destination);

        assertThat(transferProcessId).isNotNull();
        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), TEST_BACKEND_TEST_DATA);
    }
}
