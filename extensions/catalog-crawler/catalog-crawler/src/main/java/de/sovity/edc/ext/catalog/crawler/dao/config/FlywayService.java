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

package de.sovity.edc.ext.catalog.crawler.dao.config;

import de.sovity.edc.ext.catalog.crawler.CrawlerExtension;
import de.sovity.edc.extension.postgresql.FlywayExecutionParams;
import de.sovity.edc.extension.postgresql.FlywayUtils;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.configuration.Config;

import javax.sql.DataSource;

@RequiredArgsConstructor
public class FlywayService {
    private final Config config;
    private final Monitor monitor;
    private final DataSource dataSource;

    public void validateOrMigrateInTests() {
        var additionalLocations = config.getString(CrawlerExtension.DB_ADDITIONAL_FLYWAY_MIGRATION_LOCATIONS, "");

        var params = baseConfig(additionalLocations)
                .clean(config.getBoolean(CrawlerExtension.DB_CLEAN, false))
                .cleanEnabled(config.getBoolean(CrawlerExtension.DB_CLEAN_ENABLED, false))
                .migrate(config.getBoolean(CrawlerExtension.DB_MIGRATE, false))
                .infoLogger(monitor::info)
                .build();

        FlywayUtils.cleanAndMigrate(params, dataSource);
    }

    public static FlywayExecutionParams.FlywayExecutionParamsBuilder baseConfig(String additionalMigrationLocations) {
        var migrationLocations = FlywayUtils.parseFlywayLocations(
                "classpath:db-crawler/migration,%s".formatted(additionalMigrationLocations)
        );

        return FlywayExecutionParams.builder()
                .migrationLocations(migrationLocations)
                .table("flyway_schema_history");
    }
}
