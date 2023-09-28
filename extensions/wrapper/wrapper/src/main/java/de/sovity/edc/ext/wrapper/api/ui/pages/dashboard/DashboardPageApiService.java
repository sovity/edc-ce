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

import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementDirection;
import de.sovity.edc.ext.wrapper.api.ui.model.DashboardPage;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.DashboardDataFetcher;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferProcessStateService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import static de.sovity.edc.ext.wrapper.api.ui.model.TransferProcessSimplifiedState.ERROR;
import static de.sovity.edc.ext.wrapper.api.ui.model.TransferProcessSimplifiedState.OK;
import static de.sovity.edc.ext.wrapper.api.ui.model.TransferProcessSimplifiedState.RUNNING;

@RequiredArgsConstructor
public class DashboardPageApiService {
    private final DashboardDataFetcher dashboardDataFetcher;
    private final TransferProcessStateService transferProcessStateService;

    @NotNull
    public DashboardPage dashboardPage(String connectorEndpoint) {

        var transferProcesses = dashboardDataFetcher.getTransferProcesses();

        var runningTransferProcesses = transferProcesses.stream()
                .filter(transferProcess -> transferProcessStateService.getSimplifiedState(transferProcess.getState()).equals(RUNNING))
                .toList();
        var completedTransferProcesses = transferProcesses.stream()
                .filter(transferProcess -> transferProcessStateService.getSimplifiedState(transferProcess.getState()).equals(OK))
                .toList();
        var interruptedTransferProcesses = transferProcesses.stream()
                .filter(transferProcess -> transferProcessStateService.getSimplifiedState(transferProcess.getState()).equals(ERROR))
                .toList();


        var negotiations = dashboardDataFetcher.getAllContractNegotiations();


        var providingAgreements = negotiations.stream()
                .filter(negotiation -> ContractAgreementDirection.fromType(negotiation.getType()).equals(ContractAgreementDirection.PROVIDING))
                .map(negotiation -> negotiation.getContractAgreement().getId())
                .toList();

        var completedProvidingTransferProcesses = completedTransferProcesses.stream()
                .filter(transferProcess -> providingAgreements.contains(transferProcess.getDataRequest().getContractId()))
                .count();
        var interruptedProvidingTransferProcesses = interruptedTransferProcesses.stream()
                .filter(transferProcess -> providingAgreements.contains(transferProcess.getDataRequest().getContractId()))
                .count();
        var runningProvidingTransferProcesses = runningTransferProcesses.stream()
                .filter(transferProcess -> providingAgreements.contains(transferProcess.getDataRequest().getContractId()))
                .count();


        var consumingAgreements = negotiations.stream()
                .filter(negotiation -> ContractAgreementDirection.fromType(negotiation.getType()).equals(ContractAgreementDirection.CONSUMING))
                .map(negotiation -> negotiation.getContractAgreement().getId())
                .toList();

        var completedConsumingTransferProcesses = completedTransferProcesses.stream()
                .filter(transferProcess -> consumingAgreements.contains(transferProcess.getDataRequest().getContractId()))
                .count();
        var interruptedConsumingTransferProcesses = interruptedTransferProcesses.stream()
                .filter(transferProcess -> consumingAgreements.contains(transferProcess.getDataRequest().getContractId()))
                .count();
        var runningConsumingTransferProcesses = runningTransferProcesses.stream()
                .filter(transferProcess -> consumingAgreements.contains(transferProcess.getDataRequest().getContractId()))
                .count();

        DashboardPage dashboard = new DashboardPage();
        dashboard.setNumberOfAssets(dashboardDataFetcher.getNumberOfAssets());
        dashboard.setNumberOfPolicies(dashboardDataFetcher.getNumberOfPolicies());
        dashboard.setNumberOfCompletedProvidingTransferProcesses(completedProvidingTransferProcesses);
        dashboard.setNumberOfInterruptedProvidingTransferProcesses(interruptedProvidingTransferProcesses);
        dashboard.setNumberOfRunningProvidingTransferProcesses(runningProvidingTransferProcesses);
        dashboard.setNumberOfCompletedConsumingTransferProcesses(completedConsumingTransferProcesses);
        dashboard.setNumberOfInterruptedConsumingTransferProcesses(interruptedConsumingTransferProcesses);
        dashboard.setNumberOfRunningConsumingTransferProcesses(runningConsumingTransferProcesses);
        dashboard.setNumberOfProvidingAgreements(providingAgreements.size());
        dashboard.setNumberOfConsumingAgreements(consumingAgreements.size());
        dashboard.setEndpoint(connectorEndpoint);
        return dashboard;
    }
}
