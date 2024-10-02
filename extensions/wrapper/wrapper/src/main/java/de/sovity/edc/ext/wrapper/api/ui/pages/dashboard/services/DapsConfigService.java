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
import de.sovity.edc.utils.config.ConfigProps;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;

import static com.apicatalog.jsonld.StringUtils.isBlank;

@RequiredArgsConstructor
public class DapsConfigService {
    private final Config config;

    public DashboardDapsConfig buildDapsConfigOrNull() {
        var dapsConfig = new DashboardDapsConfig();
        dapsConfig.setTokenUrl(ConfigProps.EDC_OAUTH_TOKEN_URL.getStringOrNull(config));
        dapsConfig.setJwksUrl(ConfigProps.EDC_OAUTH_PROVIDER_JWKS_URL.getStringOrNull(config));
        return isBlank(dapsConfig.getTokenUrl()) ? null : dapsConfig;
    }
}
