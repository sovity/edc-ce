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

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationSimplifiedState;
import de.sovity.edc.client.gen.model.DataSourceType;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateDto;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiContractNegotiation;
import de.sovity.edc.client.gen.model.UiContractOffer;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.client.gen.model.UiDataOffer;
import de.sovity.edc.client.gen.model.UiDataSource;
import de.sovity.edc.client.gen.model.UiDataSourceHttpData;
import de.sovity.edc.client.gen.model.UiPolicyConstraint;
import de.sovity.edc.client.gen.model.UiPolicyExpression;
import de.sovity.edc.client.gen.model.UiPolicyExpressionType;
import de.sovity.edc.client.gen.model.UiPolicyLiteral;
import de.sovity.edc.client.gen.model.UiPolicyLiteralType;
import de.sovity.edc.extension.e2e.connector.remotes.management_api.ManagementApiConnectorRemote;
import de.sovity.edc.extension.e2e.connector.remotes.test_backend_controller.TestBackendRemote;
import de.sovity.edc.extension.e2e.junit.CeE2eTestExtension;
import de.sovity.edc.extension.e2e.junit.utils.Consumer;
import de.sovity.edc.extension.e2e.junit.utils.Provider;
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import de.sovity.edc.utils.config.ConfigProps;
import de.sovity.edc.utils.config.ConfigUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import org.awaitility.Awaitility;
import org.eclipse.edc.spi.system.configuration.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

import static de.sovity.edc.extension.e2e.connector.remotes.management_api.DataTransferTestUtil.validateDataTransferred;
import static org.assertj.core.api.Assertions.assertThat;

class ApiWrapperDemoTest {

    @RegisterExtension
    private static CeE2eTestExtension e2eTestExtension = CeE2eTestExtension.builder()
        .additionalModule(":launchers:connectors:sovity-dev")
        .build();
    private final String dataOfferData = "expected data 123";
    private final String dataOfferId = "my-data-offer-2023-11";

    TestBackendRemote dataAddress;
    EdcClient consumerClient;
    EdcClient providerClient;
    Config consumerConfig;
    Config providerConfig;

    @BeforeEach
    void setup(
        TestBackendRemote dataAddress,
        @Consumer EdcClient consumerClient,
        @Consumer Config consumerConfig,
        @Provider EdcClient providerClient,
        @Provider Config providerConfig
    ) {
        this.dataAddress = dataAddress;
        this.consumerClient = consumerClient;
        this.consumerConfig = consumerConfig;
        this.providerClient = providerClient;
        this.providerConfig = providerConfig;
    }

    @Test
    @DisabledOnGithub
    void provide_and_consume() {
        // provider: create data offer
        createPolicy();
        createAsset();
        createContractDefinition();

        // consumer: negotiate contract and transfer data
        var participantId = ConfigProps.EDC_PARTICIPANT_ID.getStringOrThrow(providerConfig);
        var dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(participantId, ConfigUtils.getProtocolApiUrl(providerConfig));
        var negotiation = initiateNegotiation(dataOffers.get(0), dataOffers.get(0).getContractOffers().get(0));
        negotiation = awaitNegotiationDone(negotiation.getContractNegotiationId());
        initiateTransfer(negotiation);

        // check data sink
        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), dataOfferData);
    }

    private void createAsset() {
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl(dataAddress.getDataSourceUrl(dataOfferData))
                .build())
            .build();

        var asset = UiAssetCreateRequest.builder()
            .id(dataOfferId)
            .title("My Data Offer")
            .description("Example Data Offer.")
            .version("2023-11")
            .language("EN")
            .publisherHomepage("https://my-department.my-org.com/my-data-offer")
            .licenseUrl("https://my-department.my-org.com/my-data-offer#license")
            .dataSource(dataSource)
            .build();

        providerClient.uiApi().createAsset(asset);
    }

    private void createPolicy() {
        var afterYesterday = UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.CONSTRAINT)
            .constraint(UiPolicyConstraint.builder()
                .left("POLICY_EVALUATION_TIME")
                .operator(OperatorDto.GT)
                .right(UiPolicyLiteral.builder()
                    .type(UiPolicyLiteralType.STRING)
                    .value(OffsetDateTime.now().minusDays(1).toString())
                    .build())
                .build())
            .build();

        var beforeTomorrow = UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.CONSTRAINT)
            .constraint(UiPolicyConstraint.builder()
                .left("POLICY_EVALUATION_TIME")
                .operator(OperatorDto.LT)
                .right(UiPolicyLiteral.builder()
                    .type(UiPolicyLiteralType.STRING)
                    .value(OffsetDateTime.now().plusDays(1).toString())
                    .build())
                .build())
            .build();

        var expression = UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.AND)
            .expressions(List.of(afterYesterday, beforeTomorrow))
            .build();

        var policyDefinition = PolicyDefinitionCreateDto.builder()
            .policyDefinitionId(dataOfferId)
            .expression(expression)
            .build();

        providerClient.uiApi().createPolicyDefinitionV2(policyDefinition);
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
            .assetId(dataOffer.getAsset().getAssetId())
            .contractOfferId(contractOffer.getContractOfferId())
            .policyJsonLd(contractOffer.getPolicy().getPolicyJsonLd())
            .build();

        return consumerClient.uiApi().initiateContractNegotiation(negotiationRequest);
    }

    private UiContractNegotiation awaitNegotiationDone(String negotiationId) {
        var negotiation = Awaitility.await().atMost(Duration.ofSeconds(15)).until(
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

    private String getProtocolEndpoint(ManagementApiConnectorRemote connector) {
        return connector.getConfig().getProtocolApiUrl();
    }
}
