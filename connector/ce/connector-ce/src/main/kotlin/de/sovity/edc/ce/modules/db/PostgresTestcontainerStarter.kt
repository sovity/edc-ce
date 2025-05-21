/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.db

import de.sovity.edc.ce.versions.GradleVersionsCe
import org.testcontainers.containers.PostgreSQLContainer

object PostgresTestcontainerStarter {
    fun startPostgresqlTestcontainer(
        username: String,
        password: String,
        databaseName: String,
        initDbArgs: String?,
        initScript: String?,
    ): PostgresTestcontainer {
        val container = PostgreSQLContainer(GradleVersionsCe.POSTGRES_IMAGE_TAG)
            .withUsername(username)
            .withPassword(password)
            .withDatabaseName(databaseName)

        initDbArgs?.let { args ->
            container.withEnv("POSTGRES_INITDB_ARGS", args)
        }

        initScript?.let { args ->
            container.withInitScript(args)
        }

        container.start()
        return PostgresTestcontainer(container)
    }
}
