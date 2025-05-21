/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.dashboard

import de.sovity.edc.ce.api.ui.model.DashboardPage
import de.sovity.edc.ce.api.ui.model.DashboardTransferAmounts
import de.sovity.edc.ce.api.ui.pages.dashboard.model.DashboardDao
import de.sovity.edc.ce.api.ui.pages.dashboard.services.CxDidConfigService
import de.sovity.edc.ce.api.ui.pages.dashboard.services.DapsConfigService
import de.sovity.edc.ce.api.ui.pages.dashboard.services.SelfDescriptionService
import de.sovity.edc.runtime.simple_di.Service
import org.jooq.DSLContext

@Service
class DashboardPageApiService(
    private val dapsConfigService: DapsConfigService,
    private val cxDidConfigService: CxDidConfigService,
    private val selfDescriptionService: SelfDescriptionService,
    private val dashboardDao: DashboardDao,
) {
    fun dashboardPage(
        dsl: DSLContext
    ): DashboardPage {

        val dashboardRs = dashboardDao.getDashboardPage(dsl)

        val dashboardPage = DashboardPage()
        dashboardPage.numAssets = dashboardRs.numAssets
        dashboardPage.numPolicies = dashboardRs.numPolicies
        dashboardPage.numContractDefinitions = dashboardRs.numContractDefinitions
        dashboardPage.numContractAgreementsProviding = dashboardRs.numContractAgreementsProviding
        dashboardPage.numContractAgreementsConsuming = dashboardRs.numContractAgreementsConsuming

        dashboardPage.transferProcessesConsuming = DashboardTransferAmounts(
            dashboardRs.transferProcessesConsumingTotal,
            dashboardRs.transferProcessesConsumingRunning,
            dashboardRs.transferProcessesConsumingOk,
            dashboardRs.transferProcessesConsumingError,
        )
        dashboardPage.transferProcessesProviding = DashboardTransferAmounts(
            dashboardRs.transferProcessesProvidingTotal,
            dashboardRs.transferProcessesProvidingRunning,
            dashboardRs.transferProcessesProvidingOk,
            dashboardRs.transferProcessesProvidingError,
        )

        dashboardPage.connectorTitle = selfDescriptionService.connectorTitle
        dashboardPage.connectorDescription = selfDescriptionService.connectorDescription
        dashboardPage.managementApiUrlShownInDashboard = selfDescriptionService.managementApiUrlShownInUiDashboard
        dashboardPage.connectorEndpoint = selfDescriptionService.connectorEndpoint
        dashboardPage.connectorParticipantId = selfDescriptionService.participantId

        dashboardPage.connectorCuratorUrl = selfDescriptionService.curatorUrl
        dashboardPage.connectorCuratorName = selfDescriptionService.curatorName
        dashboardPage.connectorMaintainerUrl = selfDescriptionService.maintainerUrl
        dashboardPage.connectorMaintainerName = selfDescriptionService.maintainerName

        dashboardPage.connectorCxDidConfig = cxDidConfigService.buildCxDidConfigOrNull()
        dashboardPage.connectorDapsConfig = dapsConfigService.buildDapsConfigOrNull()

        return dashboardPage
    }
}
