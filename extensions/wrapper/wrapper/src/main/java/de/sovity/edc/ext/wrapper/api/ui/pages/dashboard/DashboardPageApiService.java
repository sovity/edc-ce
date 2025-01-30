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

import de.sovity.edc.ext.db.jooq.Tables;
import de.sovity.edc.ext.wrapper.api.ui.model.DashboardPage;
import de.sovity.edc.ext.wrapper.api.ui.model.DashboardTransferAmounts;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.CxDidConfigService;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.DapsConfigService;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.DashboardDataFetcher;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.SelfDescriptionService;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferProcessStateService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.connector.controlplane.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static de.sovity.edc.ext.wrapper.api.ui.model.TransferProcessSimplifiedState.ERROR;
import static de.sovity.edc.ext.wrapper.api.ui.model.TransferProcessSimplifiedState.OK;
import static de.sovity.edc.ext.wrapper.api.ui.model.TransferProcessSimplifiedState.RUNNING;
import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class DashboardPageApiService {
    private final DashboardDataFetcher dashboardDataFetcher;
    private final TransferProcessStateService transferProcessStateService;
    private final DapsConfigService dapsConfigService;
    private final CxDidConfigService cxDidConfigService;
    private final SelfDescriptionService selfDescriptionService;

    @NotNull
    public DashboardPage dashboardPage(DSLContext dsl) {
        var transferProcesses = dashboardDataFetcher.getTransferProcesses();

        val n = Tables.EDC_CONTRACT_NEGOTIATION;
        val a = Tables.EDC_CONTRACT_AGREEMENT;

        val providingAgreementsDb = dsl
            .select(a.AGR_ID)
            .from(n)
            .leftJoin(a)
            .on(n.AGREEMENT_ID.eq(a.AGR_ID))
            .where(n.TYPE.eq(ContractNegotiation.Type.PROVIDER.toString()))
            .fetchSet(a.AGR_ID);

        val consumingAgreementsDb = dsl
            .select(a.AGR_ID)
            .from(n)
            .leftJoin(a)
            .on(n.AGREEMENT_ID.eq(a.AGR_ID))
            .where(n.TYPE.eq(ContractNegotiation.Type.CONSUMER.toString()))
            .fetchSet(a.AGR_ID);

        DashboardPage dashboardPage = new DashboardPage();
        dashboardPage.setNumAssets(dashboardDataFetcher.getNumberOfAssets(dsl));
        dashboardPage.setNumPolicies(dashboardDataFetcher.getNumberOfPolicies(dsl));
        dashboardPage.setNumContractDefinitions(dashboardDataFetcher.getNumberOfContractDefinitions(dsl));
        dashboardPage.setNumContractAgreementsProviding(providingAgreementsDb.size());
        dashboardPage.setNumContractAgreementsConsuming(consumingAgreementsDb.size());
        dashboardPage.setTransferProcessesProviding(getTransferAmounts(transferProcesses, providingAgreementsDb));
        dashboardPage.setTransferProcessesConsuming(getTransferAmounts(transferProcesses, consumingAgreementsDb));

        dashboardPage.setConnectorTitle(selfDescriptionService.getConnectorTitle());
        dashboardPage.setConnectorDescription(selfDescriptionService.getConnectorDescription());
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
