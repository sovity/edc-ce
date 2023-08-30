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

package de.sovity.edc.client;

import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.text.ParseException;

import static de.sovity.edc.client.TransferProcessTestUtils.createConsumingTransferProcesses;
import static de.sovity.edc.client.TransferProcessTestUtils.createProvidingTransferProcesses;
import static de.sovity.edc.client.gen.model.TransferHistoryEntry.DirectionEnum.CONSUMING;
import static de.sovity.edc.client.gen.model.TransferHistoryEntry.DirectionEnum.PROVIDING;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class TransferHistoryPageApiServiceTest {

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
    }

    @Test
    void startTransferProcessForAgreement(
            ContractNegotiationStore negotiationStore,
            TransferProcessStore transferProcessStore,
            AssetService assetStore
    ) throws ParseException {
        var client = TestUtils.edcClient();

        // arrange
        createProvidingTransferProcesses(negotiationStore, transferProcessStore, assetStore);
        createConsumingTransferProcesses(negotiationStore, transferProcessStore);

        // act
        var result = client.uiApi().transferHistoryPageEndpoint();

        // get
        var transferProcess = result.getTransferEntries();

        // assert for the order of entries
        assertThat(transferProcess.get(1).getTransferProcessId()).isEqualTo(TransferProcessTestUtils.PROVIDING_TRANSFER_PROCESS_ID);

        // assert for consuming request entry
        var consumingProcess = transferProcess.get(0);
        assertThat(consumingProcess.getTransferProcessId()).isEqualTo(TransferProcessTestUtils.CONSUMING_TRANSFER_PROCESS_ID);
        assertThat(consumingProcess.getAssetId()).isEqualTo(TransferProcessTestUtils.UNKNOWN_ASSET_ID);
        assertThat(consumingProcess.getCounterPartyConnectorEndpoint()).isEqualTo(TransferProcessTestUtils.COUNTER_PARTY_ADDRESS);
        assertThat(consumingProcess.getContractAgreementId()).isEqualTo(TransferProcessTestUtils.CONSUMING_CONTRACT_ID);
        assertThat(consumingProcess.getDirection()).isEqualTo(CONSUMING);
        assertThat(consumingProcess.getState().getCode()).isEqualTo(800);
        assertThat(consumingProcess.getAssetName()).isEqualTo(TransferProcessTestUtils.UNKNOWN_ASSET_ID);
        assertThat(consumingProcess.getErrorMessage()).isEqualTo("");

        // assert for providing request entry
        var providingProcess = transferProcess.get(1);
        assertThat(providingProcess.getTransferProcessId()).isEqualTo(TransferProcessTestUtils.PROVIDING_TRANSFER_PROCESS_ID);
        assertThat(providingProcess.getAssetId()).isEqualTo(TransferProcessTestUtils.VALID_ASSET_ID);
        assertThat(providingProcess.getCounterPartyConnectorEndpoint()).isEqualTo(TransferProcessTestUtils.COUNTER_PARTY_ADDRESS);
        assertThat(providingProcess.getContractAgreementId()).isEqualTo(TransferProcessTestUtils.PROVIDING_CONTRACT_ID);
        assertThat(providingProcess.getDirection()).isEqualTo(PROVIDING);
        assertThat(providingProcess.getState().getCode()).isEqualTo(800);
        assertThat(providingProcess.getAssetName()).isEqualTo(TransferProcessTestUtils.ASSET_NAME);
        assertThat(providingProcess.getErrorMessage()).isEqualTo("TransferProcessManager: attempt #8 failed to send transfer");
    }
}
