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
import de.sovity.edc.client.gen.model.UiDataSource;
import de.sovity.edc.client.gen.model.UiDataSourceHttpData;
import de.sovity.edc.client.gen.model.UiPolicyCreateRequest;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseFactory;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.ws.rs.HttpMethod;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.http.HttpStatus;
import org.awaitility.Awaitility;
import org.eclipse.edc.connector.contract.spi.ContractId;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcessStates;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.iam.IdentityService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static de.sovity.edc.client.gen.model.ContractTerminatedBy.COUNTERPARTY;
import static de.sovity.edc.client.gen.model.ContractTerminatedBy.SELF;
import static de.sovity.edc.client.gen.model.ContractTerminationStatus.ONGOING;
import static de.sovity.edc.client.gen.model.ContractTerminationStatus.TERMINATED;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;
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
        providerConfig = forTestDatabase(PROVIDER_PARTICIPANT_ID, PROVIDER_DATABASE);
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
    void canGetAgreementPageForNonTerminatedContract() {

        String asset1 = "a-1";
        String asset2 = "a-2";
        String asset3 = "a-3";

        val alwaysTrue = createPolicyDefinition("p-0");

        createAssetAndContractDef(asset1, alwaysTrue);
        createAssetAndContractDef(asset2, alwaysTrue);
        createAssetAndContractDef(asset3, alwaysTrue);

        val neg0 = initiateNegotiationForContract(asset1);
        val neg1 = initiateNegotiationForContract(asset2);
        val neg2 = initiateNegotiationForContract(asset3);

        val agreement0 = awaitNegotiationDone(consumerClient, neg0.getContractNegotiationId());
        val agreement1 = awaitNegotiationDone(consumerClient, neg1.getContractNegotiationId());
        val agreement2 = awaitNegotiationDone(consumerClient, neg2.getContractNegotiationId());

        consumerClient.uiApi().terminateContractAgreement(
            agreement0.getContractAgreementId(),
            ContractTerminationRequest.builder()
                .detail("detail")
                .reason("reason")
                .build()
        );

        consumerClient.uiApi().terminateContractAgreement(
            agreement1.getContractAgreementId(),
            ContractTerminationRequest.builder()
                .detail("detail")
                .reason("reason")
                .build()
        );

        awaitTerminationCount(consumerClient, 2);
        awaitTerminationCount(providerClient, 2);

        // act
        // don't terminate the contract
        val agreements = consumerClient.uiApi().getContractAgreementPage(ContractAgreementPageQuery.builder().build());
        val terminatedAgreements = consumerClient.uiApi().getContractAgreementPage(ContractAgreementPageQuery.builder().terminationStatus(TERMINATED).build());
        val ongoingAgreements = consumerClient.uiApi().getContractAgreementPage(ContractAgreementPageQuery.builder().terminationStatus(ONGOING).build());

        // assert
        val contractAgreements = agreements.getContractAgreements();
        assertThat(contractAgreements).hasSize(3);

        // TODO: pull this into some kind of Scenario class with all the facilities to make these calls short

        assertThat(agreements.getContractAgreements()).hasSize(3);
        assertThat(terminatedAgreements.getContractAgreements()).hasSize(2);
        assertThat(ongoingAgreements.getContractAgreements()).hasSize(1);
    }

    @Test
    @SneakyThrows
    void canGetAgreementPageForTerminatedContract() {
        arrange();

        // act
        val agreements = consumerClient.uiApi().getContractAgreementPage(ContractAgreementPageQuery.builder().build());

        String reason = "Reason";
        String details = "Details";
        consumerClient.uiApi().terminateContractAgreement(
            agreements.getContractAgreements().get(0).getContractAgreementId(),
            ContractTerminationRequest.builder().reason(reason).detail(details).build());

        awaitTerminationCount(consumerClient, 1);
        awaitTerminationCount(providerClient, 1);

        val agreementsAfterTermination = consumerClient.uiApi().getContractAgreementPage(ContractAgreementPageQuery.builder().build());

        // assert
        assertTermination(agreementsAfterTermination, details, reason, SELF);
    }

    @Test
    @SneakyThrows
    void canTerminateFromConsumer() {
        val negotiation = arrange();

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
        val negotiation = arrange();

        // act
        val detail = "Some detail";
        val max = 100;
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
        val negotiation = arrange();

        // act
        val reason = "Some reason";
        val max = 1000;
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

    @Test
    @SneakyThrows
    void canTerminateFromProvider() {
        val negotiation = arrange();

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

    @Test
    void doesntCrashWhenAgreementDoesntExist() {
        // act
        val exception = assertThrows(
            ApiException.class,
            () -> consumerClient.uiApi().terminateContractAgreement(
                ContractId.create("definition-1", "asset-1").toString(),
                ContractTerminationRequest.builder().detail("Some detail").reason("Some reason").build()));

        assertThat(exception.getCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    void canTerminateOnlyOnce() {
        val negotiation = arrange();

        val detail = "Some detail";
        val reason = "Some reason";
        val contractTerminationRequest = ContractTerminationRequest.builder().detail(detail).reason(reason).build();
        val contractAgreementId = negotiation.getContractAgreementId();
        consumerClient.uiApi().terminateContractAgreement(contractAgreementId, contractTerminationRequest);

        awaitTerminationCount(consumerClient, 1);
        awaitTerminationCount(providerClient, 1);

        // act

        val exception = assertThrows(
            ApiException.class,
            () -> consumerClient.uiApi().terminateContractAgreement(contractAgreementId, contractTerminationRequest));

        assertThat(exception.getCode()).isEqualTo(HttpStatus.SC_NOT_MODIFIED);
    }

    @Test
    void cantTransferDataAfterTerminated() throws InterruptedException {
        arrange();

        val resourceAccessed = new AtomicBoolean(false);
        mockServer.when(HttpRequest.request(sourcePath).withMethod("GET")).respond(it -> {
            resourceAccessed.set(true);
            return HttpResponse.response().withStatusCode(200);
        });
        mockServer.when(HttpRequest.request(destinationPath).withMethod("POST")).respond(it -> HttpResponse.response().withStatusCode(200));

        mockServer.when(HttpRequest.request()).respond(it -> HttpResponse.response());

        val offers = consumerClient.uiApi().getCatalogPageDataOffers(providerConfig.getProtocolEndpoint().getUri().toString());
        val firstOffer = offers.get(0);
        val firstContractOffer = firstOffer.getContractOffers().get(0);
        val initialNegotiation = initiateNegotiation(firstOffer, firstContractOffer);
        val negotiation = awaitNegotiationDone(consumerClient, initialNegotiation.getContractNegotiationId());

        val transferRequest = InitiateTransferRequest.builder()
            .contractAgreementId(negotiation.getContractAgreementId())
            .dataSinkProperties(
                Map.of(
                    EDC_NAMESPACE + "baseUrl", destinationUrl,
                    EDC_NAMESPACE + "method", HttpMethod.POST,
                    EDC_NAMESPACE + "type", "HttpData"
                )
            )
            .build();

        val initTransfer = consumerClient.uiApi().initiateTransfer(transferRequest);
        val historyEntry = awaitTransfer(consumerClient, initTransfer.getId());

        assertThat(historyEntry.getState().getCode()).isEqualTo(TransferProcessStates.COMPLETED.code());
        assertThat(resourceAccessed.get()).isTrue();

        resourceAccessed.set(false);

        val detail = "Some detail";
        val reason = "Some reason";
        val contractTerminationRequest = ContractTerminationRequest.builder().detail(detail).reason(reason).build();
        val contractAgreementId = negotiation.getContractAgreementId();
        // TODO: Preventing data transfer only makes sense when the provider terminates the contract?
        //  No need to test in the opposite direction?
        providerClient.uiApi().terminateContractAgreement(contractAgreementId, contractTerminationRequest);

        awaitTerminationCount(consumerClient, 1);
        awaitTerminationCount(providerClient, 1);

        // act
        consumerClient.uiApi().initiateTransfer(transferRequest);
        Thread.sleep(10_000);
        assertThat(resourceAccessed.get()).isFalse();
        Thread.sleep(10_000);
        assertThat(resourceAccessed.get()).isFalse();
    }

    private static void assertTermination(
        ContractAgreementPage consumerSideAgreements,
        String detail,
        String reason,
        ContractTerminatedBy terminatedBy) {

        val contractAgreements = consumerSideAgreements.getContractAgreements();
        assertThat(contractAgreements).hasSize(1);
        assertThat(contractAgreements.get(0).getTerminationStatus()).isEqualTo(TERMINATED);

        val consumerInformation = contractAgreements.get(0).getTerminationInformation();

        assertThat(consumerInformation).isNotNull();

        val now = OffsetDateTime.now();
        assertThat(consumerInformation.getTerminatedAt()).isBetween(now.minusMinutes(1), now.plusMinutes(1));

        assertThat(consumerInformation.getDetail()).isEqualTo(detail);
        assertThat(consumerInformation.getReason()).isEqualTo(reason);
        assertThat(consumerInformation.getTerminatedBy()).isEqualTo(terminatedBy);
    }

    private TransferHistoryEntry awaitTransfer(EdcClient client, String transferProcessId) {
        val historyEntry = Awaitility.await().atMost(10, SECONDS).until(() ->
                client.uiApi()
                    .getTransferHistoryPage()
                    .getTransferEntries()
                    .stream()
                    .filter(it -> it.getTransferProcessId().equals(transferProcessId))
                    .findFirst(),
            first -> first.map(it -> it.getState().getCode().equals(TransferProcessStates.COMPLETED.code()))
                .orElse(false));

        return historyEntry.get();
    }

    // TODO: group those helpers

    private @NotNull UiContractNegotiation arrange() {
        val assetId = "asset-1";
        val policyId = "policy-1";

        createPolicyDefinition(policyId);
        createAssetAndContractDef(assetId, policyId);

        val initialNegotiation = initiateNegotiationForContract(assetId);

        return awaitNegotiationDone(consumerClient, initialNegotiation.getContractNegotiationId());
    }

    private void createAssetAndContractDef(String assetId, String policyId) {
        createAsset(assetId);
        createContractDefinition(policyId, assetId);
    }

    private UiContractNegotiation initiateNegotiationForContract(String assetId) {
        val connectorEndpoint = providerConfig.getProtocolEndpoint().getUri().toString();
        val offers = consumerClient.uiApi().getCatalogPageDataOffers(connectorEndpoint);

        val offersContainingContract = offers.stream()
            .filter(offer -> offer.getAsset().getAssetId().equals(assetId))
            .toList();

        assertThat(offersContainingContract).hasSize(1);

        val firstContractOffer = offersContainingContract.get(0).getContractOffers().get(0);
        return initiateNegotiation(offersContainingContract.get(0), firstContractOffer);
    }

    private void createAsset(String assetId) {
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl(sourceUrl)
                .build())
            .build();

        providerClient.uiApi()
            .createAsset(UiAssetCreateRequest.builder()
                .id(assetId)
                .title("AssetName " + assetId)
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

    private UiContractNegotiation awaitNegotiationDone(EdcClient client, String negotiationId) {
        var negotiation = Awaitility.await().atMost(consumerConnector.timeout).until(
            () -> client.uiApi().getContractNegotiation(negotiationId),
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
                .stream()
                .filter(it -> it.getTerminationStatus().equals(TERMINATED))
                .count() >= count
        );
    }
}
