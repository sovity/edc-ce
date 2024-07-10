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
 */

package de.sovity.edc.e2e;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.ApiException;
import de.sovity.edc.client.gen.model.ContractAgreementPage;
import de.sovity.edc.client.gen.model.ContractAgreementPageQuery;
import de.sovity.edc.client.gen.model.ContractDefinitionRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationSimplifiedState;
import de.sovity.edc.client.gen.model.ContractTerminatedBy;
import de.sovity.edc.client.gen.model.ContractTerminationRequest;
import de.sovity.edc.client.gen.model.DataSourceType;
import de.sovity.edc.client.gen.model.PolicyDefinitionCreateRequest;
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
import de.sovity.edc.client.gen.model.UiPolicyCreateRequest;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseFactory;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.SneakyThrows;
import lombok.val;
import org.awaitility.Awaitility;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.iam.IdentityService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockserver.integration.ClientAndServer;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static de.sovity.edc.client.gen.model.ContractTerminatedBy.COUNTERPARTY;
import static de.sovity.edc.client.gen.model.ContractTerminatedBy.SELF;
import static de.sovity.edc.client.gen.model.ContractTerminationStatus.ONGOING;
import static de.sovity.edc.client.gen.model.ContractTerminationStatus.TERMINATED;
import static de.sovity.edc.ext.wrapper.api.ui.model.ContractTerminationRequest.MAX_DETAIL_SIZE;
import static de.sovity.edc.ext.wrapper.api.ui.model.ContractTerminationRequest.MAX_REASON_SIZE;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockserver.stop.Stop.stopQuietly;

public class ContractTerminationTest {
    private static final String PROVIDER_PARTICIPANT_ID = "provider";
    private static final String CONSUMER_PARTICIPANT_ID = "consumer";

    @RegisterExtension
    static final EdcExtension PROVIDER_EDC_CONTEXT = new EdcExtension();
    @RegisterExtension
    static final EdcExtension CONSUMER_EDC_CONTEXT = new EdcExtension();

    @RegisterExtension
    static final TestDatabase PROVIDER_DATABASE = TestDatabaseFactory.getTestDatabase(1);
    @RegisterExtension
    static final TestDatabase CONSUMER_DATABASE = TestDatabaseFactory.getTestDatabase(2);

    private ConnectorRemote providerConnector;
    private ConnectorRemote consumerConnector;

    private EdcClient providerClient;
    private EdcClient consumerClient;

    private final int port = getFreePort();
    private final String sourcePath = "/source/some/path/";
    private final String destinationPath = "/destination/some/path/";
    private final String sourceUrl = "http://localhost:" + port + sourcePath;
    private final String destinationUrl = "http://localhost:" + port + destinationPath;
    private ClientAndServer mockServer;

    private static final AtomicInteger DATA_OFFER_INDEX = new AtomicInteger(0);

    private final IdentityService identityService = mock(IdentityService.class);

    private ConnectorConfig providerConfig;
    private ConnectorConfig consumerConfig;

    @BeforeEach
    public void startServer() {
        mockServer = ClientAndServer.startClientAndServer(port);
    }

    @AfterEach
    public void stopServer() {
        stopQuietly(mockServer);
    }

    @BeforeEach
    void setup() {
        // set up provider EDC + Client
        providerConfig = forTestDatabase(PROVIDER_PARTICIPANT_ID, 34000, PROVIDER_DATABASE);
        PROVIDER_EDC_CONTEXT.setConfiguration(providerConfig.getProperties());
        providerConnector = new ConnectorRemote(fromConnectorConfig(providerConfig));

        providerClient = EdcClient.builder()
            .managementApiUrl(providerConfig.getManagementEndpoint().getUri().toString())
            .managementApiKey(providerConfig.getProperties().get("edc.api.auth.key"))
            .build();

        // set up consumer EDC + Client
        consumerConfig = forTestDatabase(CONSUMER_PARTICIPANT_ID, CONSUMER_DATABASE);
        CONSUMER_EDC_CONTEXT.setConfiguration(consumerConfig.getProperties());
        consumerConnector = new ConnectorRemote(fromConnectorConfig(consumerConfig));

        consumerClient = EdcClient.builder()
            .managementApiUrl(consumerConfig.getManagementEndpoint().getUri().toString())
            .managementApiKey(consumerConfig.getProperties().get("edc.api.auth.key"))
            .build();
    }

