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
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.extension.contractagreementtransferapi.controller;

import io.restassured.http.ContentType;
import org.eclipse.edc.connector.api.datamanagement.asset.model.DataAddressDto;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.query.QuerySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;
import static org.hamcrest.CoreMatchers.is;

@ApiTest
@ExtendWith(EdcExtension.class)
public class ContractAgreementTransferApiControllerIntegrationTest {

    private static final String COUNTER_PARTY_ADDRESS = "http://localhost:8080/api/v1/ids/data";
    private final int dataPort = getFreePort();
    private final String authKey = "123456";

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(Map.of(
                "web.http.port", String.valueOf(getFreePort()),
                "web.http.path", "/api",
                "web.http.data.port", String.valueOf(dataPort),
                "web.http.data.path", "/api/v1/data",
                "edc.api.auth.key", authKey));
    }

    @Test
    void startTransferProcessForAgreementId(
            ContractNegotiationStore store,
            TransferProcessStore transferProcessStore) {
        // given
        var agreementId = UUID.randomUUID().toString();
        var contractAgreement = createContractAgreement(agreementId);
        store.save(createContractNegotiation(COUNTER_PARTY_ADDRESS, contractAgreement));

        // when
        given()
                .baseUri("http://localhost:" + dataPort)
                .basePath("/api/v1/data")
                .header("x-api-key", authKey)
                .when()
                .contentType(ContentType.JSON)
                .body(getDataAddressDto())
                .post(String.format("/contract-agreements-transfer/contractagreements/%s/transfer", agreementId))
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(2));

        // then
        var transferProcessOptional = transferProcessStore.findAll(QuerySpec.max()).findFirst();
        Assertions.assertTrue(transferProcessOptional.isPresent());
        var transferProcess = transferProcessOptional.get();

        var dataRequest = transferProcess.getDataRequest();
        Assertions.assertEquals(agreementId, dataRequest.getContractId());
        Assertions.assertEquals(COUNTER_PARTY_ADDRESS, dataRequest.getConnectorAddress());
    }

    private DataAddressDto getDataAddressDto() {
        return DataAddressDto.Builder.newInstance()
                .properties(Map.of(
                        "type", "HttpData",
                        "baseUrl", "http://localhost"))
                .build();
    }

    private ContractNegotiation createContractNegotiation(
            String counterPartyAddress,
            ContractAgreement contractAgreement) {
        return ContractNegotiation.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .counterPartyId(UUID.randomUUID().toString())
                .counterPartyAddress(counterPartyAddress)
                .protocol("protocol")
                .contractAgreement(contractAgreement)
                .build();
    }

    private ContractAgreement createContractAgreement(String agreementId) {
        return ContractAgreement.Builder.newInstance()
                .id(agreementId)
                .providerAgentId(UUID.randomUUID().toString())
                .consumerAgentId(UUID.randomUUID().toString())
                .assetId(UUID.randomUUID().toString())
                .policy(Policy.Builder.newInstance().build())
                .build();
    }
}