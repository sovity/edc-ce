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
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationSimplifiedState;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.client.gen.model.UiContractNegotiation;
import de.sovity.edc.client.gen.model.UiContractOffer;
import de.sovity.edc.client.gen.model.UiDataOffer;
import de.sovity.edc.client.gen.model.UiDataSourceHttpData;
import de.sovity.edc.e2e.utils.Consumer;
import de.sovity.edc.e2e.utils.E2eScenario;
import de.sovity.edc.e2e.utils.E2eTestExtension;
import de.sovity.edc.e2e.utils.Provider;
import de.sovity.edc.extension.e2e.connector.MockDataAddressRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import lombok.val;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Duration;
import java.time.OffsetDateTime;

import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtil.validateDataTransferred;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(E2eTestExtension.class)
class ApiWrapperDemoTest {

    private MockDataAddressRemote dataAddress;
    private final String dataOfferData = "expected data 123";

    private final String dataOfferId = "my-data-offer-2023-11";

    @BeforeEach
    void setup(@Provider ConnectorConfig providerConfig) {
        // We use the provider EDC as data sink / data source (it has the test-backend-controller extension)
        dataAddress = new MockDataAddressRemote(providerConfig.getDefaultEndpoint());
    }

    @Test
    void provide_and_consume(
        E2eScenario scenario,
        @Consumer EdcClient consumerClient,
        @Provider ConnectorConfig providerConfig) {

        // provider: create data offer
        val now = OffsetDateTime.now();
        scenario.createPolicy(dataOfferId, now.minusDays(1), now.plusDays(1));
        scenario.createAsset(
            dataOfferId,
            UiDataSourceHttpData.builder()
            .baseUrl(dataAddress.getDataSourceUrl(dataOfferData))
            .build());
        scenario.createContractDefinition(dataOfferId);

        // consumer: negotiate contract and transfer data
        var dataOffers = consumerClient.uiApi().getCatalogPageDataOffers(providerConfig.getProtocolEndpoint().getUri().toString());
        var negotiation = initiateNegotiation(dataOffers.get(0), dataOffers.get(0).getContractOffers().get(0), consumerClient);
        negotiation = awaitNegotiationDone(negotiation.getContractNegotiationId(), consumerClient);
        initiateTransfer(negotiation, consumerClient);

        // check data sink
        validateDataTransferred(dataAddress.getDataSinkSpyUrl(), dataOfferData);
    }

    private UiContractNegotiation initiateNegotiation(UiDataOffer dataOffer, UiContractOffer contractOffer, EdcClient consumerClient) {
        var negotiationRequest = ContractNegotiationRequest.builder()
            .counterPartyAddress(dataOffer.getEndpoint())
            .counterPartyParticipantId(dataOffer.getParticipantId())
            .assetId(dataOffer.getAsset().getAssetId())
            .contractOfferId(contractOffer.getContractOfferId())
            .policyJsonLd(contractOffer.getPolicy().getPolicyJsonLd())
            .build();

        return consumerClient.uiApi().initiateContractNegotiation(negotiationRequest);
    }

    private UiContractNegotiation awaitNegotiationDone(String negotiationId, EdcClient consumerClient) {
        var negotiation = Awaitility.await().atMost(Duration.of(60, SECONDS)).until(
            () -> consumerClient.uiApi().getContractNegotiation(negotiationId),
            it -> it.getState().getSimplifiedState() != ContractNegotiationSimplifiedState.IN_PROGRESS
        );

        assertThat(negotiation.getState().getSimplifiedState()).isEqualTo(ContractNegotiationSimplifiedState.AGREED);
        return negotiation;
    }

    private void initiateTransfer(UiContractNegotiation negotiation, EdcClient consumerClient) {
        var contractAgreementId = negotiation.getContractAgreementId();
        var transferRequest = InitiateTransferRequest.builder()
            .contractAgreementId(contractAgreementId)
            .dataSinkProperties(dataAddress.getDataSinkProperties())
            .build();
        consumerClient.uiApi().initiateTransfer(transferRequest);
    }
}
