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
import de.sovity.edc.client.gen.model.ContractTerminatedBy;
import de.sovity.edc.client.gen.model.ContractTerminationRequest;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.client.gen.model.TransferHistoryEntry;
import de.sovity.edc.e2e.utils.Consumer;
import de.sovity.edc.e2e.utils.E2eScenario;
import de.sovity.edc.e2e.utils.E2eTestExtension;
import de.sovity.edc.e2e.utils.Provider;
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import jakarta.ws.rs.HttpMethod;
import lombok.SneakyThrows;
import lombok.val;
import org.awaitility.Awaitility;
import org.eclipse.edc.connector.contract.spi.ContractId;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcessStates;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.IntStream;

import static de.sovity.edc.client.gen.model.ContractTerminatedBy.COUNTERPARTY;
import static de.sovity.edc.client.gen.model.ContractTerminatedBy.SELF;
import static de.sovity.edc.client.gen.model.ContractTerminationStatus.ONGOING;
import static de.sovity.edc.client.gen.model.ContractTerminationStatus.TERMINATED;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;
import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockserver.stop.Stop.stopQuietly;

@ExtendWith(E2eTestExtension.class)
public class ContractTerminationTest {

    private ClientAndServer mockServer;


    @BeforeEach
    public void startServer() {
        mockServer = ClientAndServer.startClientAndServer(getFreePort());
    }

    @AfterEach
    public void stopServer() {
        stopQuietly(mockServer);
    }

    @Test
    void canGetAgreementPageForNonTerminatedContract(
        E2eScenario scenario,
        @Consumer EdcClient consumerClient,
        @Provider EdcClient providerClient) {

        val assets = IntStream.range(0, 3).mapToObj((it) -> scenario.createAsset());

        val agreements = assets
            .peek(scenario::createContractDefinition)
            .map(scenario::negotiateAsset)
            .toList();

        consumerClient.uiApi().terminateContractAgreement(
            agreements.get(0).getContractAgreementId(),
            ContractTerminationRequest.builder()
                .detail("detail 0")
                .reason("reason 0")
                .build()
        );

        consumerClient.uiApi().terminateContractAgreement(
            agreements.get(1).getContractAgreementId(),
            ContractTerminationRequest.builder()
                .detail("detail 1")
                .reason("reason 1")
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
        assertThat(allAgreements.getContractAgreements()).hasSize(3);
        assertThat(terminatedAgreements.getContractAgreements()).hasSize(2);
        assertThat(ongoingAgreements.getContractAgreements()).hasSize(1);
    }

    @DisabledOnGithub
    @Test
    @SneakyThrows
    void canGetAgreementPageForTerminatedContract(
        E2eScenario scenario,
        @Consumer EdcClient consumerClient,
        @Provider EdcClient providerClient) {

        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        scenario.negotiateAsset(assetId);

        val agreements = consumerClient.uiApi().getContractAgreementPage(ContractAgreementPageQuery.builder().build());

        // act

        val reason = "Reason";
        val details = "Details";
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
    void canTerminateFromConsumer(
        E2eScenario scenario,
        @Consumer EdcClient consumerClient,
        @Provider EdcClient providerClient) {

        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAsset(assetId);

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

    @DisabledOnGithub
    @Test
    void limitTheReasonSizeAt100Chars(
        E2eScenario scenario,
        @Consumer EdcClient consumerClient,
        @Provider EdcClient providerClient) {

        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAsset(assetId);

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
    void limitTheDetailSizeAt1000Chars(
        E2eScenario scenario,
        @Consumer EdcClient consumerClient,
        @Provider EdcClient providerClient) {

        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAsset(assetId);

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
    void canTerminateFromProvider(
        E2eScenario scenario,
        @Consumer EdcClient consumerClient,
        @Provider EdcClient providerClient) {

        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAsset(assetId);

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
    void doesntCrashWhenAgreementDoesntExist(
        @Consumer EdcClient consumerClient) {
        // act
        assertThrows(
            ApiException.class,
            () -> consumerClient.uiApi().terminateContractAgreement(
                ContractId.create("definition-1", "asset-1").toString(),
                ContractTerminationRequest.builder().detail("Some detail").reason("Some reason").build()));
    }

    @DisabledOnGithub
    @Test
    @SneakyThrows
    void cantTransferDataAfterTerminated(
        E2eScenario scenario,
        @Consumer EdcClient consumerClient,
        @Provider EdcClient providerClient) {

        val assetId = "asset-1";
        val mockedAsset = scenario.createAssetWithMockResource(assetId, mockServer);
        scenario.createContractDefinition(assetId);
        scenario.negotiateAsset(assetId);

        val destinationPath = "/destination/some/path/";
        val destinationUrl = "http://localhost:" + mockServer.getPort() + destinationPath;
        mockServer.when(HttpRequest.request(destinationPath).withMethod("POST")).respond(it -> HttpResponse.response().withStatusCode(200));

        val negotiation = scenario.negotiateAsset(assetId);

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

        val transferId = scenario.transferAndAwait(transferRequest);

        val historyEntry = consumerClient.uiApi()
            .getTransferHistoryPage()
            .getTransferEntries()
            .stream()
            .filter(it -> it.getTransferProcessId().equals(transferId))
            .findFirst()
            .get();

        assertThat(historyEntry.getState().getCode()).isEqualTo(TransferProcessStates.COMPLETED.code());
        assertThat(mockedAsset.accesses().get()).isGreaterThan(0);

        mockedAsset.accesses().set(0);

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
        // first transfer attempt
        Thread.sleep(10_000);
        assertThat(mockedAsset.accesses().get()).isEqualTo(0);
        // second transfer attempt
        Thread.sleep(10_000);
        assertThat(mockedAsset.accesses().get()).isEqualTo(0);
    }

    @Test
    void canTerminateOnlyOnce(
        E2eScenario scenario,
        @Consumer EdcClient consumerClient,
        @Provider EdcClient providerClient) {

        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAsset(assetId);

        val detail = "Some detail";
        val reason = "Some reason";
        val contractTerminationRequest = ContractTerminationRequest.builder().detail(detail).reason(reason).build();
        val contractAgreementId = negotiation.getContractAgreementId();
        val firstTermination = consumerClient.uiApi().terminateContractAgreement(contractAgreementId, contractTerminationRequest);

        awaitTerminationCount(consumerClient, 1);
        awaitTerminationCount(providerClient, 1);

        // act

        val alreadyExists = consumerClient.uiApi().terminateContractAgreement(contractAgreementId, contractTerminationRequest);

        assertThat(alreadyExists.getLastUpdatedDate()).isEqualTo(firstTermination.getLastUpdatedDate());
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
