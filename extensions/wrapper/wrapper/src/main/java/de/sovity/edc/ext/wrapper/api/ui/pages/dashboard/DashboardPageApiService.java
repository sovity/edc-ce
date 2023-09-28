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

import de.sovity.edc.ext.wrapper.api.common.mappers.AssetMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementCard;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementDirection;
import de.sovity.edc.ext.wrapper.api.ui.model.DashboardPage;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.DashboardDataFetcher;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferProcessStateService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import static de.sovity.edc.ext.wrapper.api.ui.model.TransferProcessSimplifiedState.ERROR;
import static de.sovity.edc.ext.wrapper.api.ui.model.TransferProcessSimplifiedState.OK;
import static de.sovity.edc.ext.wrapper.api.ui.model.TransferProcessSimplifiedState.RUNNING;
import static de.sovity.edc.ext.wrapper.utils.EdcDateUtils.utcSecondsToOffsetDateTime;

@RequiredArgsConstructor
public class DashboardPageApiService {
    private final DashboardDataFetcher dashboardDataFetcher;
    private final AssetMapper assetMapper;
    private final PolicyMapper policyMapper;
    private final TransferProcessStateService transferProcessStateService;

    @NotNull
    public DashboardPage dashboardPage(String connectorEndpoint) {

        var transferProcesses = dashboardDataFetcher.getTransferProcesses();

        var runningTransferProcesses = transferProcesses.stream()
                .filter(transferProcess -> transferProcessStateService.getSimplifiedState(transferProcess.getState()).equals(RUNNING))
                .count();

        var completedTransferProcesses = transferProcesses.stream()
                .filter(transferProcess -> transferProcessStateService.getSimplifiedState(transferProcess.getState()).equals(OK))
                .count();

        var interruptedTransferProcesses = transferProcesses.stream()
                .filter(transferProcess -> transferProcessStateService.getSimplifiedState(transferProcess.getState()).equals(ERROR))
                .count();


        var negotiations = dashboardDataFetcher.getAllContractNegotiations();
        var numberOfProvidingAgreements = negotiations.stream()
                .filter(negotiation -> ContractAgreementDirection.fromType(negotiation.getType()).equals(ContractAgreementDirection.PROVIDING))
                .count();
        var numberOfConsumingAgreements = negotiations.stream()
                .filter(negotiation -> ContractAgreementDirection.fromType(negotiation.getType()).equals(ContractAgreementDirection.CONSUMING))
                .count();

        DashboardPage dashboard = new DashboardPage();
        dashboard.setNumberOfAssets(dashboardDataFetcher.getNumberOfAssets());
        dashboard.setNumberOfPolicies(dashboardDataFetcher.getNumberOfPolicies());
        dashboard.setCompletedTransferProcesses(completedTransferProcesses);
        dashboard.setRunningTransferProcesses(runningTransferProcesses);
        dashboard.setInterruptedTransferProcesses(interruptedTransferProcesses);
        dashboard.setNumberOfProvidingAgreements(numberOfProvidingAgreements);
        dashboard.setNumberOfConsumingAgreements(numberOfConsumingAgreements);
        dashboard.setEndpoint(connectorEndpoint);
        return dashboard;
    }
}
