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

    @JvmStatic
    val SOVITY_TESTCONTAINER_POSTGRES_INITDB_ARGS = ConfigPropRef(
        property = "sovity.testcontainer.postgres.initdb.args",
        defaultDocumentation = "The arguments to pass to the Testcontainers' PostgreSQL's POSTGRES_INITDB_ARGS environment variable. e.g.\n-c shared_preload_libraries='pg_stat_statements'",
    )

    @JvmStatic
    val SOVITY_TESTCONTAINER_POSTGRES_INIT_SCRIPT = ConfigPropRef(
        property = "sovity.testcontainer.postgres.init.script",
        defaultDocumentation = "The name of the init script to execute at the creation of the container.",
    )
}
