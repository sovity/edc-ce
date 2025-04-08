/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.dashboard;

import de.sovity.edc.ce.api.ui.model.DashboardPage;
import de.sovity.edc.ce.api.ui.model.DashboardTransferAmounts;
import de.sovity.edc.ce.api.ui.pages.dashboard.services.CxDidConfigService;
import de.sovity.edc.ce.api.ui.pages.dashboard.services.DapsConfigService;
import de.sovity.edc.ce.api.ui.pages.dashboard.services.DashboardDataFetcher;
import de.sovity.edc.ce.api.ui.pages.dashboard.services.SelfDescriptionService;
import de.sovity.edc.ce.api.ui.pages.transferhistory.TransferProcessStateService;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.controlplane.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static de.sovity.edc.ce.api.ui.model.TransferProcessSimplifiedState.ERROR;
import static de.sovity.edc.ce.api.ui.model.TransferProcessSimplifiedState.OK;
import static de.sovity.edc.ce.api.ui.model.TransferProcessSimplifiedState.RUNNING;
import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
@Service
public class DashboardPageApiService {
    private final DashboardDataFetcher dashboardDataFetcher;
    private final TransferProcessStateService transferProcessStateService;
    private final DapsConfigService dapsConfigService;
    private final CxDidConfigService cxDidConfigService;
    private final SelfDescriptionService selfDescriptionService;

    @NotNull
    public DashboardPage dashboardPage() {
        var transferProcesses = dashboardDataFetcher.getTransferProcesses();
        var negotiations = dashboardDataFetcher.getAllContractNegotiations();

        var providingAgreements = negotiations.stream()
            .filter(it -> it.getType() == ContractNegotiation.Type.PROVIDER)
            .map(ContractNegotiation::getContractAgreement)
            .filter(Objects::nonNull)
            .map(ContractAgreement::getId)
            .collect(toSet());


        var consumingAgreements = negotiations.stream()
            .filter(it -> it.getType() == ContractNegotiation.Type.CONSUMER)
            .map(ContractNegotiation::getContractAgreement)
            .filter(Objects::nonNull)
            .map(ContractAgreement::getId)
            .collect(toSet());

        DashboardPage dashboardPage = new DashboardPage();
        dashboardPage.setNumAssets(dashboardDataFetcher.getNumberOfAssets());
        dashboardPage.setNumPolicies(dashboardDataFetcher.getNumberOfPolicies());
        dashboardPage.setNumContractDefinitions(dashboardDataFetcher.getNumberOfContractDefinitions());
        dashboardPage.setNumContractAgreementsProviding(providingAgreements.size());
        dashboardPage.setNumContractAgreementsConsuming(consumingAgreements.size());
        dashboardPage.setTransferProcessesProviding(getTransferAmounts(transferProcesses, providingAgreements));
        dashboardPage.setTransferProcessesConsuming(getTransferAmounts(transferProcesses, consumingAgreements));

        dashboardPage.setConnectorTitle(selfDescriptionService.getConnectorTitle());
        dashboardPage.setConnectorDescription(selfDescriptionService.getConnectorDescription());
        dashboardPage.setManagementApiUrlShownInDashboard(selfDescriptionService.getManagementApiUrlShownInUiDashboard());
        dashboardPage.setConnectorEndpoint(selfDescriptionService.getConnectorEndpoint());
        dashboardPage.setConnectorParticipantId(selfDescriptionService.getParticipantId());

        dashboardPage.setConnectorCuratorUrl(selfDescriptionService.getCuratorUrl());
        dashboardPage.setConnectorCuratorName(selfDescriptionService.getCuratorName());
        dashboardPage.setConnectorMaintainerUrl(selfDescriptionService.getMaintainerUrl());
        dashboardPage.setConnectorMaintainerName(selfDescriptionService.getMaintainerName());

        dashboardPage.setConnectorCxDidConfig(cxDidConfigService.buildCxDidConfigOrNull());
        dashboardPage.setConnectorDapsConfig(dapsConfigService.buildDapsConfigOrNull());
        return dashboardPage;
    }

    DashboardTransferAmounts getTransferAmounts(
        List<TransferProcess> transferProcesses,
        Set<String> agreements
    ) {
        var numTotal = transferProcesses.stream()
            .filter(transferProcess -> agreements.contains(transferProcess.getContractId()))
            .count();

        var numOk = transferProcesses.stream()
            .filter(transferProcess -> agreements.contains(transferProcess.getContractId()))
            .filter(transferProcess -> transferProcessStateService.getSimplifiedState(transferProcess.getState()).equals(OK))
            .count();

        var numRunning = transferProcesses.stream()
            .filter(transferProcess -> agreements.contains(transferProcess.getContractId()))
            .filter(transferProcess -> transferProcessStateService.getSimplifiedState(transferProcess.getState()).equals(RUNNING))
            .count();

        var numError = transferProcesses.stream()
            .filter(transferProcess -> agreements.contains(transferProcess.getContractId()))
            .filter(transferProcess -> transferProcessStateService.getSimplifiedState(transferProcess.getState()).equals(ERROR))
            .count();

        return new DashboardTransferAmounts(numTotal, numRunning, numOk, numError);
    }
}
