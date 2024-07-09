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

package de.sovity.edc.extension.postgresql;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.output.MigrateResult;
import org.flywaydb.core.api.output.RepairResult;

import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FlywayUtils {
    private final FlywayExecutionParams params;
    private final DataSource dataSource;

    public static void cleanAndMigrate(FlywayExecutionParams params, DataSource dataSource) {
        var instance = new FlywayUtils(params, dataSource);
        instance.cleanIfEnabled();
        instance.migrateOrValidate();
    }

    public static List<String> parseFlywayLocations(String locations) {
        return Arrays.stream(locations.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .toList();
    }

    private void cleanIfEnabled() {
        if (params.isClean()) {
            params.getInfoLogger().accept("Running flyway clean.");
            var flyway = setupFlyway();
            flyway.clean();
        }
    }

    private void migrateOrValidate() {
        if (params.isMigrate()) {
            migrate();
        } else {
            validate();
        }
    }

    private void validate() {
        var flyway = setupFlyway();
        flyway.validate();
    }

    private void migrate() {
        var flyway = setupFlyway();
        flyway.info().getInfoResult().migrations.stream()
                .map(migration -> "Found migration: %s".formatted(migration.filepath))
                .forEach(it -> params.getInfoLogger().accept(it));

        try {
            var migrateResult = flyway.migrate();
            handleFlywayMigrationResult(migrateResult);
        } catch (FlywayException e) {
            if (params.isTryRepairOnFailedMigration()) {
                repairAndRetryMigration(flyway);
            } else {
                throw new IllegalStateException("Flyway migration failed for '%s'"
                        .formatted(params.getTable()), e);
            }
        }
    }

    private void repairAndRetryMigration(Flyway flyway) {
        try {
            var repairResult = flyway.repair();
            handleFlywayRepairResult(repairResult);
            var migrateResult = flyway.migrate();
            handleFlywayMigrationResult(migrateResult);
        } catch (FlywayException e) {
            throw new IllegalStateException("Flyway migration failed for '%s'"
                    .formatted(params.getTable()), e);
        }
    }

    private void handleFlywayRepairResult(RepairResult repairResult) {
        if (!repairResult.repairActions.isEmpty()) {
            var repairActions = String.join(", ", repairResult.repairActions);
            params.getInfoLogger().accept("Repair actions for datasource %s: %s"
                    .formatted(params.getTable(), repairActions));
        }

        if (!repairResult.warnings.isEmpty()) {
            var warnings = String.join(", ", repairResult.warnings);
            throw new IllegalStateException("Repairing datasource %s failed: %s"
                    .formatted(params.getTable(), warnings));
        }
    }

    private Flyway setupFlyway() {
        params.getInfoLogger().accept("Flyway migration locations for '%s': %s".formatted(
                params.getTable(), params.getMigrationLocations()));
        return Flyway.configure()
                .baselineOnMigrate(true)
                .failOnMissingLocations(true)
                .dataSource(dataSource)
                .table(params.getTable())
                .locations(params.getMigrationLocations().toArray(new String[0]))
                .cleanDisabled(!params.isCleanEnabled())
                .load();
    }

    private void handleFlywayMigrationResult(MigrateResult migrateResult) {
        if (migrateResult.migrationsExecuted > 0) {
            params.getInfoLogger().accept(String.format(
                    "Successfully migrated database for datasource %s " +
                            "from version %s to version %s",
                    params.getTable(),
                    migrateResult.initialSchemaVersion,
                    migrateResult.targetSchemaVersion));
        } else {
            params.getInfoLogger().accept(String.format(
                    "No migration necessary for datasource %s. Current version is %s",
                    params.getTable(),
                    migrateResult.initialSchemaVersion));
        }
    }
}
