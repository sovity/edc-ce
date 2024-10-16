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
import de.sovity.edc.client.gen.model.ContractAgreementPageQuery;
import de.sovity.edc.client.gen.model.ContractTerminationRequest;
import de.sovity.edc.extension.e2e.connector.remotes.api_wrapper.E2eTestScenario;
import de.sovity.edc.extension.e2e.junit.CeE2eTestExtension;
import de.sovity.edc.extension.e2e.junit.utils.Consumer;
import de.sovity.edc.extension.e2e.junit.utils.Provider;
import de.sovity.edc.extension.utils.junit.DisabledOnGithub;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.stream.IntStream;

import static de.sovity.edc.client.gen.model.ContractTerminationStatus.ONGOING;
import static de.sovity.edc.client.gen.model.ContractTerminationStatus.TERMINATED;
import static de.sovity.edc.extension.contacttermination.ContractTerminationTestUtils.awaitTerminationCount;
import static org.assertj.core.api.Assertions.assertThat;

public class CanGetAgreementPageForNonTerminatedContractTest {

    @RegisterExtension
    private final static CeE2eTestExtension e2eTestExtension = CeE2eTestExtension.builder()
        .additionalModule(":launchers:connectors:sovity-dev")
        .build();

    @Test
    @DisabledOnGithub
    void canGetAgreementPageForNonTerminatedContract(
        E2eTestScenario scenario,
        @Consumer EdcClient consumerClient,
        @Provider EdcClient providerClient
    ) {
        val assets = IntStream.range(0, 3).mapToObj((it) -> scenario.createAsset());

        val agreements = assets
            .peek(scenario::createContractDefinition)
            .map(scenario::negotiateAssetAndAwait)
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

}
