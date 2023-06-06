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

package de.sovity.edc.ext.wrapper.api.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.connector.transfer.spi.types.DataRequest;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcessStates;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.LiteralExpression;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static de.sovity.edc.ext.wrapper.TestUtils.createConfiguration;
import static de.sovity.edc.ext.wrapper.TestUtils.givenManagementEndpoint;
import static de.sovity.edc.extension.policy.AlwaysTruePolicyConstants.EXPRESSION_LEFT_VALUE;
import static de.sovity.edc.extension.policy.AlwaysTruePolicyConstants.EXPRESSION_RIGHT_VALUE;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;

@ApiTest
@ExtendWith(EdcExtension.class)
class ContractAgreementPageTest {
    LocalDate today = LocalDate.parse("2019-04-01");
    ZonedDateTime todayAsZonedDateTime = today.atStartOfDay(ZoneId.systemDefault());
    long todayEpochMillis = todayAsZonedDateTime.toInstant().toEpochMilli();
    long todayEpochSeconds = todayAsZonedDateTime.toInstant().getEpochSecond();

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration());
    }

    ValidatableResponse whenContractAgreementEndpoint() {
        return givenManagementEndpoint()
                .when()
                .get("wrapper/ui/pages/contract-agreement-page")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void testContractAgreementPage(
            ContractNegotiationStore contractNegotiationStore,
            TransferProcessStore transferProcessStore
    ) {
        contractNegotiationStore.save(contractDefinition(1));

        transferProcessStore.save(transferProcess(1, 1, TransferProcessStates.COMPLETED.code()));

        whenContractAgreementEndpoint()
                .assertThat()
                .body("contractAgreements", hasSize(1))
                .body("contractAgreements[0].contractAgreementId", equalTo("my-contract-agreement-1"))
                .body("contractAgreements[0].contractNegotiationId", equalTo("my-contract-negotiation-1"))
                .body("contractAgreements[0].direction", equalTo("PROVIDING"))
                .body("contractAgreements[0].counterPartyAddress", equalTo("http://other-connector"))
                .body("contractAgreements[0].counterPartyId", equalTo("urn:connector:other-connector"))
                .body("contractAgreements[0].contractSigningDate", equalTo(todayPlusDays(0)))
                .body("contractAgreements[0].contractStartDate", equalTo(todayPlusDays(0)))
                .body("contractAgreements[0].contractEndDate", equalTo(todayPlusDays(2)))
                .body("contractAgreements[0].asset.assetId", equalTo("my-asset-1"))
                .body("contractAgreements[0].asset.createdAt", equalTo(todayPlusDays(0)))
                .body("contractAgreements[0].asset.properties.\"asset:prop:id\"", equalTo("my-asset-1"))
                .body("contractAgreements[0].asset.properties.some-property", equalTo("X"))
                .body("contractAgreements[0].transferProcesses", hasSize(1))
                .body("contractAgreements[0].transferProcesses[0].transferProcessId", equalTo("my-transfer-1-1"))
                .body("contractAgreements[0].transferProcesses[0].lastUpdatedDate", endsWith("Z"))
                .body("contractAgreements[0].transferProcesses[0].state.name", equalTo("COMPLETED"))
                .body("contractAgreements[0].transferProcesses[0].state.code", equalTo(800))
                .body("contractAgreements[0].transferProcesses[0].state.simplifiedState", equalTo("OK"))
                .body("contractAgreements[0].transferProcesses[0].errorMessage", equalTo("my-error-message-1"));
    }

    private TransferProcess transferProcess(int contract, int transfer, int code) {
        var dataRequest = DataRequest.Builder.newInstance()
                .contractId("my-contract-agreement-" + contract)
                .assetId("my-asset-" + contract)
                .processId("my-transfer-" + contract + "-" + transfer)
                .id("my-data-request-" + contract + "-" + transfer)
                .processId("my-transfer-" + contract + "-" + transfer)
                .connectorAddress("http://other-connector")
                .connectorId("urn:connector:other-connector")
                .dataDestination(DataAddress.Builder.newInstance().type("HttpData").build())
                .build();
        return TransferProcess.Builder.newInstance()
                .id("my-transfer-" + contract + "-" + transfer)
                .state(code)
                .type(TransferProcess.Type.PROVIDER)
                .dataRequest(dataRequest)
                .contentDataAddress(DataAddress.Builder.newInstance().type("HttpData").build())
                .errorDetail("my-error-message-" + transfer)
                .build();
    }

    private ContractNegotiation contractDefinition(int contract) {
        var agreement = ContractAgreement.Builder.newInstance()
                .id("my-contract-agreement-" + contract)
                .assetId("my-asset-" + contract)
                .contractSigningDate(todayEpochSeconds)
                .contractStartDate(todayAsZonedDateTime.toInstant().getEpochSecond())
                .contractEndDate(todayAsZonedDateTime.plusDays(2L * contract).toInstant().getEpochSecond())
                .providerAgentId("idk")
                .consumerAgentId("idk")
                .policy(alwaysTrue())
                .build();

        // Contract Negotiations can contain multiple Contract Offers (?)
        // Test this
        var irrelevantOffer = ContractOffer.Builder.newInstance()
                .id("my-contract-offer-" + contract + "-irrelevant")
                .asset(asset(contract + "-irrelevant"))
                .contractStart(todayAsZonedDateTime.minusDays(contract))
                .contractEnd(todayAsZonedDateTime.plusDays(contract))
                .consumer(URI.create("http://other-connector"))
                .offerStart(todayAsZonedDateTime.minusDays(2L * contract))
                .offerEnd(todayAsZonedDateTime.plusDays(2L * contract))
                .policy(alwaysTrue())
                .build();

        var offer = ContractOffer.Builder.newInstance()
                .id("my-contract-offer-" + contract)
                .asset(asset(String.valueOf(contract)))
                .contractStart(todayAsZonedDateTime)
                .contractEnd(todayAsZonedDateTime.plusDays(2L * contract))
                .consumer(URI.create("http://other-connector"))
                .offerStart(todayAsZonedDateTime.minusDays(2L * contract))
                .offerEnd(todayAsZonedDateTime.plusDays(2L * contract))
                .policy(alwaysTrue())
                .build();

        return ContractNegotiation.Builder.newInstance()
                .correlationId("my-correlation-" + contract)
                .contractAgreement(agreement)
                .id("my-contract-negotiation-" + contract)
                .counterPartyAddress("http://other-connector")
                .counterPartyId("urn:connector:other-connector")
                .protocol("ids")
                .type(ContractNegotiation.Type.PROVIDER)
                .contractOffers(List.of(irrelevantOffer, offer))
                .build();
    }

    private Asset asset(String suffix) {
        return Asset.Builder.newInstance()
                .id("my-asset-" + suffix)
                .property("some-property", "X")
                .createdAt(todayEpochMillis)
                .build();
    }


    private Policy alwaysTrue() {
        var alwaysTrueConstraint = AtomicConstraint.Builder.newInstance()
                .leftExpression(new LiteralExpression(EXPRESSION_LEFT_VALUE))
                .operator(Operator.EQ)
                .rightExpression(new LiteralExpression(EXPRESSION_RIGHT_VALUE))
                .build();
        var alwaysTruePermission = Permission.Builder.newInstance()
                .action(Action.Builder.newInstance().type("USE").build())
                .constraint(alwaysTrueConstraint)
                .build();
        return Policy.Builder.newInstance()
                .permission(alwaysTruePermission)
                .build();
    }

    private String todayPlusDays(int i) {
        return todayAsZonedDateTime.plusDays(i).toInstant().toString();
    }
}
