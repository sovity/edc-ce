/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.config

import de.sovity.edc.ce.api.ui.model.UiConfig
import de.sovity.edc.ce.api.ui.model.UiConfigFeature
import de.sovity.edc.ce.api.ui.model.UiConfigPreconfiguredCounterparty
import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.runtime.modules.model.ConfigPropRef
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.spi.system.configuration.Config

@Service
class UiConfigApiService(
    config: Config,
) {
    private val features = getFeatures(config, CeConfigProps.SOVITY_EDC_UI_FEATURES) +
            getFeatures(config, CeConfigProps.SOVITY_EDC_UI_FEATURES_ADD) -
            getFeatures(config, CeConfigProps.SOVITY_EDC_UI_FEATURES_EXCLUDE).toSet()

    private val preconfiguredCounterparties = getPreconfiguredCounterparties(config)
    private val logoutUrl = CeConfigProps.SOVITY_EDC_UI_LOGOUT_URL.getStringOrNull(config)
    private val documentationUrl = CeConfigProps.SOVITY_EDC_UI_DOCUMENTATION_URL.getStringOrNull(config)
    private val supportUrl = CeConfigProps.SOVITY_EDC_UI_SUPPORT_URL.getStringOrNull(config)

    fun uiConfig(): UiConfig {
        return UiConfig.builder()
            .features(features)
            .preconfiguredCounterparties(preconfiguredCounterparties)
            .logoutUrl(logoutUrl)
            .documentationUrl(documentationUrl)
            .supportUrl(supportUrl)
            .build()
    }

    private fun getFeatures(config: Config, configPropRef: ConfigPropRef) =
        configPropRef
            .getListOrEmpty(config)
            .map { UiConfigFeature.valueOf(it) }

    private fun getPreconfiguredCounterparties(config: Config) =
        CeConfigProps.SOVITY_EDC_UI_PRECONFIGURED_COUNTERPARTIES
            .getListOrEmpty(config)
            .map {
                val split = it.split("?participantId=", limit = 2)
                require(split.size == 2) { "Invalid preconfigured counterparty: $it" }
                UiConfigPreconfiguredCounterparty.builder()
                    .connectorEndpoint(split[0])
                    .participantId(split[1])
                    .build()
            }
}
