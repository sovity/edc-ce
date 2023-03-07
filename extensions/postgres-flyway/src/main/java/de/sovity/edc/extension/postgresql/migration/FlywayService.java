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

package de.sovity.edc.extension.postgresql.migration;

import de.sovity.edc.extension.postgresql.connection.DriverManagerConnectionFactory;
import de.sovity.edc.extension.postgresql.connection.JdbcConnectionProperties;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.sql.datasource.ConnectionFactoryDataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.output.MigrateResult;
import org.flywaydb.core.api.output.RepairResult;

import javax.sql.DataSource;

public class FlywayService {

    private static final String MIGRATION_LOCATION_BASE = "classpath:migration";

    private final Monitor monitor;
    private final boolean tryRepairOnFailedMigration;

    public FlywayService(Monitor monitor, boolean tryRepairOnFailedMigration) {
        this.monitor = monitor;
        this.tryRepairOnFailedMigration = tryRepairOnFailedMigration;
    }

    public void migrateDatabase(
            String datasourceName,
            JdbcConnectionProperties jdbcConnectionProperties) {
        var flyway = setupFlyway(datasourceName, jdbcConnectionProperties);
        try {
            var migrateResult = flyway.migrate();
            handleFlywayMigrationResult(datasourceName, migrateResult);
        } catch (FlywayException e) {
            if (tryRepairOnFailedMigration) {
                repairAndRetryMigration(datasourceName, flyway);
            } else {
                throw new EdcPersistenceException("Flyway migration failed", e);
            }
        }
    }

    private void repairAndRetryMigration(String datasourceName, Flyway flyway) {
        try {
            var repairResult = flyway.repair();
            handleFlywayRepairResult(datasourceName, repairResult);
            var migrateResult = flyway.migrate();
            handleFlywayMigrationResult(datasourceName, migrateResult);
        } catch (FlywayException e) {
            throw new EdcPersistenceException("Flyway migration failed", e);
        }
    }

    private void handleFlywayRepairResult(String datasourceName, RepairResult repairResult) {
        if (!repairResult.repairActions.isEmpty()) {
            var repairActions = String.join(", ", repairResult.repairActions);
            monitor.info(String.format(
                    "Repair actions for datasource %s: %s",
                    datasourceName,
                    repairActions));
        }

        if (!repairResult.warnings.isEmpty()) {
            var warnings = String.join(", ", repairResult.warnings);
            throw new EdcPersistenceException(String.format(
                    "Repairing datasource %s failed: %s",
                    datasourceName,
                    warnings));
        }
    }

    private Flyway setupFlyway(
            String datasourceName,
            JdbcConnectionProperties jdbcConnectionProperties) {
        var dataSource = getDataSource(jdbcConnectionProperties);
        var migrationTableName = String.format("flyway_schema_history_%s", datasourceName);
        var migrationScriptLocation = String.join("/", MIGRATION_LOCATION_BASE, datasourceName);
        return Flyway.configure()
                .baselineVersion(MigrationVersion.fromVersion("0.0.0"))
                .baselineOnMigrate(true)
                .failOnMissingLocations(true)
                .dataSource(dataSource)
                .table(migrationTableName)
                .locations(migrationScriptLocation)
                .load();
    }

    private DataSource getDataSource(JdbcConnectionProperties jdbcConnectionProperties) {
        var connectionFactory = new DriverManagerConnectionFactory(jdbcConnectionProperties);
        return new ConnectionFactoryDataSource(connectionFactory);
    }

    private void handleFlywayMigrationResult(String datasourceName, MigrateResult migrateResult) {
        if (migrateResult.migrationsExecuted > 0) {
            monitor.info(String.format(
                    "Successfully migrated database for datasource %s " +
                            "from version %s to version %s",
                    datasourceName,
                    migrateResult.initialSchemaVersion,
                    migrateResult.targetSchemaVersion));
        } else {
            monitor.info(String.format(
                    "No migration necessary for datasource %s. Current version is %s",
                    datasourceName,
                    migrateResult.initialSchemaVersion));
        }
    }


}
