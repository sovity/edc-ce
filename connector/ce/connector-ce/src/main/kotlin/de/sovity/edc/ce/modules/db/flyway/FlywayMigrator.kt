/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.db.flyway

import de.sovity.edc.ce.config.CeConfigProps.SOVITY_FLYWAY_CLEAN
import de.sovity.edc.ce.config.CeConfigProps.SOVITY_FLYWAY_CLEAN_ENABLE
import org.eclipse.edc.spi.monitor.Monitor
import org.eclipse.edc.spi.persistence.EdcPersistenceException
import org.eclipse.edc.spi.system.configuration.Config
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException

class FlywayMigrator(
    private val config: Config,
    private val monitor: Monitor
) {
    fun cleanAndMigrate(flyway: Flyway) {
        cleanDatabase(flyway)
        migrate(flyway)
    }

    private fun cleanDatabase(flyway: Flyway) {
        val shouldClean = SOVITY_FLYWAY_CLEAN.getBooleanOrFalse(config)
        val canClean = SOVITY_FLYWAY_CLEAN_ENABLE.getBooleanOrFalse(config)
        if (shouldClean) {
            check(canClean) {
                "In order to clean the history both $SOVITY_FLYWAY_CLEAN and $SOVITY_FLYWAY_CLEAN_ENABLE must be set to true."
            }
            monitor.info("Cleaning database before migrations, since $SOVITY_FLYWAY_CLEAN=true and $SOVITY_FLYWAY_CLEAN_ENABLE=true")
            flyway.clean()
        }
    }

    private fun migrate(flyway: Flyway) {
        try {
            flyway.migrate()
        } catch (e: FlywayException) {
            throw EdcPersistenceException("Flyway migration failed", e)
        }
    }
}
