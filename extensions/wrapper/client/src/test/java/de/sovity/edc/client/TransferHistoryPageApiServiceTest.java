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
import java.util.Map;

import static de.sovity.edc.client.TransferProcessTestUtils.*;
import static de.sovity.edc.client.gen.model.TransferHistoryEntry.DirectionEnum.CONSUMING;
import static de.sovity.edc.client.gen.model.TransferHistoryEntry.DirectionEnum.PROVIDING;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class TransferHistoryPageApiServiceTest {

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(TestUtils.createConfiguration(Map.of()));
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
        assertThat(transferProcess.get(0).getTransferProcessId()).isEqualTo(TransferProcessTestUtils.CONSUMING_TRANSFER_PROCESS_ID);
        assertThat(transferProcess.get(0).getAssetId()).isEqualTo(UNKNOWN_ASSET_ID);
        assertThat(transferProcess.get(0).getCounterPartyConnectorEndpoint()).isEqualTo(COUNTER_PARTY_ADDRESS);
        assertThat(transferProcess.get(0).getContractAgreementId()).isEqualTo(CONSUMING_CONTRACT_ID);
        assertThat(transferProcess.get(0).getDirection()).isEqualTo(CONSUMING);
        assertThat(transferProcess.get(0).getState().getCode()).isEqualTo(800);
        assertThat(transferProcess.get(0).getAssetName()).isEqualTo(UNKNOWN_ASSET_ID);
        assertThat(transferProcess.get(0).getErrorMessage()).isEqualTo("");

        // assert for providing request entry
        assertThat(transferProcess.get(1).getTransferProcessId()).isEqualTo(TransferProcessTestUtils.PROVIDING_TRANSFER_PROCESS_ID);
        assertThat(transferProcess.get(1).getAssetId()).isEqualTo(VALID_ASSET_ID);
        assertThat(transferProcess.get(1).getCounterPartyConnectorEndpoint()).isEqualTo(COUNTER_PARTY_ADDRESS);
        assertThat(transferProcess.get(1).getContractAgreementId()).isEqualTo(PROVIDING_CONTRACT_ID);
        assertThat(transferProcess.get(1).getDirection()).isEqualTo(PROVIDING);
        assertThat(transferProcess.get(1).getState().getCode()).isEqualTo(800);
        assertThat(transferProcess.get(1).getAssetName()).isEqualTo(ASSET_NAME);
        assertThat(transferProcess.get(1).getErrorMessage()).isEqualTo("TransferProcessManager: attempt #8 failed to send transfer");
    }
}
