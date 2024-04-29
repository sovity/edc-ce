/*
 *  Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial implementation
 *
 */

package de.sovity.edc.ext.brokerserver.db;

import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.spi.system.configuration.Config;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;


/**
 * Custom flyway migration logic and logging.
 */
@RequiredArgsConstructor
public class FlywayMigrator {
    private final Flyway flyway;
    private final Config config;
    private final Monitor monitor;

    /**
     * Run migrations and potentially run flyway repair
     */
    public void migrateAndRepair() {
        if (config.getBoolean(PostgresFlywayExtension.FLYWAY_CLEAN, false)) {
            monitor.info("Cleaning database before migrations, since %s=true and %s=true.".formatted(
                    PostgresFlywayExtension.FLYWAY_CLEAN_ENABLE, PostgresFlywayExtension.FLYWAY_CLEAN
            ));
            flyway.clean();
        }
        try {
            migrate();
        } catch (FlywayException e) {
            if (isFlywayRepairEnabled()) {
                try {
                    repair();
                    migrate();
                } catch (FlywayException e2) {
                    throw migrationFailed(e2);
                }
            } else {
                throw migrationFailed(e);
            }
        }
    }

    private void migrate() {
        var migrateResult = flyway.migrate();
        if (migrateResult.migrationsExecuted > 0) {
            monitor.info(String.format(
                    "Successfully migrated database from version %s to version %s",
                    migrateResult.initialSchemaVersion,
                    migrateResult.targetSchemaVersion
            ));
        } else {
            monitor.info(String.format(
                    "No migration necessary. Current version is %s",
                    migrateResult.initialSchemaVersion
            ));
        }
    }

    private void repair() {
        var repairResult = flyway.repair();
        if (!repairResult.repairActions.isEmpty()) {
            var repairActions = String.join(", ", repairResult.repairActions);
            monitor.info(String.format("Flyway Repair actions: %s", repairActions));
        }

        if (!repairResult.warnings.isEmpty()) {
            var warnings = String.join(", ", repairResult.warnings);
            throw new EdcPersistenceException(String.format("Flyway Repair failed: %s", warnings));
        }
    }

    private boolean isFlywayRepairEnabled() {
        return config.getBoolean(PostgresFlywayExtension.FLYWAY_REPAIR, true);
    }


    private EdcPersistenceException migrationFailed(FlywayException e) {
        return new EdcPersistenceException("Flyway migration failed", e);
    }
}
