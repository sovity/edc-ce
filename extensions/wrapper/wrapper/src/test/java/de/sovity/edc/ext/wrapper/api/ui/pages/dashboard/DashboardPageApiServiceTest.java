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
import de.sovity.edc.extension.e2e.junit.CeIntegrationTestExtension;
import de.sovity.edc.extension.e2e.junit.CeIntegrationTestUtils;
import de.sovity.edc.extension.e2e.junit.EmbeddedRuntimeFixed;
import de.sovity.edc.utils.config.ConfigProps;
import de.sovity.edc.utils.config.ConfigUtils;
import de.sovity.edc.utils.config.SovityEdcRuntime;
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset;
import org.eclipse.edc.connector.controlplane.asset.spi.index.AssetIndex;
import org.eclipse.edc.connector.controlplane.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.controlplane.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.controlplane.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.connector.controlplane.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.controlplane.services.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.connector.controlplane.services.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.connector.controlplane.services.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.DataRequest;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcessStates;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.ServiceResult;
import org.eclipse.edc.spi.system.configuration.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation.Type.CONSUMER;
import static org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation.Type.PROVIDER;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ApiTest
class DashboardPageApiServiceTest {

    @RegisterExtension
    static CeIntegrationTestExtension providerExtension = CeIntegrationTestExtension.builder()
        .additionalModule(":launchers:connectors:sovity-dev")
        .configOverrides(config -> config
            .property(ConfigProps.EDC_OAUTH_TOKEN_URL, "https://token-url.daps")
            .property(ConfigProps.EDC_OAUTH_PROVIDER_JWKS_URL, "https://jwks-url.daps")
            .property("tx.ssi.oauth.token.url", "https://token.miw")
            .property("tx.ssi.miw.url", "https://miw")
            .property("tx.ssi.miw.authority.id", "my-authority-id"))
        .build();

    AssetIndex assetIndex;
    PolicyDefinitionService policyDefinitionService;
    TransferProcessService transferProcessService;
    ContractNegotiationStore contractNegotiationStore;
    ContractDefinitionService contractDefinitionService;

    private final Random random = new Random();

    @BeforeEach
    void setUp(EmbeddedRuntimeFixed context) {
        assetIndex = mock();
        context.registerServiceMock(AssetIndex.class, assetIndex);

        policyDefinitionService = mock();
        context.registerServiceMock(PolicyDefinitionService.class, policyDefinitionService);

        transferProcessService = mock();
        context.registerServiceMock(TransferProcessService.class, transferProcessService);

        contractNegotiationStore = mock();
        context.registerServiceMock(ContractNegotiationStore.class, contractNegotiationStore);

        contractDefinitionService = mock();
        context.registerServiceMock(ContractDefinitionService.class, contractDefinitionService);
    }

    @Test
    void testKpis(EdcClient client) {
        // arrange
        mockAmounts(
            repeat(7, Mockito::mock),
            repeat(8, Mockito::mock),
            repeat(9, Mockito::mock),
            List.of(
                mockContractNegotiation(1, CONSUMER),
                mockContractNegotiation(2, PROVIDER),
                mockContractNegotiation(3, PROVIDER),
                mockContractNegotiationInProgress(CONSUMER),
                mockContractNegotiationInProgress(PROVIDER)
            ),
            flat(List.of(
                repeat(1, () -> mockTransferProcess(1, TransferProcessStates.REQUESTING.code())),
                repeat(2, () -> mockTransferProcess(1, TransferProcessStates.TERMINATED.code())),
                repeat(3, () -> mockTransferProcess(1, TransferProcessStates.COMPLETED.code())),
                repeat(4, () -> mockTransferProcess(2, TransferProcessStates.REQUESTING.code())),
                repeat(5, () -> mockTransferProcess(2, TransferProcessStates.TERMINATED.code())),
                repeat(6, () -> mockTransferProcess(2, TransferProcessStates.COMPLETED.code()))
            ))
        );

        // act
        var dashboardPage = client.uiApi().getDashboardPage();
        assertThat(dashboardPage.getNumAssets()).isEqualTo(7);
        assertThat(dashboardPage.getNumPolicies()).isEqualTo(8);
        assertThat(dashboardPage.getNumContractDefinitions()).isEqualTo(9);
        assertThat(dashboardPage.getNumContractAgreementsConsuming()).isEqualTo(1);
        assertThat(dashboardPage.getNumContractAgreementsProviding()).isEqualTo(2);
        assertThat(dashboardPage.getTransferProcessesConsuming().getNumTotal()).isEqualTo(1 + 2 + 3);
        assertThat(dashboardPage.getTransferProcessesConsuming().getNumRunning()).isEqualTo(1);
        assertThat(dashboardPage.getTransferProcessesConsuming().getNumError()).isEqualTo(2);
        assertThat(dashboardPage.getTransferProcessesConsuming().getNumOk()).isEqualTo(3);
        assertThat(dashboardPage.getTransferProcessesProviding().getNumTotal()).isEqualTo(4 + 5 + 6);
        assertThat(dashboardPage.getTransferProcessesProviding().getNumRunning()).isEqualTo(4);
        assertThat(dashboardPage.getTransferProcessesProviding().getNumError()).isEqualTo(5);
        assertThat(dashboardPage.getTransferProcessesProviding().getNumOk()).isEqualTo(6);
    }

