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

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreement;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.InitiateCustomTransferRequest;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.ext.wrapper.TestUtils;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.protocol.dsp.spi.types.HttpMessageProtocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class ContractAgreementTransferApiServiceTest {

    private static final String DATA_SINK = "http://my-data-sink/api/stuff";
    private static final String COUNTER_PARTY_ADDRESS =
            "http://some-other-connector/api/v1/ids/data";

    EdcClient client;

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
        client = TestUtils.edcClient();
    }

    @Test
    void startTransferProcessForAgreementId(
            ContractNegotiationStore store,
            TransferProcessStore transferProcessStore
    ) {
        // arrange
        var contractId = UUID.randomUUID().toString();
        createContractNegotiation(store, COUNTER_PARTY_ADDRESS, contractId);

        var request = new InitiateTransferRequest(
                contractId,
                Map.of(
                        "type", "HttpData",
                        "baseUrl", DATA_SINK
                ),
                Map.of("privateProperty", "privateValue")
        );

        // act
        var result = client.uiApi().initiateTransfer(request);

        // then
        var transferProcess = transferProcessStore.findById(result.getId());
        assertThat(transferProcess).isNotNull();
        assertThat(transferProcess.getPrivateProperties()).containsAllEntriesOf(Map.of(
                "privateProperty", "privateValue"
        ));

        var dataRequest = transferProcess.getDataRequest();
        assertThat(dataRequest.getContractId()).isEqualTo(contractId);
        assertThat(dataRequest.getConnectorAddress()).isEqualTo(COUNTER_PARTY_ADDRESS);
        assertThat(dataRequest.getDataDestination().getProperties()).containsAllEntriesOf(Map.of(
                "https://w3id.org/edc/v0.0.1/ns/type", "HttpData",
                "baseUrl", DATA_SINK
        ));
    }

    @Test
    void startCustomTransferProcessForAgreementId(
            ContractNegotiationStore store,
            TransferProcessStore transferProcessStore
    ) {
        // arrange
        var contractId = UUID.randomUUID().toString();
        createContractNegotiation(store, COUNTER_PARTY_ADDRESS, contractId);

        var customRequestJson = Json.createObjectBuilder()
                .add(Prop.Edc.DATA_DESTINATION, Json.createObjectBuilder()
                        .add(Prop.Edc.TYPE, "HttpData")
                        .add(Prop.Edc.BASE_URL, DATA_SINK))
                .add(Prop.Edc.PRIVATE_PROPERTIES, Json.createObjectBuilder()
                        .add(Prop.Edc.RECEIVER_HTTP_ENDPOINT, "http://my-pull-backend")
                        .add("this-will-disappear", "because-its-not-an-url")
                        .add("http://unknown/custom-prop", "value"))
                .build();
        var request = new InitiateCustomTransferRequest(
                contractId,
                JsonUtils.toJson(customRequestJson)
        );

        // act
        var result = client.uiApi().initiateCustomTransfer(request);

        // then
        var transferProcess = transferProcessStore.findById(result.getId());
        assertThat(transferProcess).isNotNull();
        assertThat(transferProcess.getPrivateProperties()).containsAllEntriesOf(Map.of(
                Prop.Edc.RECEIVER_HTTP_ENDPOINT, "http://my-pull-backend",
                "http://unknown/custom-prop", "value"
        ));

        var dataRequest = transferProcess.getDataRequest();
        assertThat(dataRequest.getContractId()).isEqualTo(contractId);
        assertThat(dataRequest.getConnectorAddress()).isEqualTo(COUNTER_PARTY_ADDRESS);
        assertThat(dataRequest.getDataDestination().getProperties()).containsAllEntriesOf(Map.of(
                Prop.Edc.TYPE, "HttpData",
                Prop.Edc.BASE_URL, DATA_SINK
        ));
    }

    /*
     * The web UI does
     *
     * curl 'http://localhost:22002/api/management/wrapper/ui/pages/contract-agreement-page/transfers'
     * -X POST
     *
     *  -H 'User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:124.0) Gecko/20100101 Firefox/124.0'
     *  -H 'Accept-Language: en-US,en;q=0.5'
     *  -H 'Accept-Encoding: gzip, deflate, br'
     *  -H 'Referer: http://localhost:22000/'
     *  -H 'X-Api-Key: ApiKeyDefaultValue'
     *  -H 'Content-Type: application/json'
     *  -H 'Origin: http://localhost:22000'
     *  -H 'DNT: 1'
     *  -H 'Sec-GPC: 1'
     *  -H 'Connection: keep-alive'
     *  -H 'Sec-Fetch-Dest: empty'
     *  -H 'Sec-Fetch-Mode: cors'
     *  -H 'Sec-Fetch-Site: same-site'
     *  --data-raw '{"contractAgreementId":"Y29udHJhY3Qx:Y3VzdG9tX2RhdGFzb3VyY2U=:NDAyOTg3ZTUtM2U1ZS00NDJlLWFmOGYtZjE0NDM1YjU0MjQ1","dataSinkProperties":{"https://w3id.org/edc/v0.0.1/ns/type":"HttpData","https://w3id.org/edc/v0.0.1/ns/baseUrl":"http://example.com","https://w3id.org/edc/v0.0.1/ns/method":"POST","https://w3id.org/edc/v0.0.1/ns/queryParams":""},"transferProcessProperties":{"https://w3id.org/edc/v0.0.1/ns/method":"PATCH","https://w3id.org/edc/v0.0.1/ns/pathSegments":"segment","https://w3id.org/edc/v0.0.1/ns/queryParams":"query=val","https://w3id.org/edc/v0.0.1/ns/body":"[]","https://w3id.org/edc/v0.0.1/ns/contentType":"application/json"}}'
     *
     * change this to use the new params.
     */

    /**
     * Tests the transformation of the legacy request as done in the UI into a request that can be sent to the
     * sovity patched EDC connector that supports parameterized requests.
     */
    @Test
    void transformLegacyRequestToParameterizedRequest(
            ContractNegotiationStore store,
            TransferProcessStore transferProcessStore
    ) {
        // arrange
        var contractId = UUID.randomUUID().toString();
        createContractNegotiation(store, COUNTER_PARTY_ADDRESS, contractId);

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

        var request = new InitiateTransferRequest(
                contractId,
                Map.of(
                        "type", "HttpData",
                        "baseUrl", "http://example.com/segment0"
                ),
                Map.of(
                        "https://w3id.org/edc/v0.0.1/ns/pathSegments", "my-endpoint",
                        "https://w3id.org/edc/v0.0.1/ns/method", "METHOD",
                        "https://w3id.org/edc/v0.0.1/ns/queryParams", "queryParams",
                        "https://w3id.org/edc/v0.0.1/ns/contentType", "mimetype",
                        "https://w3id.org/edc/v0.0.1/ns/body", "[]"
                )
        );

        // act
        var result = client.uiApi().initiateTransfer(request);

        // then
        var transferProcess = transferProcessStore.findById(result.getId());
        assertThat(transferProcess).isNotNull();
        assertThat(transferProcess.getPrivateProperties()).containsAllEntriesOf(Map.of(
                "privateProperty", "privateValue"
        ));

        var dataRequest = transferProcess.getDataRequest();
        assertThat(dataRequest.getContractId()).isEqualTo(contractId);
        assertThat(dataRequest.getConnectorAddress()).isEqualTo(COUNTER_PARTY_ADDRESS);
        assertThat(dataRequest.getDataDestination().getProperties()).containsAllEntriesOf(Map.of(
                "https://w3id.org/edc/v0.0.1/ns/type", "HttpData",
                "baseUrl", DATA_SINK
        ));
    }

    private ContractNegotiation createContractNegotiation(
            ContractNegotiationStore store,
            String counterPartyAddress,
            String agreementId
    ) {
        var assetId = UUID.randomUUID().toString();
        var agreement = ContractAgreement.Builder.newInstance()
                .id(agreementId)
                .providerId(UUID.randomUUID().toString())
                .consumerId(UUID.randomUUID().toString())
                .assetId(assetId)
                .policy(getPolicy())
                .build();

        var negotiation = ContractNegotiation.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .counterPartyId(UUID.randomUUID().toString())
                .counterPartyAddress(counterPartyAddress)
                .protocol(HttpMessageProtocol.DATASPACE_PROTOCOL_HTTP)
                .contractAgreement(agreement)
                .contractOffer(createContractOffer(assetId))
                .state(ContractNegotiationStates.FINALIZED.code())
                .build();

        store.save(negotiation);
        return negotiation;
    }

    private Policy getPolicy() {
        return Policy.Builder.newInstance().build();
    }

    private ContractOffer createContractOffer(String assetId) {
        return ContractOffer.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .assetId(assetId)
                .policy(getPolicy())
                .build();
    }
}
