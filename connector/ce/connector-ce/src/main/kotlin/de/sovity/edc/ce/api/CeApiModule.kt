/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api

import de.sovity.edc.runtime.modules.model.EdcModule

object CeApiModule {
    fun instance() = EdcModule(
        name = "sovity-ce-api",
        documentation = "sovity Community Edition EDC API Wrapper"
    ).apply {
        serviceExtensions(
            CeApiExtension::class.java
        )
    }
}
