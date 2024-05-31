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

package de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services;

import de.sovity.edc.ext.wrapper.api.ui.model.DashboardDapsConfig;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;

import static com.apicatalog.jsonld.StringUtils.isBlank;
import static de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.ConfigPropertyUtils.configKey;

@RequiredArgsConstructor
public class DapsConfigService {
    private final Config config;

    private static final String DAPS_TOKEN_URL = configKey("EDC_OAUTH_TOKEN_URL");

    private static final String DAPS_JWKS_URL = configKey("EDC_OAUTH_PROVIDER_JWKS_URL");

    public DashboardDapsConfig buildDapsConfigOrNull() {
        var dapsConfig = new DashboardDapsConfig();
        dapsConfig.setTokenUrl(configValue(DAPS_TOKEN_URL));
        dapsConfig.setJwksUrl(configValue(DAPS_JWKS_URL));
        return isBlank(dapsConfig.getTokenUrl()) ? null : dapsConfig;
    }

    String configValue(String configKey) {
        return config.getString(configKey, "");
    }
}
