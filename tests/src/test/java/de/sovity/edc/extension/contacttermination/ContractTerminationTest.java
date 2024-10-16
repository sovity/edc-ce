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

package de.sovity.edc.extension.contacttermination;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.ApiException;
import de.sovity.edc.client.gen.model.ContractAgreementPageQuery;
import de.sovity.edc.client.gen.model.ContractTerminationRequest;
import de.sovity.edc.client.gen.model.InitiateTransferRequest;
import de.sovity.edc.extension.e2e.connector.remotes.api_wrapper.E2eTestScenario;
import de.sovity.edc.extension.e2e.junit.CeE2eTestExtension;
import de.sovity.edc.extension.e2e.junit.utils.Consumer;
import de.sovity.edc.extension.e2e.junit.utils.Provider;
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.ws.rs.HttpMethod;
import lombok.SneakyThrows;
import lombok.val;
import org.eclipse.edc.connector.controlplane.contract.spi.ContractOfferId;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcessStates;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static de.sovity.edc.client.gen.model.ContractTerminationStatus.ONGOING;
import static de.sovity.edc.client.gen.model.ContractTerminationStatus.TERMINATED;
import static de.sovity.edc.extension.contacttermination.ContractTerminationTestUtils.awaitTerminationCount;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class ContractTerminationTest {

    @RegisterExtension
    private final static CeE2eTestExtension e2eTestExtension = CeE2eTestExtension.builder()
        .additionalModule(":launchers:connectors:sovity-dev")
        .build();

    @DisabledOnGithub
    @Test
    void limitTheReasonSizeAt100Chars(
        E2eTestScenario scenario,
        @Consumer EdcClient consumerClient,
        @Provider EdcClient providerClient
    ) {
        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAssetAndAwait(assetId);

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

    @DisabledOnGithub
    @Test
    void limitTheDetailSizeAt1000Chars(
        E2eTestScenario scenario,
        @Consumer EdcClient consumerClient,
        @Provider EdcClient providerClient
    ) {
        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAssetAndAwait(assetId);

        // act
        val reason = "Some reason";
        val max = 1000;
        val maxSize = IntStream.range(0, max).mapToObj(it -> "M").reduce("", (acc, e) -> acc + e);
        val tooLong = IntStream.range(0, max + 1).mapToObj(it -> "O").reduce("", (acc, e) -> acc + e);

        // assert when too big

        assertThrows(
            ApiException.class,
            () -> consumerClient.uiApi().terminateContractAgreement(
                negotiation.getContractAgreementId(),
                ContractTerminationRequest.builder()
                    .detail(tooLong)
                    .reason(reason)
                    .build())
        );

        // assert when max size

        consumerClient.uiApi().terminateContractAgreement(
            negotiation.getContractAgreementId(),
            ContractTerminationRequest.builder()
                .detail(maxSize)
                .reason(reason)
                .build());

        awaitTerminationCount(consumerClient, 1);
        awaitTerminationCount(providerClient, 1);

        // termination completed == success
    }

    @DisabledOnGithub
    @TestFactory
    List<DynamicTest> theDetailsAreMandatory(
        E2eTestScenario scenario,
        @Consumer EdcClient consumerClient
    ) {
        val invalidDetails = List.of(
            "",
            " ",
            "            ",
            "\t",
            "\n"
        );

        return invalidDetails.stream().map(
            detail -> dynamicTest("Can't use '%s' for details".formatted(detail),
                () -> {
                    val assetId = scenario.createAsset();
                    scenario.createContractDefinition(assetId);
                    val negotiation = scenario.negotiateAssetAndAwait(assetId);

                    // act
                    val reason = "Some reason";

                    // assert when too big

                    assertThrows(
                        ApiException.class,
                        () -> consumerClient.uiApi().terminateContractAgreement(
                            negotiation.getContractAgreementId(),
                            ContractTerminationRequest.builder()
                                .detail(detail)
                                .reason(reason)
                                .build())
                    );
                })).toList();
    }

    @Test
    void doesntCrashWhenAgreementDoesntExist(
        @Consumer EdcClient consumerClient
    ) {
        // act
        assertThrows(
            ApiException.class,
            () -> consumerClient.uiApi().terminateContractAgreement(
                ContractOfferId.create("definition-1", "asset-1").toString(),
                ContractTerminationRequest.builder().detail("Some detail").reason("Some reason").build()));
    }

    @DisabledOnGithub
    @Test
    @SneakyThrows
    void cantTransferDataAfterTerminated(
        E2eTestScenario scenario,
        ClientAndServer mockServer,
        @Consumer EdcClient consumerClient,
        @Provider EdcClient providerClient
    ) {
        val assetId = "asset-1";
        val mockedAsset = scenario.createAssetWithMockResource(assetId);
        scenario.createContractDefinition(assetId);
        scenario.negotiateAssetAndAwait(assetId);

        val destinationPath = "/destination/some/path/";
        val destinationUrl = "http://localhost:" + mockServer.getPort() + destinationPath;
        mockServer.when(HttpRequest.request(destinationPath).withMethod("POST")).respond(it -> HttpResponse.response().withStatusCode(200));

        val negotiation = scenario.negotiateAssetAndAwait(assetId);

        val transferRequest = InitiateTransferRequest.builder()
            .contractAgreementId(negotiation.getContractAgreementId())
            .transferType("HttpData-PUSH")
            .dataSinkProperties(
                Map.of(
                    Prop.Edc.BASE_URL, destinationUrl,
                    Prop.Edc.METHOD, HttpMethod.POST,
                    Prop.Edc.TYPE, "HttpData"
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
        assertThat(mockedAsset.networkAccesses().get()).isGreaterThan(0);

        mockedAsset.networkAccesses().set(0);

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
        assertThat(mockedAsset.networkAccesses().get()).isEqualTo(0);
        // second transfer attempt
        Thread.sleep(10_000);
        assertThat(mockedAsset.networkAccesses().get()).isEqualTo(0);
    }

    @Test
    @DisabledOnGithub
    void canTerminateOnlyOnce(
        E2eTestScenario scenario,
        @Consumer EdcClient consumerClient,
        @Provider EdcClient providerClient
    ) {
        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAssetAndAwait(assetId);

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

    @SneakyThrows
    @Test
    @DisabledOnGithub
    void canListenToTerminationEvents(
        E2eTestScenario scenario,
        @Consumer EdcClient consumerClient,
        @Consumer ContractAgreementTerminationService consumerService,
        @Provider EdcClient providerClient,
        @Provider ContractAgreementTerminationService providerService
    ) {
        // arrange
        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAssetAndAwait(assetId);

        val detail = "Some detail";
        val reason = "Some reason";
        val contractTerminationRequest = ContractTerminationRequest.builder().detail(detail).reason(reason).build();
        val contractAgreementId = negotiation.getContractAgreementId();

        val consumerObserver = Mockito.spy(new ContractTerminationObserver() {
        });
        val providerObserver = Mockito.spy(new ContractTerminationObserver() {
        });

        consumerService.getContractTerminationObservable().registerListener(consumerObserver);
        providerService.getContractTerminationObservable().registerListener(providerObserver);

        // act

        consumerClient.uiApi().terminateContractAgreement(contractAgreementId, contractTerminationRequest);

        awaitTerminationCount(consumerClient, 1);
        awaitTerminationCount(providerClient, 1);

        Thread.sleep(2000);

        // assert

        assertThat(consumerService.getContractTerminationObservable().getListeners()).hasSize(1);
        assertThat(providerService.getContractTerminationObservable().getListeners()).hasSize(1);

        ArgumentCaptor<ContractTerminationEvent> argument = ArgumentCaptor.forClass(ContractTerminationEvent.class);

        verify(consumerObserver).contractTerminationStartedFromThisInstance(argument.capture());
        assertTerminationEvent(argument, contractAgreementId, contractTerminationRequest);

        verify(consumerObserver).contractTerminationCompletedOnThisInstance(any());
        assertTerminationEvent(argument, contractAgreementId, contractTerminationRequest);

        verify(consumerObserver).contractTerminationOnCounterpartyStarted(any());
        assertTerminationEvent(argument, contractAgreementId, contractTerminationRequest);

        verify(providerObserver).contractTerminatedByCounterpartyStarted(any());
        assertTerminationEvent(argument, contractAgreementId, contractTerminationRequest);

        verify(providerObserver).contractTerminatedByCounterparty(any());
        assertTerminationEvent(argument, contractAgreementId, contractTerminationRequest);

        // act

        consumerService.getContractTerminationObservable().unregisterListener(consumerObserver);
        providerService.getContractTerminationObservable().unregisterListener(providerObserver);

        // assert

        assertThat(consumerService.getContractTerminationObservable().getListeners()).hasSize(0);
        assertThat(providerService.getContractTerminationObservable().getListeners()).hasSize(0);
    }

    private static void assertTerminationEvent(ArgumentCaptor<ContractTerminationEvent> argument, String contractAgreementId,
                                  ContractTerminationRequest contractTerminationRequest) {
        assertThat(argument.getValue().contractAgreementId()).isEqualTo(contractAgreementId);
        assertThat(argument.getValue().detail()).isEqualTo(contractTerminationRequest.getDetail());
        assertThat(argument.getValue().reason()).isEqualTo(contractTerminationRequest.getReason());
        assertThat(argument.getValue().timestamp()).isNotNull();
    }
}