    @Test
    @SneakyThrows
    void canGetContractAgreementPageForNonTerminatedContract() {
        // arrange

        val assetId = "asset-0";
        val policyId = "policy-0";
        createAsset(assetId);
        createPolicyDefinition(policyId);
        createContractDefinition(policyId, assetId);

        val offers = consumerClient.uiApi().getCatalogPageDataOffers(providerConfig.getProtocolEndpoint().getUri().toString());
        val firstOffer = offers.get(0);
        val firstContractOffer = firstOffer.getContractOffers().get(0);
        val initialNegotiation = initiateNegotiation(firstOffer, firstContractOffer);
        awaitNegotiationDone(initialNegotiation.getContractNegotiationId());

        // act
        // don't terminate the contract
        val agreements = consumerClient.uiApi().getContractAgreementPage(ContractAgreementPageQuery.builder().build());

        // assert
        val contractAgreements = agreements.getContractAgreements();
        assertThat(contractAgreements).hasSize(1);
        assertThat(contractAgreements.get(0).getTerminationStatus()).isEqualTo(ONGOING);
        val information = contractAgreements.get(0).getTerminationInformation();
        assertThat(information).isNull();
    }

    @Test
    @SneakyThrows
    void canTerminateContractFromConsumer() {
        // arrange

        val assetId = "asset-1";
        val policyId = "policy-1";
        createAsset(assetId);
        createPolicyDefinition(policyId);
        createContractDefinition(policyId, assetId);

        val offers = consumerClient.uiApi().getCatalogPageDataOffers(providerConfig.getProtocolEndpoint().getUri().toString());
        val firstOffer = offers.get(0);
        val firstContractOffer = firstOffer.getContractOffers().get(0);
        val initialNegotiation = initiateNegotiation(firstOffer, firstContractOffer);
        val negotiation = awaitNegotiationDone(initialNegotiation.getContractNegotiationId());

        // act
        val detail = "Some detail";
        val reason = "Some reason";
        consumerClient.uiApi().terminateContractAgreement(negotiation.getContractAgreementId(), ContractTerminationRequest.builder()
            .detail(detail)
            .reason(reason)
            .build());

        awaitTerminationCount(consumerClient, 1);
        awaitTerminationCount(providerClient, 1);

        val consumerSideAgreements = consumerClient.uiApi().getContractAgreementPage(ContractAgreementPageQuery.builder().build());
        val providerSideAgreements = providerClient.uiApi().getContractAgreementPage(ContractAgreementPageQuery.builder().build());

        // assert
        assertTermination(consumerSideAgreements, detail, reason, SELF);
        assertTermination(providerSideAgreements, detail, reason, COUNTERPARTY);
    }

    @Test
    void limitTheReasonSizeAt100Chars() {
        // arrange

        val assetId = "asset-1";
        val policyId = "policy-1";
        createAsset(assetId);
        createPolicyDefinition(policyId);
        createContractDefinition(policyId, assetId);

        val connectorEndpoint = providerConfig.getProtocolEndpoint().getUri().toString();
        val offers = consumerClient.uiApi().getCatalogPageDataOffers(connectorEndpoint);
        val firstOffer = offers.get(0);
        val firstContractOffer = firstOffer.getContractOffers().get(0);
        val initialNegotiation = initiateNegotiation(firstOffer, firstContractOffer);
        val negotiation = awaitNegotiationDone(initialNegotiation.getContractNegotiationId());

        // act
        val detail = "Some detail";
        val max = MAX_REASON_SIZE;
        val maxSize = IntStream.range(0, max).mapToObj(it -> "M").reduce("", (acc, e) -> acc + e);
        val tooLong = IntStream.range(0, max + 1).mapToObj(it -> "O").reduce("", (acc, e) -> acc + e);

        // assert when too big

        assertThrows(
            ApiException.class,
            () -> consumerClient.uiApi()
                .terminateContractAgreement(negotiation.getContractAgreementId(), ContractTerminationRequest.builder()
                    .detail(detail)
                    .reason(tooLong)
                    .build())
        );

        // assert when max size

        consumerClient.uiApi().terminateContractAgreement(negotiation.getContractAgreementId(), ContractTerminationRequest.builder()
            .detail(detail)
            .reason(maxSize)
            .build());

        awaitTerminationCount(consumerClient, 1);
        awaitTerminationCount(providerClient, 1);

        // termination completed == success
    }

    @Test
    void limitTheDetailSizeAt1000Chars() {
        // arrange

        val assetId = "asset-1";
        val policyId = "policy-1";
        createAsset(assetId);
        createPolicyDefinition(policyId);
        createContractDefinition(policyId, assetId);

        val connectorEndpoint = providerConfig.getProtocolEndpoint().getUri().toString();
        val offers = consumerClient.uiApi().getCatalogPageDataOffers(connectorEndpoint);
        val firstOffer = offers.get(0);
        val firstContractOffer = firstOffer.getContractOffers().get(0);
        val initialNegotiation = initiateNegotiation(firstOffer, firstContractOffer);
        val negotiation = awaitNegotiationDone(initialNegotiation.getContractNegotiationId());

        // act
        val reason = "Some reason";
        val max = MAX_DETAIL_SIZE;
        val maxSize = IntStream.range(0, max).mapToObj(it -> "M").reduce("", (acc, e) -> acc + e);
        val tooLong = IntStream.range(0, max + 1).mapToObj(it -> "O").reduce("", (acc, e) -> acc + e);

        // assert when too big

        assertThrows(
            ApiException.class,
            () -> consumerClient.uiApi()
                .terminateContractAgreement(negotiation.getContractAgreementId(), ContractTerminationRequest.builder()
                    .detail(tooLong)
                    .reason(reason)
                    .build())
        );

        // assert when max size

        consumerClient.uiApi().terminateContractAgreement(negotiation.getContractAgreementId(), ContractTerminationRequest.builder()
            .detail(maxSize)
            .reason(reason)
            .build());

        awaitTerminationCount(consumerClient, 1);
        awaitTerminationCount(providerClient, 1);

        // termination completed == success
    }

