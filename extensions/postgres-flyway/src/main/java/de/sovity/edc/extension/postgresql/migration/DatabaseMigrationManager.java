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

import de.sovity.edc.extension.postgresql.connection.JdbcConnectionProperties;
import org.eclipse.edc.spi.system.configuration.Config;

import java.util.List;

public class DatabaseMigrationManager {
    private static final String EDC_DATASOURCE_PREFIX = "edc.datasource";

    private final Config config;
    private final FlywayService flywayService;

    public DatabaseMigrationManager(Config config, FlywayService flywayService) {
        this.config = config;
        this.flywayService = flywayService;
    }

    public void repairAllDataSources() {
        for (String datasourceName : getDataSourceNames(config)) {
            var jdbcConnectionProperties = new JdbcConnectionProperties(config, datasourceName);
            flywayService.repair(datasourceName, jdbcConnectionProperties);
        }
    }

    public void migrateAllDataSources() {
        for (String datasourceName : getDataSourceNames(config)) {
            var jdbcConnectionProperties = new JdbcConnectionProperties(config, datasourceName);
            flywayService.migrateDatabase(datasourceName, jdbcConnectionProperties);
        }
    }

    private List<String> getDataSourceNames(Config config) {
        var edcDatasourceConfig = config.getConfig(EDC_DATASOURCE_PREFIX);
        return edcDatasourceConfig.partition().toList().stream()
                .map(Config::currentNode)
                .toList();
    }
}
