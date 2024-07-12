/*
 *  Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.e2e.utils;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationSimplifiedState;
import de.sovity.edc.client.gen.model.DataSourceType;
import de.sovity.edc.client.gen.model.IdResponseDto;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.client.gen.model.UiAssetCreateRequest;
import de.sovity.edc.client.gen.model.UiContractNegotiation;
import de.sovity.edc.client.gen.model.UiCriterion;
import de.sovity.edc.client.gen.model.UiCriterionLiteral;
import de.sovity.edc.client.gen.model.UiCriterionLiteralType;
import de.sovity.edc.client.gen.model.UiCriterionOperator;
import de.sovity.edc.client.gen.model.UiDataOffer;
import de.sovity.edc.client.gen.model.UiDataSource;
import de.sovity.edc.client.gen.model.UiDataSourceHttpData;
import de.sovity.edc.client.gen.model.UiPolicyConstraint;
import de.sovity.edc.client.gen.model.UiPolicyCreateRequest;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.utils.Delegate;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.awaitility.Awaitility;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class Scenario {
    private final EdcClient consumerClient;
    private final ConnectorConfig consumerConfig;
    private final EdcClient providerClient;
    private final ConnectorConfig providerConfig;

    private final Delegate<String> alwaysTruePolicy = new Delegate<>(
        () -> createPolicyDefinition("alwaysTrue")
    );

    public String createAsset(String id) {

        val dummyDataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("http://example.com")
                .build())
            .build();

        return internalCreateAsset(id, dummyDataSource).getId();
    }

    public MockedAsset createAssetWithMockResource(String id, ClientAndServer clientAndServer) {

        val path = "/assets/" + id;
        val url = "http://localhost:" + clientAndServer.getPort() + path;

        val uiDataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder().baseUrl(url).build())
            .build();

        val accessed = new AtomicBoolean(false);

        clientAndServer.when(HttpRequest.request(path).withMethod("GET")).respond(it -> {
            accessed.set(true);
            return HttpResponse.response().withStatusCode(200);
        });

        internalCreateAsset(id, uiDataSource);

        return new MockedAsset(id, accessed);
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

    public String createPolicyDefinition(String policyId, UiPolicyConstraint... constraints) {
        return createPolicyDefinition(policyId, Arrays.stream(constraints).toList()).getId();
    }

    private IdResponseDto createPolicyDefinition(String policyId, List<UiPolicyConstraint> constraints) {
        var policyDefinition = PolicyDefinitionCreateRequest.builder()
            .policyDefinitionId(policyId)
            .policy(UiPolicyCreateRequest.builder()
                .constraints(constraints)
                .build()
            )
            .build();

        return providerClient.uiApi().createPolicyDefinition(policyDefinition);
    }

    public String createContractDefinition(String assetId) {
        return createContractDefinition(alwaysTruePolicy.get(), assetId).getId();
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
        val connectorEndpoint = providerConfig.getProtocolEndpoint().getUri().toString();
        val offers = consumerClient.uiApi().getCatalogPageDataOffers(connectorEndpoint);

        val offersContainingContract = offers.stream()
            .filter(offer -> offer.getAsset().getAssetId().equals(assetId))
            .toList();

        assertThat(offersContainingContract).hasSize(1);

        val firstContractOffer = offersContainingContract.get(0).getContractOffers().get(0);
        UiDataOffer dataOffer = offersContainingContract.get(0);
        var negotiationRequest = ContractNegotiationRequest.builder()
            .counterPartyAddress(dataOffer.getEndpoint())
            .counterPartyParticipantId(dataOffer.getParticipantId())
            .assetId(dataOffer.getAsset().getAssetId())
            .contractOfferId(firstContractOffer.getContractOfferId())
            .policyJsonLd(firstContractOffer.getPolicy().getPolicyJsonLd())
            .build();

        val negotiationId = consumerClient.uiApi().initiateContractNegotiation(negotiationRequest).getContractNegotiationId();

        var negotiation = Awaitility.await().atMost(ofSeconds(5)).until(
            () -> consumerClient.uiApi().getContractNegotiation(negotiationId),
            it -> it.getState().getSimplifiedState() != ContractNegotiationSimplifiedState.IN_PROGRESS
        );

        assertThat(negotiation.getState().getSimplifiedState()).isEqualTo(ContractNegotiationSimplifiedState.AGREED);

        return negotiation;
    }
}
