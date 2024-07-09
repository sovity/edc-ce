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
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseFactory;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.monitor.Monitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.text.ParseException;

import static de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferProcessTestUtils.createConsumingTransferProcesses;
import static de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferProcessTestUtils.createProvidingTransferProcesses;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfigFactory.fromConnectorConfig;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
class TransferProcessAssetApiServiceTest {
    private static final String PARTICIPANT_ID = "someid";

    private ConnectorConfig config;

    @RegisterExtension
    static final EdcExtension EDC_CONTEXT = new EdcExtension();

    @RegisterExtension
    static final TestDatabase DATABASE = TestDatabaseFactory.getTestDatabase(1);

    private ConnectorRemote connector;

    private EdcClient client;

    @BeforeEach
    void setUp() {
        // set up provider EDC + Client
        // TODO: try to fix again after RT's PR. The EDC uses the DSP port 34003 instead of the dynamically allocated one...
        config = forTestDatabase(PARTICIPANT_ID, 34000, DATABASE);
        EDC_CONTEXT.setConfiguration(config.getProperties());
        connector = new ConnectorRemote(fromConnectorConfig(config));

        client = EdcClient.builder()
            .managementApiUrl(config.getManagementEndpoint().getUri().toString())
            .managementApiKey(config.getProperties().get("edc.api.auth.key"))
            .build();

    }

    @Test
    void testProviderTransferProcess(
            ContractNegotiationStore negotiationStore,
            TransferProcessStore transferProcessStore,
            AssetService assetStore,
            Monitor monitor
    ) throws ParseException {
        monitor.info("Hello World from TransferProcessAssetApiServiceTest#testProviderTransferProcess!");
        // arrange
        createProvidingTransferProcesses(negotiationStore, transferProcessStore, assetStore);

        // act
        var providerAssetResult = client.uiApi().getTransferProcessAsset(TransferProcessTestUtils.PROVIDING_TRANSFER_PROCESS_ID);

        // assert
        assertThat(providerAssetResult.getAssetId()).isEqualTo(TransferProcessTestUtils.PROVIDING_ASSET_ID);
        assertThat(providerAssetResult.getTitle()).isEqualTo(TransferProcessTestUtils.PROVIDING_ASSET_NAME);
    }

    @Test
    void testConsumerTransferProcess(ContractNegotiationStore negotiationStore,
                                     TransferProcessStore transferProcessStore) throws ParseException {
        // arrange
        createConsumingTransferProcesses(negotiationStore, transferProcessStore);

        // act
        var consumerAssetResult = client.uiApi().getTransferProcessAsset(TransferProcessTestUtils.CONSUMING_TRANSFER_PROCESS_ID);

        // assert
        assertThat(consumerAssetResult.getAssetId()).isEqualTo(TransferProcessTestUtils.CONSUMING_ASSET_ID);
        assertThat(consumerAssetResult.getTitle()).isEqualTo(TransferProcessTestUtils.CONSUMING_ASSET_ID);
    }

}
