/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractAgreementDirection;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.junit.CeIntegrationTestUtils;
import de.sovity.edc.extension.e2e.junit.RuntimePerClassWithDbExtension;
import org.eclipse.edc.connector.controlplane.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.controlplane.services.spi.asset.AssetService;
import org.eclipse.edc.connector.controlplane.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.text.ParseException;

import static de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferProcessTestUtils.createConsumingTransferProcesses;
import static de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferProcessTestUtils.createProvidingTransferProcesses;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
class TransferHistoryPageApiServiceTest {

    private static ConnectorConfig config;
    private static EdcClient client;

    @RegisterExtension
    static RuntimePerClassWithDbExtension providerExtension = new RuntimePerClassWithDbExtension(
        ":launchers:connectors:sovity-dev",
        "provider",
        testDatabase -> {
            config = CeIntegrationTestUtils.defaultConfig("my-edc-participant-id", testDatabase);
            client = EdcClient.builder()
                .managementApiUrl(config.getManagementApiUrl())
                .managementApiKey(config.getManagementApiKey())
                .build();
            return config.getProperties();
        }
    );

    @Test
    void transferHistoryTest(
            ContractNegotiationStore negotiationStore,
            TransferProcessStore transferProcessStore,
            AssetService assetStore
    ) throws ParseException {
        // arrange
        createProvidingTransferProcesses(negotiationStore, transferProcessStore, assetStore);
        createConsumingTransferProcesses(negotiationStore, transferProcessStore);

        // act
        var actual = client.uiApi().getTransferHistoryPage().getTransferEntries();

        // assert for consuming request entry
        var consumingProcess = actual.get(0);
        assertThat(consumingProcess.getTransferProcessId()).isEqualTo(TransferProcessTestUtils.CONSUMING_TRANSFER_PROCESS_ID);
        assertThat(consumingProcess.getAssetId()).isEqualTo(TransferProcessTestUtils.CONSUMING_ASSET_ID);
        assertThat(consumingProcess.getCounterPartyConnectorEndpoint()).isEqualTo(TransferProcessTestUtils.COUNTER_PARTY_ADDRESS);
        assertThat(consumingProcess.getCounterPartyParticipantId()).isEqualTo(TransferProcessTestUtils.COUNTER_PARTY_ID);
        assertThat(consumingProcess.getContractAgreementId()).isEqualTo(TransferProcessTestUtils.CONSUMING_CONTRACT_ID);
        assertThat(consumingProcess.getDirection()).isEqualTo(ContractAgreementDirection.CONSUMING);
        assertThat(consumingProcess.getState().getCode()).isEqualTo(800);
        assertThat(consumingProcess.getAssetName()).isEqualTo(TransferProcessTestUtils.CONSUMING_ASSET_ID);
        assertThat(consumingProcess.getErrorMessage()).isEmpty();

        // assert for providing request entry
        var providingProcess = actual.get(1);
        assertThat(providingProcess.getTransferProcessId()).isEqualTo(TransferProcessTestUtils.PROVIDING_TRANSFER_PROCESS_ID);
        assertThat(providingProcess.getAssetId()).isEqualTo(TransferProcessTestUtils.PROVIDING_ASSET_ID);
        assertThat(providingProcess.getCounterPartyConnectorEndpoint()).isEqualTo(TransferProcessTestUtils.COUNTER_PARTY_ADDRESS);
        assertThat(providingProcess.getCounterPartyParticipantId()).isEqualTo(TransferProcessTestUtils.COUNTER_PARTY_ID);
        assertThat(providingProcess.getContractAgreementId()).isEqualTo(TransferProcessTestUtils.PROVIDING_CONTRACT_ID);
        assertThat(providingProcess.getDirection()).isEqualTo(ContractAgreementDirection.PROVIDING);
        assertThat(providingProcess.getState().getCode()).isEqualTo(800);
        assertThat(providingProcess.getAssetName()).isEqualTo(TransferProcessTestUtils.PROVIDING_ASSET_NAME);
        assertThat(providingProcess.getErrorMessage()).isEqualTo("TransferProcessManager: attempt #8 failed to send transfer");
    }

    @Test
    void transferProcessAssetTest_providing(
            ContractNegotiationStore negotiationStore,
            TransferProcessStore transferProcessStore,
            AssetService assetStore
    ) throws ParseException {
        // arrange
        createProvidingTransferProcesses(negotiationStore, transferProcessStore, assetStore);

        // act
        var result = client.uiApi().getTransferProcessAsset(TransferProcessTestUtils.PROVIDING_TRANSFER_PROCESS_ID);

        // assert for the order of entries
        assertThat(result.getAssetId()).isEqualTo(TransferProcessTestUtils.PROVIDING_ASSET_ID);
        assertThat(result.getTitle()).isEqualTo(TransferProcessTestUtils.PROVIDING_ASSET_NAME);
    }

    @Test
    void transferProcessAssetTest_consuming(
            ContractNegotiationStore negotiationStore,
            TransferProcessStore transferProcessStore
    ) throws ParseException {
        // arrange
        createConsumingTransferProcesses(negotiationStore, transferProcessStore);

        // act
        var result = client.uiApi().getTransferProcessAsset(TransferProcessTestUtils.CONSUMING_TRANSFER_PROCESS_ID);

        // assert for the order of entries
        assertThat(result.getAssetId()).isEqualTo(TransferProcessTestUtils.CONSUMING_ASSET_ID);
        assertThat(result.getTitle()).isEqualTo(TransferProcessTestUtils.CONSUMING_ASSET_ID);
    }
}
