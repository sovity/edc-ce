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
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.extension.e2e.connector.remotes.api_wrapper;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationSimplifiedState;
import de.sovity.edc.client.gen.model.ContractTerminationRequest;
import de.sovity.edc.client.gen.model.DataSourceType;
import de.sovity.edc.client.gen.model.IdResponseDto;
import de.sovity.edc.client.gen.model.InitiateCustomTransferRequest;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateDto;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiContractNegotiation;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.client.gen.model.UiDataSource;
import de.sovity.edc.client.gen.model.UiDataSourceHttpData;
import de.sovity.edc.client.gen.model.UiPolicyConstraint;
import de.sovity.edc.client.gen.model.UiPolicyExpression;
import de.sovity.edc.client.gen.model.UiPolicyExpressionType;
import de.sovity.edc.client.gen.model.UiPolicyLiteral;
import de.sovity.edc.client.gen.model.UiPolicyLiteralType;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import org.awaitility.Awaitility;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation;
import org.jetbrains.annotations.NotNull;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static de.sovity.edc.client.gen.model.TransferProcessSimplifiedState.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class E2eTestScenario {
    @NonNull
    private final E2eTestScenarioConfig config;

    @NonNull
    private final EdcClient consumerClient;

    @NonNull
    private final EdcClient providerClient;

    @NonNull
    private final ClientAndServer mockServer;

    private final AtomicInteger assetCounter = new AtomicInteger(0);

    public String createAsset() {
        val dummyDataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("http://example.com")
                .build())
            .build();

        return internalCreateAsset(nextAssetId(), dummyDataSource).getId();
    }

    private @NotNull String nextAssetId() {
        return "asset-" + assetCounter.getAndIncrement();
    }

    public String createAsset(String id, UiDataSourceHttpData uiDataSourceHttpData) {
        val uiDataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(uiDataSourceHttpData)
            .build();

        return internalCreateAsset(id, uiDataSource).getId();
    }

    public String createAsset(String id, UiDataSource uiDataSource) {
        return internalCreateAsset(id, uiDataSource).getId();
    }

    public String createAsset(UiAssetCreateRequest uiAssetCreateRequest) {
        return providerClient.uiApi().createAsset(uiAssetCreateRequest).getId();
    }

    public MockedAsset createAssetWithMockResource(String id) {

        val path = "/assets/" + id;
        val url = "http://localhost:" + mockServer.getPort() + path;

        val uiDataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder().baseUrl(url).build())
            .build();

        val accesses = new AtomicInteger(0);

        mockServer.when(HttpRequest.request(path).withMethod("GET")).respond(it -> {
            accesses.incrementAndGet();
            return HttpResponse.response().withStatusCode(200);
        });

        internalCreateAsset(id, uiDataSource);

        return new MockedAsset(id, accesses);
    }

    private IdResponseDto internalCreateAsset(String assetId, UiDataSource dataSource) {
        return providerClient.uiApi()
            .createAsset(UiAssetCreateRequest.builder()
                .id(assetId)
                .title("AssetName " + assetId)
                .version("1.0.0")
                .language("en")
                .dataSource(dataSource)
                .build());
    }

    public String createContractDefinition(String assetId) {
        return createContractDefinition("always-true", assetId).getId();
    }

    public IdResponseDto createContractDefinition(String policyId, String assetId) {
        return providerClient.uiApi().createContractDefinition(ContractDefinitionRequest.builder()
            .contractDefinitionId("cd-" + policyId + "-" + assetId)
            .accessPolicyId(policyId)
            .contractPolicyId(policyId)
            .assetSelector(List.of(UiCriterion.builder()
                .operandLeft(Prop.Edc.ID)
                .operator(UiCriterionOperator.EQ)
                .operandRight(UiCriterionLiteral.builder()
                    .type(UiCriterionLiteralType.VALUE)
                    .value(assetId)
                    .build())
                .build()))
            .build());
    }

    public UiContractNegotiation negotiateAssetAndAwait(String assetId) {
        val connectorEndpoint = config.getProviderProtocolApiUrl();
        val participantId = config.getProviderParticipantId();
        val offers = consumerClient.uiApi().getCatalogPageDataOffers(participantId, connectorEndpoint);

        val offersContainingContract = offers.stream()
            .filter(offer -> offer.getAsset().getAssetId().equals(assetId))
            .toList();

        assertThat(offersContainingContract).hasSize(1);

        val firstContractOffer = offersContainingContract.get(0).getContractOffers().get(0);
        val dataOffer = offersContainingContract.get(0);
        var negotiationRequest = ContractNegotiationRequest.builder()
            .counterPartyAddress(dataOffer.getEndpoint())
            .assetId(dataOffer.getAsset().getAssetId())
            .contractOfferId(firstContractOffer.getContractOfferId())
            .policyJsonLd(firstContractOffer.getPolicy().getPolicyJsonLd())
            .build();

        val negotiation = consumerClient.uiApi().initiateContractNegotiation(negotiationRequest);

        val neg = Awaitility.await().atMost(config.getTimeout()).until(
            () -> consumerClient.uiApi().getContractNegotiation(negotiation.getContractNegotiationId()),
            it -> it.getState().getSimplifiedState() != ContractNegotiationSimplifiedState.IN_PROGRESS
        );

        assertThat(neg.getState().getSimplifiedState()).isEqualTo(ContractNegotiationSimplifiedState.AGREED);

        return neg;
    }

    public void createPolicy(String id, OffsetDateTime from, OffsetDateTime until) {
        val startConstraint = UiPolicyConstraint.builder()
            .left("POLICY_EVALUATION_TIME")
            .operator(OperatorDto.GT)
            .right(UiPolicyLiteral.builder()
                .type(UiPolicyLiteralType.STRING)
                .value(from.toString())
                .build())
            .build();

        val endConstraint = UiPolicyConstraint.builder()
            .left("POLICY_EVALUATION_TIME")
            .operator(OperatorDto.LT)
            .right(UiPolicyLiteral.builder()
                .type(UiPolicyLiteralType.STRING)
                .value(until.toString())
                .build())
            .build();

        val expression = UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.AND)
            .expressions(Stream.of(startConstraint, endConstraint)
                .map(it -> UiPolicyExpression.builder()
                    .type(UiPolicyExpressionType.CONSTRAINT)
                    .constraint(it)
                    .build())
                .toList())
            .build();

        var policyDefinition = PolicyDefinitionCreateDto.builder()
            .policyDefinitionId(id)
            .expression(expression)
            .build();

        providerClient.uiApi().createPolicyDefinitionV2(policyDefinition);
    }

    public String transferAndAwait(InitiateTransferRequest transferRequest) {
        val transferInit = consumerClient.uiApi().initiateTransfer(transferRequest).getId();
        awaitTransferCompletion(transferInit);
        return transferInit;
    }

    public String transferAndAwait(InitiateCustomTransferRequest transferRequest) {
        val transferInit = consumerClient.uiApi().initiateCustomTransfer(transferRequest).getId();
        awaitTransferCompletion(transferInit);
        return transferInit;
    }

    public void awaitTransferCompletion(String transferId) {
        Awaitility.await().atMost(config.getTimeout()).until(
            () -> consumerClient.uiApi()
                .getTransferHistoryPage()
                .getTransferEntries()
                .stream()
                .filter(it -> it.getTransferProcessId().equals(transferId))
                .findFirst()
                .map(it -> it.getState().getSimplifiedState()),
            it -> it.orElse(RUNNING) != RUNNING
        );
    }

    public IdResponseDto terminateContractAgreementAndAwait(
        ContractNegotiation.Type party,
        String contractAgreementId,
        ContractTerminationRequest terminationRequest
    ) {
        if (party.equals(ContractNegotiation.Type.CONSUMER)) {
            return consumerClient.uiApi().terminateContractAgreement(contractAgreementId, terminationRequest);
        } else {
            return providerClient.uiApi().terminateContractAgreement(contractAgreementId, terminationRequest);
        }
    }
}
