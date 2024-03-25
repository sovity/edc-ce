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
import org.eclipse.edc.connector.transfer.spi.types.TransferProcessStates;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtil.validateDataTransferred;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class DataSourceVerbParamTest {

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
    private final String encodedParam = "a=%25"; // Unencoded param "a=%"
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

    /*

    curl 'http://localhost:11002/api/management/wrapper/ui/pages/contract-agreement-page/transfers' -X POST -H 'User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:124.0) Gecko/20100101 Firefox/124.0' -H 'Accept-Language: en-US,en;q=0.5' -H 'Accept-Encoding: gzip, deflate, br' -H 'Referer: http://localhost:11000/' -H 'X-Api-Key: ApiKeyDefaultValue' -H 'Content-Type: application/json' -H 'Origin: http://localhost:11000' -H 'Connection: keep-alive' -H 'Sec-Fetch-Dest: empty' -H 'Sec-Fetch-Mode: cors' -H 'Sec-Fetch-Site: same-site' --data-raw '{"contractAgreementId":"cGFyYW1fc291cmNlX2NvbnRyYWN0:YXNzZXRfMjJrX3BhcmFtX3NvdXJjZQ==:MTZhMGI3YjUtYmRmYS00ZDAyLTlkMjUtZDYyOWViMmM5OWI2","dataSinkProperties":{"https://w3id.org/edc/v0.0.1/ns/type":"HttpData","https://w3id.org/edc/v0.0.1/ns/baseUrl":"http://destination.com","https://w3id.org/edc/v0.0.1/ns/method":"POST"},"transferProcessProperties":{"https://w3id.org/edc/v0.0.1/ns/method":"PATCH"}}'

     */

    /**
     * This test will fail as soon as the handling of query parameters is fixed in the EDC project
     */
    @Test
    void canSendTheHttpMethodToTheProviderEdc() throws InterruptedException {
        // arrange
        createPolicy();
        createAsset();
        createContractDefinition();

        // act
        var dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(getProtocolEndpoint(providerConnector));
        var negotiation = initiateNegotiation(dataOffers.get(0), dataOffers.get(0).getContractOffers().get(0));
        negotiation = awaitNegotiationDone(negotiation.getContractNegotiationId());


        var contractAgreementId = negotiation.getContractAgreementId();
        var transferRequest = InitiateTransferRequest.builder()
                .contractAgreementId(contractAgreementId)
                .dataSinkProperties(
                        // TODO: builder
                        Map.of(
                                // TODO HTTP data VS HTTP proxy
                                // same result
                                "https://w3id.org/edc/v0.0.1/ns/type", "HttpProxy",
                                "https://w3id.org/edc/v0.0.1/ns/baseUrl", "http://destination.com",
                                "https://w3id.org/edc/v0.0.1/ns/method", "POST"
                        )
                )
                .transferProcessProperties(
                        Map.of(
                                "https://w3id.org/edc/v0.0.1/ns/method", "PATCH"
                        )
                )
                .build();

        // TODO: must be able to do a pull transfer
        val response = consumerClient.uiApi().initiateTransfer(transferRequest);
        val id = response.getId();

        var x = consumerClient.uiApi().getTransferHistoryPage();
        while(x.getTransferEntries().get(0).getState().getCode() != TransferProcessStates.COMPLETED.code()) {
            System.out.println(x.getTransferEntries().get(0).getState());
            Thread.sleep(1000);
            x = consumerClient.uiApi().getTransferHistoryPage();
        }

        // assert
        // TODO: how to validate that the provider received the correct message?
        //  - use listener?
        //  - use extension?
        //  - use web API!
    }

    private void createAsset() {
        var asset = UiAssetCreateRequest.builder()
                .id(dataOfferId)
                .title("My Data Offer")
                .dataAddressProperties(Map.of(
                        Prop.Edc.TYPE, "HttpData",
                        Prop.Edc.PROXY_METHOD, "true",
                        Prop.Edc.BASE_URL, dataAddress.getDataSourceQueryParamsUrl()
                ))
                .build();

        providerClient.uiApi().createAsset(asset);
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

    private void initiateTransfer(UiContractNegotiation negotiation) {
        var contractAgreementId = negotiation.getContractAgreementId();
        var transferRequest = InitiateTransferRequest.builder()
                .contractAgreementId(contractAgreementId)
                .dataSinkProperties(dataAddress.getDataSinkProperties())
                .build();
        consumerClient.uiApi().initiateTransfer(transferRequest);
    }

    private String getProtocolEndpoint(ConnectorRemote connector) {
        return connector.getConfig().getProtocolEndpoint().getUri().toString();
    }
}
