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
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.connector.transfer.spi.types.DataRequest;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import static de.sovity.edc.client.gen.model.TransferHistoryEntry.DirectionEnum.CONSUMING;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class TransferHistoryPageApiServiceTest {

    private static final String COUNTER_PARTY_ADDRESS = "http://some-other-connector/api/v1/ids/data";

    private static final String CONTRACT_ID = "db-rail-network-2023-jan-cd:f52a5d30-6356-4a55-a75a-3c45d7a88c3e";

    private static final String ASSET_ID = "urn:artifact:db-rail-network-2023-jan";


    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(TestUtils.createConfiguration(Map.of()));
    }

    @Test
    void startTransferProcessForAgreementId(
            ContractNegotiationStore store,
            TransferProcessStore transferProcessStore
    ) throws ParseException {
        var client = TestUtils.edcClient();

        // arrange
        var agreement = createContractAgreement(CONTRACT_ID, ASSET_ID);
        createContractNegotiation(store, COUNTER_PARTY_ADDRESS, agreement);
        createTransferProcess(transferProcessStore);

        // act
        var result = client.uiApi().transferHistoryPageEndpoint();

        // get
        var transferProcess = result.getTransferEntries();

        // assert for the order of entries
        assertThat(transferProcess.get(1).getTransferProcessId()).isEqualTo("81cdf4cf-8427-480f-9662-8a29d66ddd3b");

        // assert for one request entry
        assertThat(transferProcess.get(0).getTransferProcessId()).isEqualTo("be0cac12-bb43-420e-aa29-d66bb3d0e0ac");
        assertThat(transferProcess.get(0).getAssetId()).isEqualTo(ASSET_ID);
        assertThat(transferProcess.get(0).getCounterPartyConnectorEndpoint()).isEqualTo(COUNTER_PARTY_ADDRESS);
        assertThat(transferProcess.get(0).getContractAgreementId()).isEqualTo(CONTRACT_ID);
        assertThat(transferProcess.get(0).getDirection()).isEqualTo(CONSUMING);
        assertThat(transferProcess.get(0).getState().getCode()).isEqualTo(800);
        assertThat(transferProcess.get(0).getAssetName()).isEqualTo(ASSET_ID);
        assertThat(transferProcess.get(0).getErrorMessage()).isNull();
    }

    private ContractAgreement createContractAgreement(
            String agreementId,
            String assetId
    ) {
        var agreement = ContractAgreement.Builder.newInstance()
                .id(agreementId)
                .providerAgentId(UUID.randomUUID().toString())
                .consumerAgentId(UUID.randomUUID().toString())
                .assetId(assetId)
                .policy(Policy.Builder.newInstance().build())
                .build();

        return agreement;
    }

    private ContractNegotiation createContractNegotiation(
            ContractNegotiationStore store,
            String counterPartyAddress,
            ContractAgreement agreement
    ) {
        var negotiation = ContractNegotiation.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .counterPartyId(UUID.randomUUID().toString())
                .counterPartyAddress(counterPartyAddress)
                .protocol("protocol")
                .contractAgreement(agreement)
                .build();

        store.save(negotiation);
        return negotiation;
    }

    private void createTransferProcess(TransferProcessStore transferProcessStore) throws ParseException {

        var dataAddress = DataAddress.Builder.newInstance()
                .type("HttpData")
                .build();

        var dataRequestForTransfer = DataRequest.Builder.newInstance()
                .assetId(ASSET_ID)
                .contractId(CONTRACT_ID)
                .dataDestination(dataAddress)
                .build();

        var consumerTransferProcess = TransferProcess.Builder.newInstance()
                .id("be0cac12-bb43-420e-aa29-d66bb3d0e0ac")
                .type(TransferProcess.Type.CONSUMER).dataRequest(dataRequestForTransfer)
                .createdAt(dateFormatterToLong("7-Jul-2023"))
                .updatedAt(dateFormatterToLong("9-Jul-2023"))
                .state(800)
                .errorDetail(null)
                .build();
        var providerTransferProcess = TransferProcess.Builder.newInstance()
                .id("81cdf4cf-8427-480f-9662-8a29d66ddd3b")
                .type(TransferProcess.Type.PROVIDER)
                .dataRequest(dataRequestForTransfer)
                .createdAt(dateFormatterToLong("8-Jul-2023"))
                .updatedAt(dateFormatterToLong("8-Jul-2023"))
                .state(800)
                .errorDetail(null)
                .build();

        transferProcessStore.save(consumerTransferProcess);
        transferProcessStore.save(providerTransferProcess);
    }

    private long dateFormatterToLong(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        return formatter.parse(date).getTime();
    }
}
