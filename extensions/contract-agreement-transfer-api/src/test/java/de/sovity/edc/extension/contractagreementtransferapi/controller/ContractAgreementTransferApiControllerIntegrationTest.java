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

package de.sovity.edc.extension.contractagreementtransferapi.controller;

import io.restassured.http.ContentType;
import org.eclipse.edc.api.model.DataAddressDto;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.dataplane.selector.spi.store.DataPlaneInstanceStore;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.protocol.ProtocolWebhook;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.callback.CallbackAddress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.mock;

@ApiTest
@ExtendWith(EdcExtension.class)
public class ContractAgreementTransferApiControllerIntegrationTest {

    private static final String COUNTER_PARTY_ADDRESS = "http://localhost:8080/api/v1/ids/data";
    private final int dataPort = getFreePort();
    private final String authKey = "123456";

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.registerServiceMock(ProtocolWebhook.class, mock(ProtocolWebhook.class));
        extension.registerServiceMock(JsonLd.class, mock(JsonLd.class));
        extension.registerServiceMock(DataPlaneInstanceStore.class,
                mock(DataPlaneInstanceStore.class));
        extension.setConfiguration(Map.of(
                "web.http.port", String.valueOf(getFreePort()),
                "web.http.path", "/api",
                "web.http.management.port", String.valueOf(dataPort),
                "web.http.management.path", "/api/v1/data",
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
                .post(String.format("/contract-agreements-transfer/contractagreements/%s/transfer"
                        , agreementId))
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("createdAt", is(notNullValue())).and()
                .body("@id", is(notNullValue()));

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
                .callbackAddresses(List.of(CallbackAddress.Builder.newInstance()
                        .uri("local://test")
                        .events(Set.of("test"))
                        .transactional(true)
                        .build()))
                .build();
    }

    private ContractAgreement createContractAgreement(String agreementId) {
        return ContractAgreement.Builder.newInstance()
                .id(agreementId)
                .providerId(UUID.randomUUID().toString())
                .consumerId(UUID.randomUUID().toString())
                .assetId(UUID.randomUUID().toString())
                .policy(Policy.Builder.newInstance().build())
                .build();
    }
}