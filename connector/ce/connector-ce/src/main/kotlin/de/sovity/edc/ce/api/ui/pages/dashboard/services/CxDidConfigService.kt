/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.dashboard.services

import de.sovity.edc.ce.api.ui.model.DashboardCxDidConfig
import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.spi.system.configuration.Config

@Service
class CxDidConfigService(
    private val config: Config
) {
    fun buildCxDidConfigOrNull(): DashboardCxDidConfig? {
        val cxDidConfig = DashboardCxDidConfig()
        cxDidConfig.myDid = CeConfigProps.EDC_IAM_ISSUER_ID.getStringOrEmpty(config)
        cxDidConfig.walletTokenUrl = CeConfigProps.EDC_IAM_STS_OAUTH_TOKEN_URL.getStringOrEmpty(config)
        cxDidConfig.trustedVcIssuer = CeConfigProps.EDC_IAM_TRUSTED_ISSUER_COFINITY_ID.getStringOrEmpty(config).ifBlank {
            CeConfigProps.EDC_IAM_TRUSTED_ISSUER_SPHINX_ID.getStringOrEmpty(config)
        }

        cxDidConfig.dimUrl = CeConfigProps.TX_EDC_IAM_STS_DIM_URL.getStringOrEmpty(config)
        cxDidConfig.bdrsUrl = CeConfigProps.TX_IAM_IATP_BDRS_SERVER_URL.getStringOrEmpty(config)

        return if (cxDidConfig.myDid.isNullOrBlank()) null else cxDidConfig
    }
}
