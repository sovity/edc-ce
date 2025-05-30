/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.db

import de.sovity.edc.ce.modules.db.DbExtension.JdbcCredentials
import org.testcontainers.containers.PostgreSQLContainer

class PostgresTestcontainer(
    val container: PostgreSQLContainer<*>
) {
    fun stop() {
        container.stop()
    }

    fun getJdbcCredentials(): JdbcCredentials =
        JdbcCredentials(
            url = container.jdbcUrl,
            user = container.username,
            password = container.password,
        )
}
