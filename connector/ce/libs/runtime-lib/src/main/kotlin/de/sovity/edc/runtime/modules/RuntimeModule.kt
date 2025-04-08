/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.runtime.modules

import de.sovity.edc.runtime.modules.model.ConfigPropCategory
import de.sovity.edc.runtime.modules.model.EdcModule

@Suppress("MaxLineLength")
object RuntimeModule {
    fun instance() = EdcModule(
        name = "runtime",
        documentation = """
            Documents properties that neither fit the CE nor the EE, but will definitely be part of each
            EDC because they are features of our custom runtime or custom test runtime.
        """.trimIndent()
    ).apply {
        property(
            ConfigPropCategory.OPTIONAL,
            RuntimeConfigProps.SOVITY_PRINT_CONFIG
        )
        property(
            ConfigPropCategory.OVERRIDES,
            RuntimeConfigProps.SOVITY_FIRST_PORT
        )
        property(
            ConfigPropCategory.OPTIONAL,
            RuntimeConfigProps.SOVITY_TESTCONTAINER_POSTGRES_INITDB_ARGS,
        )
        property(
            ConfigPropCategory.OPTIONAL,
            RuntimeConfigProps.SOVITY_TESTCONTAINER_POSTGRES_INIT_SCRIPT,
        )

        documentDockerImageEnvVar(
            envVarName = "REMOTE_DEBUG",
            requiredOrDefault = """
                Default value `false`
                Default set in `docker-entrypoint.sh`
            """.trimIndent(),
            documentation = "Docker Image Remote Debugging: Enables Java Remote Debugging if set to `true`"
        )

        documentDockerImageEnvVar(
            envVarName = "REMOTE_DEBUG_SUSPEND",
            requiredOrDefault = """
                Default value `false`
                Default set in `docker-entrypoint.sh`
            """.trimIndent(),
            documentation = "Docker Image Remote Debugging: Suspend the application on startup if set to `true`. Requires remote debugging to be enabled."
        )

        documentDockerImageEnvVar(
            envVarName = "REMOTE_DEBUG_SUSPEND",
            requiredOrDefault = """
                Default value `127.0.0.1:5005`
                Default set in `docker-entrypoint.sh`
            """.trimIndent(),
            documentation = "Docker Image Remote Debugging: Changes the bind address the remote debugger will bind itself to. Can be used to change the port, too. Requires remote debugging to be enabled."
        )

        documentDockerImageEnvVar(
            envVarName = "LOGGING_KIND",
            requiredOrDefault = """
                Default value `console`
                Default set in `docker-entrypoint.sh`
            """.trimIndent(),
            documentation = "Docker Image Logging: Changes the Log4j XML to the given logging kind. Allowed values are `console` or `json`."
        )

        documentDockerImageEnvVar(
            envVarName = "LOGGING_LEVEL",
            requiredOrDefault = """
                Default value `INFO`
                Default set in `docker-entrypoint.sh`
            """.trimIndent(),
            documentation = "Docker Image Logging: Changes the Log4j XML to the given logging level. Allowed values are `INFO` or `DEBUG`."
        )

        documentDockerImageEnvVar(
            envVarName = "JAVA_ARGS",
            requiredOrDefault = "Optional",
            documentation = "Docker Image: Additional Java arguments to be passed to the JVM."
        )
    }
}
