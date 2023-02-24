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
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.output.MigrateResult;

import javax.sql.DataSource;

public class FlywayService {

    private static final String MIGRATION_LOCATION_BASE = "classpath:migration";

    private final Monitor monitor;

    public FlywayService(Monitor monitor) {
        this.monitor = monitor;
    }

    public void migrateDatabase(
            String datasourceName,
            JdbcConnectionProperties jdbcConnectionProperties) {
        var dataSource = getDataSource(jdbcConnectionProperties);
        var migrationTableName = String.format("flyway_schema_history_%s", datasourceName);
        var migrationScriptLocation = String.join("/", MIGRATION_LOCATION_BASE, datasourceName);
        final var flyway = Flyway.configure()
                .baselineVersion(MigrationVersion.fromVersion("0.0.0"))
                .failOnMissingLocations(true)
                .dataSource(dataSource)
                .table(migrationTableName)
                .locations(migrationScriptLocation)
                .load();
        flyway.baseline();
        var migrateResult = flyway.migrate();
        handleFlywayResult(datasourceName, migrateResult);
    }

    private DataSource getDataSource(JdbcConnectionProperties jdbcConnectionProperties) {
        var connectionFactory = new DriverManagerConnectionFactory(jdbcConnectionProperties);
        return new ConnectionFactoryDataSource(connectionFactory);
    }

    private void handleFlywayResult(String datasourceName, MigrateResult migrateResult) {
        if (migrateResult.success) {
            handleSuccessfulMigration(datasourceName, migrateResult);
        } else {
            handleFailedMigration(datasourceName, migrateResult);
        }
    }

    private void handleFailedMigration(String datasourceName, MigrateResult migrateResult) {
        var warnings = String.join(", ", migrateResult.warnings);
        throw new EdcPersistenceException(String.format(
                "Migration for datasource %s failed: %s",
                datasourceName,
                warnings));
    }

    private void handleSuccessfulMigration(String datasourceName, MigrateResult migrateResult) {
        if (migrateResult.migrationsExecuted > 0) {
            monitor.info(String.format(
                    "Successfully migrated database for datasource %s from version %s to version " +
                            "%s",
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
