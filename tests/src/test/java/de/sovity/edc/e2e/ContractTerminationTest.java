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

package de.sovity.edc.e2e;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.ApiException;
import de.sovity.edc.client.gen.model.ContractAgreementPage;
import de.sovity.edc.client.gen.model.ContractAgreementPageQuery;
import de.sovity.edc.client.gen.model.ContractNegotiationRequest;
import de.sovity.edc.client.gen.model.ContractNegotiationSimplifiedState;
import de.sovity.edc.client.gen.model.ContractTerminatedBy;
import de.sovity.edc.client.gen.model.ContractTerminationRequest;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.client.gen.model.TransferHistoryEntry;
import de.sovity.edc.client.gen.model.UiContractNegotiation;
import de.sovity.edc.client.gen.model.UiContractOffer;
import de.sovity.edc.client.gen.model.UiDataOffer;
import de.sovity.edc.e2e.utils.Scenario;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.db.EdcRuntimeExtensionWithTestDatabase;
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import jakarta.ws.rs.HttpMethod;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.http.HttpStatus;
import org.awaitility.Awaitility;
import org.eclipse.edc.connector.contract.spi.ContractId;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcessStates;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.sovity.edc.client.gen.model.ContractTerminatedBy.COUNTERPARTY;
import static de.sovity.edc.client.gen.model.ContractTerminatedBy.SELF;
import static de.sovity.edc.client.gen.model.ContractTerminationStatus.ONGOING;
import static de.sovity.edc.client.gen.model.ContractTerminationStatus.TERMINATED;
import static de.sovity.edc.e2e.utils.AwaitNegotiationPolicy.AWAIT;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockserver.stop.Stop.stopQuietly;

public class ContractTerminationTest {

    private static final String CONSUMER_PARTICIPANT_ID = "consumer";
    private static ConnectorConfig consumerConfig;
    private static EdcClient consumerClient;

    @RegisterExtension
    static EdcRuntimeExtensionWithTestDatabase consumerExtension = new EdcRuntimeExtensionWithTestDatabase(
        ":launchers:connectors:sovity-dev",
        "consumer",
        testDatabase -> {
            consumerConfig = forTestDatabase(CONSUMER_PARTICIPANT_ID, testDatabase);
            consumerClient = EdcClient.builder()
                .managementApiUrl(consumerConfig.getManagementEndpoint().getUri().toString())
                .managementApiKey(consumerConfig.getProperties().get("edc.api.auth.key"))
                .build();
            return consumerConfig.getProperties();
        }
    );


    private static final String PROVIDER_PARTICIPANT_ID = "provider";
    private static ConnectorConfig providerConfig;
    private static EdcClient providerClient;

    @RegisterExtension
    static EdcRuntimeExtensionWithTestDatabase providerExtension = new EdcRuntimeExtensionWithTestDatabase(
        ":launchers:connectors:sovity-dev",
        "provider",
        testDatabase -> {
            providerConfig = forTestDatabase(PROVIDER_PARTICIPANT_ID, testDatabase);
            providerClient = EdcClient.builder()
                .managementApiUrl(providerConfig.getManagementEndpoint().getUri().toString())
                .managementApiKey(providerConfig.getProperties().get("edc.api.auth.key"))
                .build();
            return providerConfig.getProperties();
        }
    );

    private ClientAndServer mockServer;
    private final int port = getFreePort();
    private final String destinationPath = "/destination/some/path/";
    private final String destinationUrl = "http://localhost:" + port + destinationPath;


    @BeforeEach
    public void startServer() {
        mockServer = ClientAndServer.startClientAndServer(port);
    }

    @AfterEach
    public void stopServer() {
        stopQuietly(mockServer);
    }

