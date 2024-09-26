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
import de.sovity.edc.utils.config.ConfigProps;
import org.eclipse.edc.spi.system.configuration.Config;

public record PostgresFlywayConfig(
    JdbcCredentials jdbcCredentials,
    boolean flywayRepair,
    boolean flywayCleanEnabled,
    boolean flywayClean,
    String edcFlywayAdditionalMigrationLocations,
    int poolSize,
    int connectionTimeoutInMs
) {

    public static PostgresFlywayConfig fromConfig(Config config) {
        return new PostgresFlywayConfig(
            new JdbcCredentials(
                ConfigProps.EDC_DATASOURCE_DEFAULT_URL.getStringOrNull(config),
                ConfigProps.EDC_DATASOURCE_DEFAULT_USER.getStringOrNull(config),
                ConfigProps.EDC_DATASOURCE_DEFAULT_PASSWORD.getStringOrNull(config)
            ),
            ConfigProps.EDC_FLYWAY_REPAIR.getBoolean(config),
            ConfigProps.EDC_FLYWAY_CLEAN_ENABLE.getBoolean(config),
            ConfigProps.EDC_FLYWAY_CLEAN.getBoolean(config),
            ConfigProps.EDC_FLYWAY_ADDITIONAL_MIGRATION_LOCATIONS.getStringOrEmpty(config),
            ConfigProps.EDC_SERVER_DB_CONNECTION_POOL_SIZE.getInt(config),
            ConfigProps.EDC_SERVER_DB_CONNECTION_TIMEOUT_IN_MS.getInt(config)
        );
    }
}
