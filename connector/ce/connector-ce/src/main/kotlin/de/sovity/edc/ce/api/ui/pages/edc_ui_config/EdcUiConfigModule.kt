/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.edc_ui_config

import de.sovity.edc.runtime.modules.model.EdcModule

object EdcUiConfigModule {
    fun instance() = EdcModule(
        name = "edc-ui-config",
        documentation = "Api endpoint that passes configuration to the EDC UI."
    ).apply {
        serviceExtensions(
            EdcUiConfigExtension::class.java
        )
    }
}
