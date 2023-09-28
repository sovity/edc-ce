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
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementDirection;
import de.sovity.edc.ext.wrapper.api.ui.model.DashboardPage;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.ContractAgreementPageCardBuilder;
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
    private final ContractAgreementPageCardBuilder contractAgreementPageCardBuilder;
    private final AssetMapper assetMapper;
    private final PolicyMapper policyMapper;
    private final TransferProcessStateService transferProcessStateService;

    @NotNull
    public DashboardPage dashboardPage(String connectorEndpoint) {

        var runningTransferProcesses = dashboardDataFetcher.getTransferProcessesAmount().stream()
                .filter(transferProcess -> transferProcessStateService.getSimplifiedState(transferProcess.getState()).equals(RUNNING))
                .count();

        var completedTransferProcesses = dashboardDataFetcher.getTransferProcessesAmount().stream()
                .filter(transferProcess -> transferProcessStateService.getSimplifiedState(transferProcess.getState()).equals(OK))
                .count();

        var interruptedTransferProcesses = dashboardDataFetcher.getTransferProcessesAmount().stream()
                .filter(transferProcess -> transferProcessStateService.getSimplifiedState(transferProcess.getState()).equals(ERROR))
                .count();


        var negotiations = dashboardDataFetcher.getAllContractNegotiations();
        var numberOfProvidingAgreements = negotiations.stream()
                .filter(negotiation -> ContractAgreementDirection.fromType(negotiation.getType()).equals(ContractAgreementDirection.PROVIDING))
                .count();
        var numberOfConsumingAgreements = negotiations.stream()
                .filter(negotiation -> ContractAgreementDirection.fromType(negotiation.getType()).equals(ContractAgreementDirection.CONSUMING))
                .count();


        var numberOfAssets = dashboardDataFetcher.getAllAssets().stream().map(assetMapper::buildUiAsset).toList().size();

        var numberOfPolicies = dashboardDataFetcher.getAllPolicies().stream().map(policyMapper::buildUiPolicy).toList().size();

        return new DashboardPage(runningTransferProcesses, completedTransferProcesses, interruptedTransferProcesses, numberOfAssets, numberOfPolicies, numberOfConsumingAgreements, numberOfProvidingAgreements, connectorEndpoint);
    }
}
