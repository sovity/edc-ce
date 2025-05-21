/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.connector.remotes.api_wrapper;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.CatalogFilterExpression;
import de.sovity.edc.client.gen.model.CatalogFilterExpressionLiteral;
import de.sovity.edc.client.gen.model.CatalogFilterExpressionLiteralType;
import de.sovity.edc.client.gen.model.CatalogFilterExpressionOperator;
import de.sovity.edc.client.gen.model.CatalogQuery;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationSimplifiedState;
import de.sovity.edc.client.gen.model.ContractTerminationRequest;
import de.sovity.edc.client.gen.model.DataOfferCreateRequest;
import de.sovity.edc.client.gen.model.DataOfferPublishType;
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
import de.sovity.edc.client.gen.model.UiDataSourceHttpDataMethod;
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
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation;
import org.jetbrains.annotations.NotNull;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;
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

    private final Random random = new Random();

    /**
     * For rare cases when the provider is the consumer and the consumer is the provider
     */
    public E2eTestScenario reverseScenario() {
        return E2eTestScenario.builder()
            .config(new E2eTestScenarioConfig(
                // reversed
                config.getConsumer(),
                // reversed
                config.getProvider(),
                config.getTimeout()
            ))
            .consumerClient(providerClient)
            .providerClient(consumerClient)
            .mockServer(mockServer)
            .build();
    }

    public String createAsset() {
        val dummyDataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("http://example.com")
                .build())
            .build();

        return internalCreateAsset(providerClient, nextAssetId(), dummyDataSource).getId();
    }

    public @NotNull String nextAssetId() {
        return "asset-" + random.nextInt();
    }

    public String createAsset(String id, UiDataSourceHttpData uiDataSourceHttpData) {
        val uiDataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(uiDataSourceHttpData)
            .build();

        return internalCreateAsset(providerClient, id, uiDataSource).getId();
    }

    public String createAsset(String id, UiDataSource uiDataSource) {
        return internalCreateAsset(providerClient, id, uiDataSource).getId();
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

        internalCreateAsset(providerClient, id, uiDataSource);

        return new MockedAsset(id, accesses);
    }

    private IdResponseDto internalCreateAsset(EdcClient client, String assetId, UiDataSource dataSource) {
        return client.uiApi()
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

    /**
     * Negotiate from the consumer an asset located on the provider side
     */
    public UiContractNegotiation negotiateAssetAndAwait(String assetId) {
        val connectorEndpoint = config.getProvider().getProtocolApiUrl();
        val participantId = config.getProvider().getParticipantId();
        return negotiate(consumerClient, connectorEndpoint, participantId, assetId);
    }

    private UiContractNegotiation negotiate(
        EdcClient negotiator,
        String connectorEndpoint,
        String participantId,
        String assetId
    ) {
        val offers = negotiator.useCaseApi()
            .queryCatalog(CatalogQuery.builder()
                .participantId(participantId)
                .connectorEndpoint(connectorEndpoint)
                .filterExpressions(List.of(
                    CatalogFilterExpression.builder()
                        .operandLeft(Asset.PROPERTY_ID)
                        .operator(CatalogFilterExpressionOperator.EQ)
                        .operandRight(CatalogFilterExpressionLiteral.builder()
                            .type(CatalogFilterExpressionLiteralType.VALUE)
                            .value(assetId)
                            .build())
                        .build()
                ))
                .build());

        val offersContainingContract = offers.stream()
            .filter(offer -> offer.getAsset().getAssetId().equals(assetId))
            .toList();

        assertThat(offersContainingContract).hasSize(1);

        val firstContractOffer = offersContainingContract.get(0).getContractOffers().get(0);
        val dataOffer = offersContainingContract.get(0);
        var negotiationRequest = ContractNegotiationRequest.builder()
            .counterPartyId(dataOffer.getParticipantId())
            .counterPartyAddress(dataOffer.getEndpoint())
            .assetId(dataOffer.getAsset().getAssetId())
            .contractOfferId(firstContractOffer.getContractOfferId())
            .policyJsonLd(firstContractOffer.getPolicy().getPolicyJsonLd())
            .build();

        val negotiation = negotiator.uiApi().initiateContractNegotiation(negotiationRequest);

        val neg = Awaitility.await().atMost(config.getTimeout()).until(
            () -> negotiator.uiApi().getContractNegotiation(negotiation.getContractNegotiationId()),
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
        awaitTransferCompletion(consumerClient, transferInit);
        return transferInit;
    }

    public String transferAndAwait(InitiateCustomTransferRequest transferRequest) {
        val transferInit = consumerClient.uiApi().initiateCustomTransfer(transferRequest).getId();
        awaitTransferCompletion(consumerClient, transferInit);
        return transferInit;
    }

    public void awaitTransferCompletion(EdcClient client, String transferId) {
        Awaitility.await().atMost(config.getTimeout()).until(
            () -> client.uiApi()
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

    public String createDataOffer(String assetId) {
        return internalCreateDataOffer(providerClient, assetId);
    }

    private String internalCreateDataOffer(EdcClient client, String assetId) {
        return client.uiApi().createDataOffer(
            DataOfferCreateRequest.builder()
                .asset(
                    UiAssetCreateRequest.builder()
                        .dataSource(
                            UiDataSource.builder()
                                .httpData(
                                    UiDataSourceHttpData.builder()
                                        .baseUrl("http://example.com")
                                        .method(UiDataSourceHttpDataMethod.GET)
                                        .build()
                                )
                                .type(DataSourceType.HTTP_DATA)
                                .build()
                        )
                        .id(assetId)
                        .title("My asset")
                        .build()
                )
                .publishType(DataOfferPublishType.PUBLISH_RESTRICTED)
                .policyExpression(
                    UiPolicyExpression.builder()
                        .type(UiPolicyExpressionType.CONSTRAINT)
                        .constraint(
                            UiPolicyConstraint.builder()
                                .left("foo")
                                .operator(OperatorDto.EQ)
                                .right(
                                    UiPolicyLiteral.builder()
                                        .type(UiPolicyLiteralType.STRING)
                                        .value("bar")
                                        .build()
                                )
                                .build()
                        )
                        .build()
                ).build()
        ).getId();
    }
}