    private static void assertTermination(ContractAgreementPage consumerSideAgreements, String detail, String reason,
                                          ContractTerminatedBy terminatedBy) {
        // TODO: it is not mentioned which side terminated the contract. Should it be added?
        // TODO: why do we have 2 different enum to represent the same kind of data. This is confusing...

        val now = OffsetDateTime.now();

        assertThat(consumerSideAgreements.getContractAgreements()).hasSize(1);
        assertThat(consumerSideAgreements.getContractAgreements().get(0).getTerminationStatus()).isEqualTo(TERMINATED);
        val consumerInformation = consumerSideAgreements.getContractAgreements().get(0).getTerminationInformation();
        assertThat(consumerInformation).isNotNull();
        assertThat(consumerInformation.getTerminatedAt()).isBetween(now.minusMinutes(1), now.plusMinutes(1));
        assertThat(consumerInformation.getDetail()).isEqualTo(detail);
        assertThat(consumerInformation.getReason()).isEqualTo(reason);
        assertThat(consumerInformation.getTerminatedBy()).isEqualTo(terminatedBy);
    }

    @Test
    @SneakyThrows
    void canTerminateContractFromProvider() {
        // arrange

        val assetId = "asset-1";
        val policyId = "policy-1";
        createAsset(assetId);
        createPolicyDefinition(policyId);
        createContractDefinition(policyId, assetId);

        val offers = consumerClient.uiApi().getCatalogPageDataOffers(providerConfig.getProtocolEndpoint().getUri().toString());
        val firstOffer = offers.get(0);
        val firstContractOffer = firstOffer.getContractOffers().get(0);
        val initialNegotiation = initiateNegotiation(firstOffer, firstContractOffer);
        val negotiation = awaitNegotiationDone(initialNegotiation.getContractNegotiationId());

        // act
        val detail = "Some detail";
        val reason = "Some reason";

        providerClient.uiApi().terminateContractAgreement(
            negotiation.getContractAgreementId(),
            ContractTerminationRequest.builder()
                .detail(detail)
                .reason(reason)
                .build());

        awaitTerminationCount(consumerClient, 1);
        awaitTerminationCount(providerClient, 1);

        val consumerSideAgreements = consumerClient.uiApi().getContractAgreementPage(ContractAgreementPageQuery.builder().build());
        val providerSideAgreements = providerClient.uiApi().getContractAgreementPage(ContractAgreementPageQuery.builder().build());

        // assert

        // assert
        assertTermination(consumerSideAgreements, detail, reason, COUNTERPARTY);
        assertTermination(providerSideAgreements, detail, reason, SELF);
    }

    // TODO: try to cancel a contract agreement that doesn't exist
    // TODO: test who cancelled the contract
    // TODO: assert max length for detail to at least 500
    // TODO: test that the contract can't be cancelled twice

    // TODO: group those helpers

    private void createAsset(String assetId) {
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("http://example.com")
                .build())
            .build();

        providerClient.uiApi()
            .createAsset(UiAssetCreateRequest.builder()
                .id(assetId)
                .title("AssetName")
                .version("1.0.0")
                .language("en")
                .dataSource(dataSource)
                .build())
            .getId();
    }

    private String createPolicyDefinition(String policyId) {
        var policyDefinition = PolicyDefinitionCreateRequest.builder()
            .policyDefinitionId(policyId)
            .policy(UiPolicyCreateRequest.builder()
                .constraints(List.of())
                .build()
            )
            .build();

        return providerClient.uiApi().createPolicyDefinition(policyDefinition).getId();
    }

    public void createContractDefinition(String policyId, String assetId) {
        providerClient.uiApi().createContractDefinition(ContractDefinitionRequest.builder()
            .contractDefinitionId("cd-1")
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

    private void awaitTerminationCount(EdcClient client, int count) {
        Awaitility.await().atMost(ofSeconds(5)).until(
            () -> client.uiApi()
                .getContractAgreementPage(ContractAgreementPageQuery.builder().build())
                .getContractAgreements()
                .size() >= count
        );
    }
}
