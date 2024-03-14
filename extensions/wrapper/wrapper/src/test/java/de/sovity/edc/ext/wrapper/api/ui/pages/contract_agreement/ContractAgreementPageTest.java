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

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreement;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.client.gen.model.ContractAgreementDirection;
import de.sovity.edc.client.gen.model.OperatorDto;
import de.sovity.edc.client.gen.model.TransferProcessSimplifiedState;
import de.sovity.edc.ext.wrapper.TestUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
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
import org.eclipse.edc.spi.asset.AssetIndex;
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
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
@ExtendWith(EdcExtension.class)
class ContractAgreementPageTest {

    private static final int CONTRACT_DEFINITION_ID = 1;
    private static final String ASSET_ID = UUID.randomUUID().toString();

    EdcClient client;
    LocalDate today = LocalDate.parse("2019-04-01");
    ZonedDateTime todayAsZonedDateTime = today.atStartOfDay(ZoneId.systemDefault());
    long todayEpochMillis = todayAsZonedDateTime.toInstant().toEpochMilli();
    long todayEpochSeconds = todayAsZonedDateTime.toInstant().getEpochSecond();

    @BeforeEach
    void setUp(EdcExtension extension) {
        TestUtils.setupExtension(extension);
        client = TestUtils.edcClient();
    }

    @Test
    void testContractAgreementPage(
            ContractNegotiationStore contractNegotiationStore,
            TransferProcessStore transferProcessStore,
            AssetIndex assetIndex
    ) {

        // arrange
        assetIndex.create(asset(ASSET_ID)).orElseThrow(storeFailure -> new RuntimeException("Failed to create asset"));
        contractNegotiationStore.save(contractDefinition(CONTRACT_DEFINITION_ID));
        transferProcessStore.save(transferProcess(1, 1, TransferProcessStates.COMPLETED.code()));

        // act
        var actual = client.uiApi().getContractAgreementPage().getContractAgreements();
        assertThat(actual).hasSize(1);

        // assert
        var agreement = actual.get(0);
        assertThat(agreement.getContractAgreementId()).isEqualTo("my-contract-agreement-1");
        assertThat(agreement.getContractNegotiationId()).isEqualTo("my-contract-negotiation-1");
        assertThat(agreement.getDirection()).isEqualTo(ContractAgreementDirection.PROVIDING);
        assertThat(agreement.getCounterPartyAddress()).isEqualTo("http://other-connector");
        assertThat(agreement.getCounterPartyId()).isEqualTo("urn:connector:other-connector");
        assertThat(agreement.getContractSigningDate()).isEqualTo(todayPlusDays(0));
        assertThat(agreement.getAsset().getAssetId()).isEqualTo(ASSET_ID);
        assertThat(agreement.getAsset().getLandingPageUrl()).isEqualTo("X");
        assertThat(agreement.getTransferProcesses()).hasSize(1);

        var transfer = agreement.getTransferProcesses().get(0);
        assertThat(transfer.getTransferProcessId()).isEqualTo("my-transfer-1-1");
        assertThat(transfer.getLastUpdatedDate()).isNotNull();
        assertThat(transfer.getState().getName()).isEqualTo("COMPLETED");
        assertThat(transfer.getState().getCode()).isEqualTo(800);
        assertThat(transfer.getState().getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.OK);
        assertThat(transfer.getErrorMessage()).isEqualTo("my-error-message-1");

        var constraint = agreement.getContractPolicy().getConstraints().get(0);
        assertThat(constraint.getLeft()).isEqualTo("ALWAYS_TRUE");
        assertThat(constraint.getOperator()).isEqualTo(OperatorDto.EQ);
        assertThat(constraint.getRight().getValue()).isEqualTo("true");
    }

    private DataAddress dataAddress() {
        return DataAddress.Builder.newInstance()
                .type("HttpData")
                .properties(Map.of("baseUrl", "http://some-url"))
                .build();
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
                .assetId(ASSET_ID)
                .contractSigningDate(todayEpochSeconds)
                .policy(alwaysTrue())
                .providerId(URI.create("http://other-connector").toString())
                .consumerId(URI.create("http://my-connector").toString())
                .build();

        // Contract Negotiations can contain multiple Contract Offers (?)
        // Test this
        var irrelevantOffer = ContractOffer.Builder.newInstance()
                .id("my-contract-offer-" + contract + "-irrelevant")
                .assetId(asset(contract + "-irrelevant").getId())
                .policy(alwaysTrue())
                .build();

        var offer = ContractOffer.Builder.newInstance()
                .id("my-contract-offer-" + contract)
                .assetId(ASSET_ID)
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

    private Asset asset(String assetId) {
        return Asset.Builder.newInstance()
                .id(assetId)
                .property(Prop.Dcat.LANDING_PAGE, "X")
                .createdAt(todayEpochMillis)
                .dataAddress(dataAddress())
                .build();
    }


    private Policy alwaysTrue() {
        var alwaysTrueConstraint = AtomicConstraint.Builder.newInstance()
                .leftExpression(new LiteralExpression("ALWAYS_TRUE"))
                .operator(Operator.EQ)
                .rightExpression(new LiteralExpression("true"))
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
