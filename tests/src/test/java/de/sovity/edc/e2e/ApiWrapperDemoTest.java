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
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationSimplifiedState;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiContractNegotiation;
import de.sovity.edc.client.gen.model.UiContractOffer;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.client.gen.model.UiDataOffer;
import de.sovity.edc.client.gen.model.UiPolicyConstraint;
import de.sovity.edc.client.gen.model.UiPolicyCreateRequest;
import de.sovity.edc.client.gen.model.UiPolicyLiteral;
import de.sovity.edc.client.gen.model.UiPolicyLiteralType;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.MockDataAddressRemote;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseViaTestcontainers;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import org.awaitility.Awaitility;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtil.validateDataTransferred;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static org.assertj.core.api.Assertions.assertThat;

class ApiWrapperDemoTest {

    private static final String PROVIDER_PARTICIPANT_ID = "provider";
    private static final String CONSUMER_PARTICIPANT_ID = "consumer";

    @RegisterExtension
    static EdcExtension providerEdcContext = new EdcExtension();
    @RegisterExtension
    static EdcExtension consumerEdcContext = new EdcExtension();

    @RegisterExtension
    static final TestDatabase PROVIDER_DATABASE = new TestDatabaseViaTestcontainers();
    @RegisterExtension
    static final TestDatabase CONSUMER_DATABASE = new TestDatabaseViaTestcontainers();

    private ConnectorRemote providerConnector;
    private ConnectorRemote consumerConnector;

    private EdcClient providerClient;
    private EdcClient consumerClient;
    private MockDataAddressRemote dataAddress;
    private final String dataOfferData = "expected data 123";

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
    void provide_and_consume() {
        // provider: create data offer
        createPolicy();
        createAsset();
        createContractDefinition();

        // consumer: negotiate contract and transfer data
        var dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(getProtocolEndpoint(providerConnector));
        var negotiation = initiateNegotiation(dataOffers.get(0), dataOffers.get(0).getContractOffers().get(0));
        negotiation = awaitNegotiationDone(negotiation.getContractNegotiationId());
        initiateTransfer(negotiation);

        // check data sink
        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), dataOfferData);
    }

    private void createAsset() {
        var asset = UiAssetCreateRequest.builder()
                .id(dataOfferId)
                .title("My Data Offer")
                .description("Example Data Offer.")
                .version("2023-11")
                .language("EN")
                .publisherHomepage("https://my-department.my-org.com/my-data-offer")
                .licenseUrl("https://my-department.my-org.com/my-data-offer#license")
                .dataAddressProperties(Map.of(
                        Prop.Edc.TYPE, "HttpData",
                        Prop.Edc.METHOD, "GET",
                        Prop.Edc.BASE_URL, dataAddress.getDataSourceUrl(dataOfferData)
                ))
                .build();

        providerClient.uiApi().createAsset(asset);
    }

    private void createPolicy() {
        var afterYesterday = UiPolicyConstraint.builder()
                .left("POLICY_EVALUATION_TIME")
                .operator(OperatorDto.GT)
                .right(UiPolicyLiteral.builder()
                        .type(UiPolicyLiteralType.STRING)
                        .value(OffsetDateTime.now().minusDays(1).toString())
                        .build())
                .build();

        var beforeTomorrow = UiPolicyConstraint.builder()
                .left("POLICY_EVALUATION_TIME")
                .operator(OperatorDto.LT)
                .right(UiPolicyLiteral.builder()
                        .type(UiPolicyLiteralType.STRING)
                        .value(OffsetDateTime.now().plusDays(1).toString())
                        .build())
                .build();

        var policyDefinition = PolicyDefinitionCreateRequest.builder()
                .policyDefinitionId(dataOfferId)
                .policy(UiPolicyCreateRequest.builder()
                        .constraints(List.of(afterYesterday, beforeTomorrow))
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
