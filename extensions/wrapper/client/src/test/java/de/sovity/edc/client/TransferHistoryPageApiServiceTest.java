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

import de.sovity.edc.client.gen.model.ContractAgreementTransferRequest;
import de.sovity.edc.client.gen.model.ContractAgreementTransferRequestParams;
import de.sovity.edc.client.gen.model.TransferHistoryPage;
import de.sovity.edc.ext.wrapper.api.ui.model.TransferHistoryEntry;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.connector.transfer.spi.types.*;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static de.sovity.edc.ext.wrapper.utils.EdcDateUtils.utcMillisToOffsetDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
public class TransferHistoryPageApiServiceTest {

    private static final String DATA_SINK = "http://my-data-sink/api/stuff";
    private static final String COUNTER_PARTY_ADDRESS = "http://some-other-connector/api/v1/ids/data";

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(TestUtils.createConfiguration(Map.of()));
    }

    @Test
    void startTransferProcessForAgreementId(
            ContractNegotiationStore store,
            TransferProcessStore transferProcessStore
    ) {
        var client = TestUtils.edcClient();

        // arrange
        var contractId = UUID.randomUUID().toString();
        var negotiation = createContractNegotiation(store, COUNTER_PARTY_ADDRESS, contractId);

        var request = new ContractAgreementTransferRequest(
                ContractAgreementTransferRequest.TypeEnum.PARAMS_ONLY,
                new ContractAgreementTransferRequestParams(
                        contractId,
                        Map.of(
                                "type", "HttpData",
                                "baseUrl", DATA_SINK
                        ),
                        Map.of("some", "prop")
                ),
                null
        );

        // act
        var result = client.uiApi().initiateTransfer(request);

        // then
        var transferProcess = transferProcessStore.find(result.getId());
        assertThat(transferProcess).isNotNull();


        var updatedTransferProcessStore = createTransferProcesses(transferProcessStore);

        var transferHistoryEntries = createTransferHistoryPageEntry()
        // act
        var result = client.uiApi().transferHistoryPageEndpointCall(transferHistoryEntries);

        // then
        var transferProcess = transferProcessStore.find(result.getId());
        assertThat(transferProcess).isNotNull();
    }


    private ContractNegotiation createContractNegotiation(
            ContractNegotiationStore store,
            String counterPartyAddress,
            String agreementId
    ) {
        var agreement = ContractAgreement.Builder.newInstance()
                .id(agreementId)
                .providerAgentId(UUID.randomUUID().toString())
                .consumerAgentId(UUID.randomUUID().toString())
                .assetId(UUID.randomUUID().toString())
                .policy(Policy.Builder.newInstance().build())
                .build();

        var negotiation = ContractNegotiation.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .counterPartyId(UUID.randomUUID().toString())
                .counterPartyAddress(counterPartyAddress)
                .protocol("protocol")
                .contractAgreement(agreement)
                .createdAt(2323223232L)
                .build();

        store.save(negotiation);
        return negotiation;
    }

    private TransferProcessStore createTransferProcesses(
            TransferProcessStore transferProcessStore
    ) {

        var dataRequestForTransfer = DataRequest.Builder.newInstance().assetId("urn:artifact:db-rail-network-2023-jan")
                .build();
        var consumerTransferProcess = TransferProcess.Builder.newInstance().id("be0cac12-bb43-420e-aa29-d66bb3d0e0ac")
                .type(TransferProcess.Type.CONSUMER).dataRequest(dataRequestForTransfer).createdAt(2323223231L).
                updatedAt(2323223530L).state(4).errorDetail(null).build();
        var providerTransferProcess = TransferProcess.Builder.newInstance().id("81cdf4cf-8427-480f-9662-8a29d66ddd3b")
                .type(TransferProcess.Type.PROVIDER).dataRequest(dataRequestForTransfer).createdAt(2323223231L).
                updatedAt(2323223530L).state(4).errorDetail(null).build();

        transferProcessStore.save(consumerTransferProcess);
        transferProcessStore.save(providerTransferProcess);
        return transferProcessStore;
    }

    private TransferHistoryPage createTransferHistoryPageEntry(
            ContractNegotiation negotiation,
            ContractNegotiationStore contractNegotiationStore,
            ContractAgreementService contractAgreementService,
            AssetService assetService,
            TransferProcessService transferProcessService,
            TransferProcessStore transferProcessStore
    ) {

        var transferHistoryEntry = TransferHistoryEntry.Builder.newInstance()
                .transferProcessId(UUID.randomUUID().toString())
                .createdDate(utcMillisToOffsetDateTime(negotiation.getCreatedAt()))
                .lastUpdatedDate(utcMillisToOffsetDateTime(transferProcessStore.find("be0cac12-bb43-420e-aa29-d66bb3d0e0ac").getUpdatedAt())
                        .state(transferProcessStore.find("be0cac12-bb43-420e-aa29-d66bb3d0e0ac").getState()).;

        transferProcessStore.save(transferHistoryEntry);
        return transferHistoryEntry;
    }


}
