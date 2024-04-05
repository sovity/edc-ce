/*
 * Copyright (c) 2024 sovity GmbH
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
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationSimplifiedState;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.client.gen.model.TransferHistoryEntry;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiContractNegotiation;
import de.sovity.edc.client.gen.model.UiContractOffer;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.client.gen.model.UiDataOffer;
import de.sovity.edc.client.gen.model.UiPolicyCreateRequest;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.MockDataAddressRemote;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseFactory;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.val;
import org.awaitility.Awaitility;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.sovity.edc.client.gen.model.TransferProcessSimplifiedState.OK;
import static de.sovity.edc.client.gen.model.TransferProcessSimplifiedState.RUNNING;
import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtil.validateDataTransferred;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;

class DataSourceMethodParamTest {

    private static final String PROVIDER_PARTICIPANT_ID = "provider";
    private static final String CONSUMER_PARTICIPANT_ID = "consumer";

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

    private EdcClient providerClient;
    private EdcClient consumerClient;
    private MockDataAddressRemote dataAddress;
    private final String dataOfferId = "my-data-offer-2023-11";

    @BeforeEach
    void setup() {
        // set up provider EDC + Client
        var providerConfig = forTestDatabase(PROVIDER_PARTICIPANT_ID, 21000, PROVIDER_DATABASE);
        providerEdcContext.setConfiguration(providerConfig.getProperties());
        providerConnector = new ConnectorRemote(fromConnectorConfig(providerConfig));

        providerClient = EdcClient.builder()
                .managementApiUrl(providerConfig.getManagementEndpoint().getUri().toString())
                .managementApiKey(providerConfig.getProperties().get("edc.api.auth.key"))
                .build();

        // set up consumer EDC + Client
        var consumerConfig = forTestDatabase(CONSUMER_PARTICIPANT_ID, 23000, CONSUMER_DATABASE);
        consumerEdcContext.setConfiguration(consumerConfig.getProperties());
        consumerConnector = new ConnectorRemote(fromConnectorConfig(consumerConfig));

        consumerClient = EdcClient.builder()
                .managementApiUrl(consumerConfig.getManagementEndpoint().getUri().toString())
                .managementApiKey(consumerConfig.getProperties().get("edc.api.auth.key"))
                .build();

        // We use the provider EDC as data sink / data source (it has the test-backend-controller extension)
        dataAddress = new MockDataAddressRemote(providerConnector.getConfig().getDefaultEndpoint());
    }

    @Test
    void canTransferMethodParameterizedAssetWithGetMethod() {
        // arrange
        createPolicy();
        val assetId = createAssetWithParamedMethod();
        createContractDefinition();

        // act
        var dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(getProtocolEndpoint(providerConnector));
        var negotiation = initiateNegotiation(dataOffers.get(0), dataOffers.get(0).getContractOffers().get(0));
        negotiation = awaitNegotiationDone(negotiation.getContractNegotiationId());
        val transferId = initiateTransfer(negotiation, "GET");

        Awaitility.await().atMost(consumerConnector.timeout).until(
                () -> consumerClient.uiApi()
                        .getTransferHistoryPage()
                        .getTransferEntries()
                        .stream()
                        .filter(it -> it.getTransferProcessId().equals(transferId))
                        .findFirst()
                        .map(it -> it.getState().getSimplifiedState()),
                it -> it.orElse(RUNNING) != RUNNING
        );

        // assert
        TransferHistoryEntry actual = consumerClient.uiApi().getTransferHistoryPage().getTransferEntries().get(0);
        assertThat(actual.getAssetId()).isEqualTo(assetId);
        assertThat(actual.getTransferProcessId()).isEqualTo(transferId);
        assertThat(actual.getState().getSimplifiedState()).isEqualTo(OK);

        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), "some content");
    }

    @Test
    void canTransferMethodParameterizedAssetWithOptions() {
        // arrange
        createPolicy();
        val assetId = createAssetWithParamedMethod();
        createContractDefinition();

        // act
        var dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(getProtocolEndpoint(providerConnector));
        var negotiation = initiateNegotiation(dataOffers.get(0), dataOffers.get(0).getContractOffers().get(0));
        negotiation = awaitNegotiationDone(negotiation.getContractNegotiationId());
        val transferId = initiateTransfer(negotiation, "OPTIONS");

        Awaitility.await().atMost(consumerConnector.timeout).until(
                () -> consumerClient.uiApi()
                        .getTransferHistoryPage()
                        .getTransferEntries()
                        .stream()
                        .filter(it -> it.getTransferProcessId().equals(transferId))
                        .findFirst()
                        .map(it -> it.getState().getSimplifiedState()),
                it -> it.orElse(RUNNING) != RUNNING
        );

        // assert
        TransferHistoryEntry actual = consumerClient.uiApi().getTransferHistoryPage().getTransferEntries().get(0);
        assertThat(actual.getAssetId()).isEqualTo(assetId);
        assertThat(actual.getTransferProcessId()).isEqualTo(transferId);
        assertThat(actual.getState().getSimplifiedState()).isEqualTo(OK);

        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), "some content");
    }

    private String createAssetWithParamedMethod() {
        var asset = UiAssetCreateRequest.builder()
                .id(dataOfferId)
                .title("My Data Offer")
                .dataAddressProperties(Map.of(
                        Prop.Edc.TYPE, "HttpData",
                        Prop.Edc.PROXY_METHOD, "true",
                        Prop.Edc.BASE_URL, "https://webhook.site/7eb84f91-1264-4dd1-b5b5-2ed173d64bc2"
                ))
                .customJsonLdAsString("""
                        {
                            "https://w3id.org/edc/v0.0.1/ns/type": "HttpData",
                            "https://w3id.org/edc/v0.0.1/ns/baseUrl": "https://app.mydepartment.myorg.com/api",
                            "https://w3id.org/edc/v0.0.1/ns/proxyMethod": "true"
                        }
                        """)
                .build();

        return providerClient.uiApi().createAsset(asset).getId();
    }

    private void createPolicy() {
        var policyDefinition = PolicyDefinitionCreateRequest.builder()
                .policyDefinitionId(dataOfferId)
                .policy(UiPolicyCreateRequest.builder()
                        .constraints(List.of())
                        .build())
                .build();

        providerClient.uiApi().createPolicyDefinition(policyDefinition);
    }

    private void createContractDefinition() {
        var contractDefinition = ContractDefinitionRequest.builder()
                .contractDefinitionId(dataOfferId)
                .accessPolicyId(dataOfferId)
                .contractPolicyId(dataOfferId)
                .assetSelector(List.of(UiCriterion.builder()
                        .operandLeft(Prop.Edc.ID)
                        .operator(UiCriterionOperator.EQ)
                        .operandRight(UiCriterionLiteral.builder()
                                .type(UiCriterionLiteralType.VALUE)
                                .value(dataOfferId)
                                .build())
                        .build()))
                .build();

        providerClient.uiApi().createContractDefinition(contractDefinition);
    }

    private UiContractNegotiation initiateNegotiation(UiDataOffer dataOffer, UiContractOffer contractOffer) {
        var negotiationRequest = ContractNegotiationRequest.builder()
                .counterPartyAddress(dataOffer.getEndpoint())
                .counterPartyParticipantId(dataOffer.getParticipantId())
                .assetId(dataOffer.getAsset().getAssetId())
                .contractOfferId(contractOffer.getContractOfferId())
                .policyJsonLd(contractOffer.getPolicy().getPolicyJsonLd())
                .build();

        return consumerClient.uiApi().initiateContractNegotiation(negotiationRequest);
    }

    private UiContractNegotiation awaitNegotiationDone(String negotiationId) {
        var negotiation = Awaitility.await().atMost(consumerConnector.timeout).until(
                () -> consumerClient.uiApi().getContractNegotiation(negotiationId),
                it -> it.getState().getSimplifiedState() != ContractNegotiationSimplifiedState.IN_PROGRESS
        );

        assertThat(negotiation.getState().getSimplifiedState()).isEqualTo(ContractNegotiationSimplifiedState.AGREED);
        return negotiation;
    }

    private String initiateTransfer(UiContractNegotiation negotiation, String httpMethod) {
        /*
        {
          "@type": "https://w3id.org/edc/v0.0.1/ns/TransferRequest",
          "https://w3id.org/edc/v0.0.1/ns/assetId": "{{ASSET_ID}}",
          "https://w3id.org/edc/v0.0.1/ns/contractId": "{{CONTRACT_ID}}",
          "https://w3id.org/edc/v0.0.1/ns/connectorAddress": "https://{{PROVIDER_EDC_FQDN}}/api/dsp",
          "https://w3id.org/edc/v0.0.1/ns/connectorId": "{{PROVIDER_EDC_PARTICIPANT_ID}}",
          "https://w3id.org/edc/v0.0.1/ns/dataDestination": {
            "https://w3id.org/edc/v0.0.1/ns/type": "HttpData",
            "https://w3id.org/edc/v0.0.1/ns/baseUrl": "{{target-url}}"
          },
          "https://w3id.org/edc/v0.0.1/ns/privateProperties": {
            "https://w3id.org/edc/v0.0.1/ns/pathSegments": "my-endpoint",
            "https://w3id.org/edc/v0.0.1/ns/method": "POST",
            "https://w3id.org/edc/v0.0.1/ns/queryParams": "filter=abc&limit=10",
            "https://w3id.org/edc/v0.0.1/ns/contentType": "application/json",
            "https://w3id.org/edc/v0.0.1/ns/body": "{\"myBody\": \"myValue\"}"
          },
          "https://w3id.org/edc/v0.0.1/ns/protocol": "dataspace-protocol-http",
          "https://w3id.org/edc/v0.0.1/ns/managedResources": false
        }
         */
        var contractAgreementId = negotiation.getContractAgreementId();
        Map<String, String> dataSinkProperties = new HashMap<>(dataAddress.getDataSinkProperties());
        dataSinkProperties.put(EDC_NAMESPACE + "baseUrl", "https://webhook.site/7eb84f91-1264-4dd1-b5b5-2ed173d64bc2");
        dataSinkProperties.put("https://w3id.org/edc/v0.0.1/ns/method", httpMethod);

        var transferRequest = InitiateTransferRequest.builder()
                .contractAgreementId(contractAgreementId)
                .dataSinkProperties(dataSinkProperties)
                .transferProcessProperties(
                        Map.of(
                                "https://w3id.org/edc/v0.0.1/ns/contentType", "application/json",
                                "https://w3id.org/edc/v0.0.1/ns/method", httpMethod
                        )
                )
                .build();
        return consumerClient.uiApi().initiateTransfer(transferRequest).getId();
    }

    private String getProtocolEndpoint(ConnectorRemote connector) {
        return connector.getConfig().getProtocolEndpoint().getUri().toString();
    }
}
