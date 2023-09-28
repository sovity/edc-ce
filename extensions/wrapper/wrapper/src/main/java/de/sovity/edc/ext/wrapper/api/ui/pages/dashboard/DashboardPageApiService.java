/*
 *  Copyright (c) 2022 sovity GmbH
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

<<<<<<<HEAD
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementDirection;
import de.sovity.edc.ext.wrapper.api.ui.model.DashboardPage;
import de.sovity.edc.ext.wrapper.api.ui.model.DashboardTransferAmounts;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.DashboardDataFetcher;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferProcessStateService;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.sovity.edc.ext.wrapper.api.ui.model.TransferProcessSimplifiedState.ERROR;
import static de.sovity.edc.ext.wrapper.api.ui.model.TransferProcessSimplifiedState.OK;
import static de.sovity.edc.ext.wrapper.api.ui.model.TransferProcessSimplifiedState.RUNNING;

@RequiredArgsConstructor
public class DashboardPageApiService {
    private final DashboardDataFetcher dashboardDataFetcher;
    private final TransferProcessStateService transferProcessStateService;
    private final DapsConfigService dapsConfigService;
    private final MiwConfigService miwConfigService;
    private final SelfDescriptionService selfDescriptionService;

    @NotNull
    public DashboardPage dashboardPage(String connectorEndpoint) {

        var transferProcesses = dashboardDataFetcher.getTransferProcesses();
        var negotiations = dashboardDataFetcher.getAllContractNegotiations();

        var providingAgreements = negotiations.stream()
                .filter(negotiation -> ContractAgreementDirection.fromType(negotiation.getType()).equals(ContractAgreementDirection.PROVIDING))
                .map(negotiation -> negotiation.getContractAgreement().getId())
                .collect(Collectors.toSet());


        var consumingAgreements = negotiations.stream()
                .filter(negotiation -> ContractAgreementDirection.fromType(negotiation.getType()).equals(ContractAgreementDirection.CONSUMING))
                .map(negotiation -> negotiation.getContractAgreement().getId())
                .collect(Collectors.toSet());

        DashboardPage dashboardPage = new DashboardPage();
        dashboardPage.setConnectorTitle(selfDescriptionService.getConnectorTitle());
        dashboardPage.setConnectorDescription(selfDescriptionService.getConnectorDescription());
        dashboardPage.setConnectorEndpoint(selfDescriptionService.getConnectorEndpoint());
        dashboardPage.setConnectorParticipantId(selfDescriptionService.getParticipantId());

        dashboardPage.setConnectorCuratorUrl(selfDescriptionService.getCuratorUrl());
        dashboardPage.setConnectorCuratorName(selfDescriptionService.getCuratorName());
        dashboardPage.setConnectorMaintainerUrl(selfDescriptionService.getMaintainerUrl());
        dashboardPage.setConnectorMaintainerName(selfDescriptionService.getMaintainerName());

        dashboardPage.setConnectorMiwConfig(miwConfigService.buildMiwConfigOrNull());
        dashboardPage.setConnectorDapsConfig(dapsConfigService.buildDapsConfigOrNull());
        dashboardPage.setNumberOfAssets(dashboardDataFetcher.getNumberOfAssets());
        dashboardPage.setNumberOfPolicies(dashboardDataFetcher.getNumberOfPolicies());
        dashboardPage.setProvidingTransferProcesses(getTransferAmounts(transferProcesses, providingAgreements));
        dashboardPage.setConsumingTransferProcesses(getTransferAmounts(transferProcesses, consumingAgreements));
        dashboardPage.setNumberOfProvidingAgreements(providingAgreements.size());
        dashboardPage.setNumberOfConsumingAgreements(consumingAgreements.size());
        return dashboardPage;
    }

    DashboardTransferAmounts getTransferAmounts(
            List<TransferProcess> transferProcesses,
            Set<String> agreements
    ) {
        var numOk = transferProcesses.stream()
                .filter(transferProcess -> transferProcessStateService.getSimplifiedState(transferProcess.getState()).equals(OK))
                .filter(transferProcess -> agreements.contains(transferProcess.getDataRequest().getContractId()))
                .count();

        var numRunning = transferProcesses.stream()
                .filter(transferProcess -> transferProcessStateService.getSimplifiedState(transferProcess.getState()).equals(RUNNING))
                .filter(transferProcess -> agreements.contains(transferProcess.getDataRequest().getContractId()))
                .count();

        var numError = transferProcesses.stream()
                .filter(transferProcess -> transferProcessStateService.getSimplifiedState(transferProcess.getState()).equals(ERROR))
                .filter(transferProcess -> agreements.contains(transferProcess.getDataRequest().getContractId()))
                .count();

        return new DashboardTransferAmounts(numRunning, numOk, numError);
    }
}
