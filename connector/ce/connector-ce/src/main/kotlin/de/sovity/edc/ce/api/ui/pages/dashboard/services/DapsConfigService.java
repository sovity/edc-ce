/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.dashboard.services;

import de.sovity.edc.ce.api.ui.model.DashboardDapsConfig;
import de.sovity.edc.ce.config.CeConfigProps;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;

import static com.apicatalog.jsonld.StringUtils.isBlank;

@RequiredArgsConstructor
@Service
public class DapsConfigService {
    private final Config config;

    public DashboardDapsConfig buildDapsConfigOrNull() {
        var dapsConfig = new DashboardDapsConfig();
        dapsConfig.setTokenUrl(CeConfigProps.getEDC_OAUTH_TOKEN_URL().getStringOrNull(config));
        dapsConfig.setJwksUrl(CeConfigProps.getEDC_OAUTH_PROVIDER_JWKS_URL().getStringOrNull(config));
        return isBlank(dapsConfig.getTokenUrl()) ? null : dapsConfig;
    }
}
