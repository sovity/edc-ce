/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce

import de.sovity.edc.ce.api.ui.model.UiConfigFeature
import de.sovity.edc.ce.config.CeConfigProps
import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.DocumentedFn
import de.sovity.edc.runtime.modules.model.EdcModule

object EdcUiModule {
    /**
     * Preconfigures the given EdcModule / EDC distribution to enable the given UI features by default
     */
    fun withActivatedFeatures(
        category: ConfigPropCategory,
        uiFeaturesTitle: String,
        uiFeatures: List<UiConfigFeature>
    ) = EdcModule(
        name = "edc-ui",
        documentation = "Configuration for the EDC UI"
    ).apply {
        property(
            category,
            CeConfigProps.SOVITY_EDC_UI_FEATURES
        ) {
            defaultValueFn = DocumentedFn(
                "$uiFeaturesTitle:\n${uiFeatures.sorted().joinToString("\n") { " * `$it`" }}"
            ) {
                uiFeatures.joinToString(",") { it.name }
            }
        }

        property(
            category,
            CeConfigProps.SOVITY_EDC_UI_FEATURES_ADD
        )

        property(
            category,
            CeConfigProps.SOVITY_EDC_UI_FEATURES_EXCLUDE
        )

        property(
            category,
            CeConfigProps.SOVITY_EDC_UI_PRECONFIGURED_COUNTERPARTIES
        )

        property(
            category,
            CeConfigProps.SOVITY_EDC_UI_LOGOUT_URL
        )

        property(
            category,
            CeConfigProps.SOVITY_EDC_UI_SUPPORT_URL
        )

        property(
            category,
            CeConfigProps.SOVITY_EDC_UI_DOCUMENTATION_URL
        )

        property(
            category,
            CeConfigProps.SOVITY_EDC_UI_LEGAL_NOTICE_URL
        )

        property(
            category,
            CeConfigProps.SOVITY_EDC_UI_PRIVACY_POLICY_URL
        )
    }
}
