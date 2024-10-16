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
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static de.sovity.edc.client.gen.model.ContractTerminatedBy.COUNTERPARTY;
import static de.sovity.edc.client.gen.model.ContractTerminatedBy.SELF;
import static de.sovity.edc.extension.contacttermination.ContractTerminationTestUtils.assertTermination;
import static de.sovity.edc.extension.contacttermination.ContractTerminationTestUtils.awaitTerminationCount;

public class CanTerminateFromProviderTest {

    @RegisterExtension
    private final static CeE2eTestExtension e2eTestExtension = CeE2eTestExtension.builder()
        .additionalModule(":launchers:connectors:sovity-dev")
        .build();

    @DisabledOnGithub
    @Test
    @SneakyThrows
    void test(
        E2eTestScenario scenario,
        @Consumer EdcClient consumerClient,
        @Provider EdcClient providerClient
    ) {
        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAssetAndAwait(assetId);

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
        assertTermination(consumerSideAgreements, detail, reason, COUNTERPARTY);
        assertTermination(providerSideAgreements, detail, reason, SELF);
    }

}