    @Test
    void canGetAgreementPageForNonTerminatedContract() {

        val scenario = new Scenario(consumerClient, consumerConfig, providerClient, providerConfig);

        val assets = Stream.of("a-1", "a-2", "a-3");

        val agreements = assets.map(scenario::createAsset)
            .peek(scenario::createContractDefinition)
            .map(it -> scenario.negotiateAsset(it, AWAIT))
            .toList();

        consumerClient.uiApi().terminateContractAgreement(
            agreements.get(0).getContractAgreementId(),
            ContractTerminationRequest.builder()
                .detail("detail")
                .reason("reason")
                .build()
        );

        consumerClient.uiApi().terminateContractAgreement(
            agreements.get(1).getContractAgreementId(),
            ContractTerminationRequest.builder()
                .detail("detail")
                .reason("reason")
                .build()
        );

        awaitTerminationCount(consumerClient, 2);
        awaitTerminationCount(providerClient, 2);

        // act
        // don't terminate the contract
        val allAgreements = consumerClient.uiApi().getContractAgreementPage(ContractAgreementPageQuery.builder().build());
        val terminatedAgreements =
            consumerClient.uiApi().getContractAgreementPage(ContractAgreementPageQuery.builder().terminationStatus(TERMINATED).build());
        val ongoingAgreements =
            consumerClient.uiApi().getContractAgreementPage(ContractAgreementPageQuery.builder().terminationStatus(ONGOING).build());

        // assert
        val contractAgreements = allAgreements.getContractAgreements();
        assertThat(contractAgreements).hasSize(3);

        assertThat(allAgreements.getContractAgreements()).hasSize(3);
        assertThat(terminatedAgreements.getContractAgreements()).hasSize(2);
        assertThat(ongoingAgreements.getContractAgreements()).hasSize(1);
    }

    @DisabledOnGithub
    @Test
    @SneakyThrows
    void canGetAgreementPageForTerminatedContract() {

        val scenario = new Scenario(consumerClient, consumerConfig, providerClient, providerConfig);

        val assetId = "asset-1";
        scenario.createAsset(assetId);
        scenario.createContractDefinition(assetId);
        scenario.negotiateAsset(assetId, AWAIT);

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

        val scenario = new Scenario(consumerClient, consumerConfig, providerClient, providerConfig);

        val assetId = "asset-1";

        scenario.createAsset(assetId);
        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAsset(assetId, AWAIT);

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

        val scenario = new Scenario(consumerClient, consumerConfig, providerClient, providerConfig);

        val assetId = "asset-1";

        scenario.createAsset(assetId);
        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAsset(assetId, AWAIT);

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

        val scenario = new Scenario(consumerClient, consumerConfig, providerClient, providerConfig);

        val assetId = "asset-1";

        scenario.createAsset(assetId);
        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAsset(assetId, AWAIT);

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

        val scenario = new Scenario(consumerClient, consumerConfig, providerClient, providerConfig);

        val assetId = "asset-1";

        scenario.createAsset(assetId);
        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAsset(assetId, AWAIT);

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

        val scenario = new Scenario(consumerClient, consumerConfig, providerClient, providerConfig);

        val assetId = "asset-1";

        scenario.createAsset(assetId);
        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAsset(assetId, AWAIT);

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

    @DisabledOnGithub
    @Test
    void cantTransferDataAfterTerminated() throws InterruptedException {

        val scenario = new Scenario(consumerClient, consumerConfig, providerClient, providerConfig);

        val assetId = "asset-1";
        val mockedAsset = scenario.createAssetWithMockResource(assetId, mockServer);
        scenario.createContractDefinition(assetId);
        scenario.negotiateAsset(assetId, AWAIT);

        mockServer.when(HttpRequest.request(destinationPath).withMethod("POST")).respond(it -> HttpResponse.response().withStatusCode(200));

        val negotiation = scenario.negotiateAsset(assetId, AWAIT);

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
        assertThat(mockedAsset.accessed().get()).isTrue();

        mockedAsset.accessed().set(false);

        val detail = "Some detail";
        val reason = "Some reason";
        val contractTerminationRequest = ContractTerminationRequest.builder().detail(detail).reason(reason).build();
        val contractAgreementId = negotiation.getContractAgreementId();

        // only testing the provider's cancellation as this is the party that is concerned by data access
        providerClient.uiApi().terminateContractAgreement(contractAgreementId, contractTerminationRequest);

        awaitTerminationCount(consumerClient, 1);
        awaitTerminationCount(providerClient, 1);

        // act
        consumerClient.uiApi().initiateTransfer(transferRequest);
        Thread.sleep(10_000);
        assertThat(mockedAsset.accessed().get()).isFalse();
        Thread.sleep(10_000);
        assertThat(mockedAsset.accessed().get()).isFalse();
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


    public static UiContractNegotiation initiateNegotiation(UiDataOffer dataOffer, UiContractOffer contractOffer) {
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
        var negotiation = Awaitility.await().atMost(ofSeconds(5)).until(
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
