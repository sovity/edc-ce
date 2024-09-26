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

package de.sovity.edc.extension.postgresql.legacy.migration;

import de.sovity.edc.extension.postgresql.legacy.connection.JdbcConnectionProperties;
import de.sovity.edc.utils.config.ConfigProps;
import org.eclipse.edc.spi.system.configuration.Config;

import java.util.Arrays;
import java.util.List;

public class DatabaseMigrationManager {
    private static final String DEFAULT_DATASOURCE = "default";

    private final Config config;
    private final FlywayService flywayService;

    private final List<String> dataSourceNames = List.of(
            // Pre EDC 0 legacy migrations
            "asset",
            "contractdefinition",
            "policy",
            "contractnegotiation",
            "transferprocess",
            "dataplaneinstance",

            // Actual DB migrations
            "default"
    );

    public DatabaseMigrationManager(Config config, FlywayService flywayService) {
        this.config = config;
        this.flywayService = flywayService;
    }

    public void migrateAllDataSources() {
        flywayService.cleanDatabase(DEFAULT_DATASOURCE, new JdbcConnectionProperties(config, DEFAULT_DATASOURCE));
        for (String datasourceName : dataSourceNames) {
            var jdbcConnectionProperties = new JdbcConnectionProperties(config, datasourceName);
            List<String> additionalMigrationLocations = getAdditionalFlywayMigrationLocations(datasourceName);
            flywayService.migrateDatabase(datasourceName, jdbcConnectionProperties, additionalMigrationLocations);
        }
    }

    public List<String> getAdditionalFlywayMigrationLocations(String datasourceName) {
        // Only the default data has configurable additional migration scripts
        if (!datasourceName.equals(DEFAULT_DATASOURCE)) {
            return List.of();
        }

        String commaJoined = ConfigProps.EDC_FLYWAY_ADDITIONAL_MIGRATION_LOCATIONS.getStringOrEmpty(config);
        return Arrays.stream(commaJoined.split(","))
                .map(String::trim)
                .filter(it -> !it.isEmpty())
                .toList();
    }
}
