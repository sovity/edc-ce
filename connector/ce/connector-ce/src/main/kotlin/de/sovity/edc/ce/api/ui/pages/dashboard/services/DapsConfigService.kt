/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.dashboard.services

import de.sovity.edc.ce.api.ui.model.DashboardDapsConfig
import de.sovity.edc.ce.config.CeConfigProps.EDC_OAUTH_PROVIDER_JWKS_URL
import de.sovity.edc.ce.config.CeConfigProps.EDC_OAUTH_TOKEN_URL
import de.sovity.edc.runtime.simple_di.Service
import lombok.RequiredArgsConstructor
import org.eclipse.edc.spi.system.configuration.Config

@RequiredArgsConstructor
@Service
class DapsConfigService(
    private val config: Config
) {
    fun buildDapsConfigOrNull(): DashboardDapsConfig? {
        val dapsConfig = DashboardDapsConfig()
        dapsConfig.tokenUrl = EDC_OAUTH_TOKEN_URL.getStringOrNull(config)
        dapsConfig.jwksUrl = EDC_OAUTH_PROVIDER_JWKS_URL.getStringOrNull(config)
        return if (dapsConfig.tokenUrl.isNullOrEmpty()) null else dapsConfig
    }
}
