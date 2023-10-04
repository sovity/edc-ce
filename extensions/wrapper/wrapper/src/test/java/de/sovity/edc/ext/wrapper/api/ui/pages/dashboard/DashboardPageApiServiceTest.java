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

package de.sovity.edc.ext.wrapper.api.ui.pages.dashboard;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.ext.wrapper.TestUtils;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.types.DataRequest;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcessStates;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.service.spi.result.ServiceResult;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation.Type.CONSUMER;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation.Type.PROVIDER;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ApiTest
@ExtendWith(EdcExtension.class)
class DashboardPageApiServiceTest {
    EdcClient client;
    AssetIndex assetIndex;
    PolicyDefinitionService policyDefinitionService;
    TransferProcessService transferProcessService;
    ContractNegotiationStore contractNegotiationStore;

    @BeforeEach
    void setUp(EdcExtension extension) {
        assetIndex = mock(AssetIndex.class);
        extension.registerServiceMock(AssetIndex.class, assetIndex);

        policyDefinitionService = mock(PolicyDefinitionService.class);
        extension.registerServiceMock(PolicyDefinitionService.class, policyDefinitionService);

        transferProcessService = mock(TransferProcessService.class);
        extension.registerServiceMock(TransferProcessService.class, transferProcessService);

        contractNegotiationStore = mock(ContractNegotiationStore.class);
        extension.registerServiceMock(ContractNegotiationStore.class, contractNegotiationStore);

        TestUtils.setupExtension(extension);
        client = TestUtils.edcClient();
    }


    @Test
    void testDashboardPage() {

        // arrange
        var assets = List.of(
                dummyAsset(),
                dummyAsset(),
                dummyAsset(),
                dummyAsset(),
                dummyAsset(),
                dummyAsset()
        );
        var transferProcesses = List.of(
                mockTransferProcess("test1", 1, TransferProcessStates.COMPLETED.code()),
                mockTransferProcess("test2", 2, TransferProcessStates.TERMINATED.code()),
                mockTransferProcess("test3", 3, TransferProcessStates.STARTED.code()),
                mockTransferProcess("test4", 4, TransferProcessStates.DEPROVISIONED.code()),
                mockTransferProcess("test5", 5, TransferProcessStates.TERMINATED.code()),
                mockTransferProcess("test6", 6, TransferProcessStates.INITIAL.code())

        );

        var policyDefinitions = List.of(
                mockPolicyDefinition(),
                mockPolicyDefinition()
        );

        var contractNegotiations = List.of(
                mockContractNegotiation(1, CONSUMER),
                mockContractNegotiation(2, CONSUMER),
                mockContractNegotiation(3, CONSUMER),
                mockContractNegotiation(4, PROVIDER),
                mockContractNegotiation(5, PROVIDER),
                mockContractNegotiation(6, PROVIDER),
                mockContractNegotiation(7, PROVIDER),
                mockContractNegotiationInProgress(PROVIDER)
        );

        when(assetIndex.queryAssets(eq(QuerySpec.max()))).thenReturn(assets.stream());
        when(transferProcessService.query(eq(QuerySpec.max()))).thenReturn(ServiceResult.success(transferProcesses.stream()));
        when(policyDefinitionService.query(eq(QuerySpec.max()))).thenReturn(ServiceResult.success(policyDefinitions.stream()));
        when(contractNegotiationStore.queryNegotiations(eq(QuerySpec.max()))).thenReturn(contractNegotiations.stream());


        // act
        var dashboardPage = client.uiApi().getDashboardPage();

        // assert
        assertThat(dashboardPage.getNumberOfAssets()).isEqualTo(assets.size());
        assertThat(dashboardPage.getNumberOfPolicies()).isEqualTo(policyDefinitions.size());
        assertThat(dashboardPage.getNumberOfConsumingAgreements()).isEqualTo(3);
        assertThat(dashboardPage.getNumberOfProvidingAgreements()).isEqualTo(4);
        assertThat(dashboardPage.getConsumingTransferProcesses().getNumOk()).isEqualTo(1);
        assertThat(dashboardPage.getProvidingTransferProcesses().getNumOk()).isEqualTo(1);
        assertThat(dashboardPage.getConsumingTransferProcesses().getNumError()).isEqualTo(1);
        assertThat(dashboardPage.getProvidingTransferProcesses().getNumError()).isEqualTo(1);
        assertThat(dashboardPage.getConsumingTransferProcesses().getNumRunning()).isEqualTo(1);
        assertThat(dashboardPage.getProvidingTransferProcesses().getNumRunning()).isEqualTo(1);

        //test Connector Properties
        assertThat(dashboardPage.getConnectorParticipantId()).isEqualTo("my-edc-participant-id");
        assertThat(dashboardPage.getConnectorDescription()).isEqualTo("My Connector Description");
        assertThat(dashboardPage.getConnectorTitle()).isEqualTo("My Connector");
        assertThat(dashboardPage.getConnectorEndpoint()).isEqualTo(TestUtils.PROTOCOL_ENDPOINT);
        assertThat(dashboardPage.getConnectorCuratorName()).isEqualTo("My Org");
        assertThat(dashboardPage.getConnectorCuratorUrl()).isEqualTo("https://connector.my-org");
        assertThat(dashboardPage.getConnectorMaintainerName()).isEqualTo("Maintainer Org");
        assertThat(dashboardPage.getConnectorMaintainerUrl()).isEqualTo("https://maintainer-org");
        assertThat(dashboardPage.getConnectorDapsConfig()).isNotNull();
        assertThat(dashboardPage.getConnectorDapsConfig().getTokenUrl()).isEqualTo("https://token-url.daps");
        assertThat(dashboardPage.getConnectorDapsConfig().getJwksUrl()).isEqualTo("https://jwks-url.daps");
        assertThat(dashboardPage.getConnectorMiwConfig()).isNotNull();
        assertThat(dashboardPage.getConnectorMiwConfig().getAuthorityId()).isEqualTo("my-authority-id");
        assertThat(dashboardPage.getConnectorMiwConfig().getUrl()).isEqualTo("https://miw");
        assertThat(dashboardPage.getConnectorMiwConfig().getTokenUrl()).isEqualTo("https://token.miw");
    }

    private TransferProcess mockTransferProcess(String id, int contractId, int state) {
        DataRequest dataRequest = mock(DataRequest.class);
        when(dataRequest.getContractId()).thenReturn("ca-" + contractId);

        TransferProcess transferProcess = mock(TransferProcess.class);
        when(transferProcess.getId()).thenReturn(id);
        when(transferProcess.getDataRequest()).thenReturn(dataRequest);
        when(transferProcess.getState()).thenReturn(state);
        return transferProcess;
    }

    private Asset dummyAsset() {
        return mock(Asset.class);
    }

    private PolicyDefinition mockPolicyDefinition() {
        return mock(PolicyDefinition.class);
    }

    private ContractNegotiation mockContractNegotiation(int contract, ContractNegotiation.Type type ) {
        var contractAgreement = mock(ContractAgreement.class);
        when(contractAgreement.getId()).thenReturn("ca-" + contract);

        var contractNegotiation = mock(ContractNegotiation.class);
        when(contractNegotiation.getType()).thenReturn(type);
        when(contractNegotiation.getContractAgreement()).thenReturn(contractAgreement);

        return contractNegotiation;
    }

    private ContractNegotiation mockContractNegotiationInProgress(ContractNegotiation.Type type) {
        var contractNegotiation = mock(ContractNegotiation.class);
        when(contractNegotiation.getType()).thenReturn(type);
        return contractNegotiation;
    }
}
