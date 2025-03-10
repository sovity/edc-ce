/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.config_utils

import de.sovity.edc.runtime.modules.model.EdcModule

object ConfigUtilsModule {
    fun instance() = EdcModule("sovity-edc-ee-config-utils", "Provides instance of ConfigUtils for the EE").apply {
        serviceExtensions(ConfigUtilsExtension::class.java)
    }
}
