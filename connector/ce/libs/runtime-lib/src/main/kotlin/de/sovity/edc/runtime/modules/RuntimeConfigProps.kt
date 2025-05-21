/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules

import de.sovity.edc.runtime.modules.model.ConfigPropRef

object RuntimeConfigProps {

    @JvmStatic
    val SOVITY_EDC_CONFIG_JSON = ConfigPropRef(
        property = "sovity.edc.config.json",
        defaultDocumentation = "Additional Config as a JSON object. This takes less precedence than the ENV Vars. This helps with Bash environments where dots or dashes in environment variables are hard to deal with. Can be set as `SOVITY_EDC_CONFIG_JSON`."
    )

    @JvmStatic
    val SOVITY_PRINT_CONFIG = ConfigPropRef(
        property = "sovity.print.config",
        defaultDocumentation = "Prints out config, dependencies and service extensions at startup if set to 'true'. Do not enable for productive connectors as it will leak confidential data to the log!",
    )

    @JvmStatic
    val SOVITY_ENVIRONMENT = ConfigPropRef(
        property = "sovity.environment",
        defaultDocumentation = "Deployment Environment as required for either defaulting or requiring config such as FQDN"
    )

    @JvmStatic
    val SOVITY_FIRST_PORT = ConfigPropRef(
        property = "sovity.first.port",
        defaultDocumentation = """
            The first port of several ports to be used for the several API endpoints.
            If set to 11000, the Management API will be on 11002, etc.

            During tests it can be set to 'auto' for the test framework to auto find a free port-range.
        """.trimIndent(),
    )
}
