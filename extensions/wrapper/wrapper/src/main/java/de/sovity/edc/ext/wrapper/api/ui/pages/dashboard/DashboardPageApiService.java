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

import de.sovity.edc.ext.wrapper.api.ui.model.DashboardPage;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.DapsConfigService;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.MiwConfigService;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.SelfDescriptionService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class DashboardPageApiService {
    private final DapsConfigService dapsConfigService;
    private final MiwConfigService miwConfigService;
    private final SelfDescriptionService selfDescriptionService;

    @NotNull
    public DashboardPage dashboardPage() {
        var dashboardPage = new DashboardPage();
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
        return dashboardPage;
    }
}
