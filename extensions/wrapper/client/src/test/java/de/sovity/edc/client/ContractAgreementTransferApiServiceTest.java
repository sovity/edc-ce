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
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.protocol.dsp.spi.types.HttpMessageProtocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class ContractAgreementTransferApiServiceTest {

    private static final String DATA_SINK = "http://my-data-sink/api/stuff";
    private static final String COUNTER_PARTY_ADDRESS =
            "http://some-other-connector/api/v1/ids/data";

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
    }

    @Test
    void startTransferProcessForAgreementId(
            ContractNegotiationStore store,
            TransferProcessStore transferProcessStore
    ) {
        var client = TestUtils.edcClient();

        // arrange
        var contractId = UUID.randomUUID().toString();
        createContractNegotiation(store, COUNTER_PARTY_ADDRESS, contractId);

        var request = new ContractAgreementTransferRequest(
                ContractAgreementTransferRequest.TypeEnum.PARAMS_ONLY,
                new ContractAgreementTransferRequestParams(
                        contractId,
                        Map.of(
                                "type", "HttpData",
                                "baseUrl", DATA_SINK
                        ),
                        Map.of("privateProperty", "privateValue")
                ),
                null
        );

        // act
        var result = client.uiApi().initiateTransfer(request);

        // then
        var transferProcess = transferProcessStore.findById(result.getId());
        assertThat(transferProcess).isNotNull();
        assertThat(transferProcess.getPrivateProperties()).containsAllEntriesOf(Map.of(
                "privateProperty", "privateValue"
        ));

        var dataRequest = transferProcess.getDataRequest();
        assertThat(dataRequest.getContractId()).isEqualTo(contractId);
        assertThat(dataRequest.getConnectorAddress()).isEqualTo(COUNTER_PARTY_ADDRESS);
        assertThat(dataRequest.getDataDestination().getProperties()).containsAllEntriesOf(Map.of(
                "https://w3id.org/edc/v0.0.1/ns/type", "HttpData",
                "baseUrl", DATA_SINK
        ));
    }

    private ContractNegotiation createContractNegotiation(
            ContractNegotiationStore store,
            String counterPartyAddress,
            String agreementId
    ) {
        var assetId = UUID.randomUUID().toString();
        var agreement = ContractAgreement.Builder.newInstance()
                .id(agreementId)
                .providerId(UUID.randomUUID().toString())
                .consumerId(UUID.randomUUID().toString())
                .assetId(assetId)
                .policy(getPolicy())
                .build();

        var negotiation = ContractNegotiation.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .counterPartyId(UUID.randomUUID().toString())
                .counterPartyAddress(counterPartyAddress)
                .protocol(HttpMessageProtocol.DATASPACE_PROTOCOL_HTTP)
                .contractAgreement(agreement)
                .contractOffer(createContractOffer(assetId))
                .state(ContractNegotiationStates.FINALIZED.code())
                .build();

        store.save(negotiation);
        return negotiation;
    }

    private Policy getPolicy() {
        return Policy.Builder.newInstance().build();
    }

    private ContractOffer createContractOffer(String assetId) {
        return ContractOffer.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .assetId(assetId)
                .policy(getPolicy())
                .build();
    }
}