    @Test
    void testConnectorMetadata(EdcClient client, Config config) {
        // arrange
        mockAmounts(List.of(), List.of(), List.of(), List.of(), List.of());

        // act
        var dashboardPage = client.uiApi().getDashboardPage();

        // assert
        assertThat(dashboardPage.getConnectorParticipantId()).isEqualTo("my-edc-participant-id");
        assertThat(dashboardPage.getConnectorDescription()).isEqualTo("Connector Description my-edc-participant-id");
        assertThat(dashboardPage.getConnectorTitle()).isEqualTo("Connector Title my-edc-participant-id");

        assertThat(dashboardPage.getConnectorEndpoint()).isEqualTo(ConfigUtils.getProtocolApiUrl(config.getEntries()));
        assertThat(dashboardPage.getConnectorCuratorName()).isEqualTo("Curator Name my-edc-participant-id");
        assertThat(dashboardPage.getConnectorCuratorUrl()).isEqualTo("http://curator.my-edc-participant-id");
        assertThat(dashboardPage.getConnectorMaintainerName()).isEqualTo("Maintainer Name my-edc-participant-id");
        assertThat(dashboardPage.getConnectorMaintainerUrl()).isEqualTo("http://maintainer.my-edc-participant-id");

        assertThat(dashboardPage.getConnectorDapsConfig()).isNotNull();
        assertThat(dashboardPage.getConnectorDapsConfig().getTokenUrl()).isEqualTo("https://token-url.daps");
        assertThat(dashboardPage.getConnectorDapsConfig().getJwksUrl()).isEqualTo("https://jwks-url.daps");

        assertThat(dashboardPage.getConnectorMiwConfig()).isNotNull();
        assertThat(dashboardPage.getConnectorMiwConfig().getAuthorityId()).isEqualTo("my-authority-id");
        assertThat(dashboardPage.getConnectorMiwConfig().getUrl()).isEqualTo("https://miw");
        assertThat(dashboardPage.getConnectorMiwConfig().getTokenUrl()).isEqualTo("https://token.miw");
    }

    private Asset mockAsset() {
        return mock();
    }

    private PolicyDefinition mockPolicyDefinition() {
        return mock();
    }

    private ContractDefinition mockContractDefinition() {
        return mock();
    }

    private ContractNegotiation mockContractNegotiation(int contract, ContractNegotiation.Type type) {
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

    private TransferProcess mockTransferProcess(int contractId, int state) {
        DataRequest dataRequest = mock();
        when(dataRequest.getContractId()).thenReturn("ca-" + contractId);

        TransferProcess transferProcess = mock();
        when(transferProcess.getId()).thenReturn(String.valueOf(random.nextInt()));
        when(transferProcess.getDataRequest()).thenReturn(dataRequest);
        when(transferProcess.getState()).thenReturn(state);
        return transferProcess;
    }

    private void mockAmounts(
        List<Asset> assets,
        List<PolicyDefinition> policyDefinitions,
        List<ContractDefinition> contractDefinitions,
        List<ContractNegotiation> contractNegotiations,
        List<TransferProcess> transferProcesses
    ) {
        when(assetIndex.queryAssets(eq(QuerySpec.max()))).thenAnswer(i -> assets.stream());
        when(transferProcessService.query(eq(QuerySpec.max())))
            .thenAnswer(i -> ServiceResult.success(transferProcesses.stream()));
        when(policyDefinitionService.query(eq(QuerySpec.max())))
            .thenAnswer(i -> ServiceResult.success(policyDefinitions.stream()));
        when(contractNegotiationStore.queryNegotiations(eq(QuerySpec.max())))
            .thenAnswer(i -> contractNegotiations.stream());
        when(contractDefinitionService.query(eq(QuerySpec.max())))
            .thenAnswer(i -> ServiceResult.success(contractDefinitions.stream()));
    }

    private <T> List<T> repeat(int times, Supplier<T> supplier) {
        return IntStream.range(0, times).mapToObj(i -> supplier.get()).toList();
    }

    private <T> List<T> flat(Collection<Collection<T>> collections) {
        return collections.stream().flatMap(Collection::stream).toList();
    }
}
