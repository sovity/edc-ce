/*
 *  Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.extension.postgresql;

import de.sovity.edc.extension.postgresql.utils.JdbcCredentials;
import org.apache.commons.lang3.Validate;
import org.eclipse.edc.spi.system.configuration.Config;

public record PostgresFlywayConfig(
        JdbcCredentials jdbcCredentials,
        boolean flywayRepair,
        boolean flywayCleanEnabled,
        boolean flywayClean,
        String edcFlywayAdditionalMigrationLocations,
        int poolSize,
        int connectionTimeoutInMs) {

    public static PostgresFlywayConfig fromConfig(Config config) {
        return new PostgresFlywayConfig(
                new JdbcCredentials(
                        getRequiredStringProperty(config, PostgresFlywayExtension.JDBC_URL),
                        getRequiredStringProperty(config, PostgresFlywayExtension.JDBC_USER),
                        getRequiredStringProperty(config, PostgresFlywayExtension.JDBC_PASSWORD)
                ),
                config.getBoolean(PostgresFlywayExtension.EDC_DATASOURCE_REPAIR_SETTING, false),
                config.getBoolean(PostgresFlywayExtension.FLYWAY_CLEAN_ENABLE, false),
                config.getBoolean(PostgresFlywayExtension.FLYWAY_CLEAN, false),
                config.getString(PostgresFlywayExtension.EDC_FLYWAY_ADDITIONAL_MIGRATION_LOCATIONS, ""),
                config.getInteger(PostgresFlywayExtension.DB_CONNECTION_POOL_SIZE, 3),
                config.getInteger(PostgresFlywayExtension.DB_CONNECTION_TIMEOUT_IN_MS, 5000)
        );
    }

    public static String getRequiredStringProperty(Config config, String name) {
        String value = config.getString(name, "");
        Validate.notBlank(value, "EDC Property '%s' is required".formatted(name));
        return value;
    }

}
