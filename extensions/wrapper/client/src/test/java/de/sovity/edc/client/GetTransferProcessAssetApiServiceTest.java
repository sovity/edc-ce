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
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class GetTransferProcessAssetApiServiceTest {

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
    }

    @Test
    void testProviderTransferProcess(ContractNegotiationStore negotiationStore,
                                     TransferProcessStore transferProcessStore,
                                     AssetService assetStore) throws ParseException {

        var client = TestUtils.edcClient();

        //arrange data
        createProvidingTransferProcesses(negotiationStore, transferProcessStore, assetStore);

        //act
        var providerAssetResult = client.uiApi().getTransferProcessAsset(TransferProcessTestUtils.PROVIDING_TRANSFER_PROCESS_ID);

        //assert
        assertThat(providerAssetResult.getAssetId()).isEqualTo(TransferProcessTestUtils.PROVIDING_ASSET_ID);
        assertThat(providerAssetResult.getName()).isEqualTo(TransferProcessTestUtils.PROVIDING_ASSET_NAME);
    }

    @Test
    void testConsumerTransferProcess(ContractNegotiationStore negotiationStore,
                                     TransferProcessStore transferProcessStore) throws ParseException {

        var client = TestUtils.edcClient();

        //arrange data
        createConsumingTransferProcesses(negotiationStore, transferProcessStore);

        //act
        var consumerAssetResult = client.uiApi().getTransferProcessAsset(TransferProcessTestUtils.CONSUMING_TRANSFER_PROCESS_ID);

        //assert
        assertThat(consumerAssetResult.getAssetId()).isEqualTo(TransferProcessTestUtils.CONSUMING_ASSET_ID);
        assertThat(consumerAssetResult.getName()).isEqualTo(TransferProcessTestUtils.CONSUMING_ASSET_ID);
    }

}
