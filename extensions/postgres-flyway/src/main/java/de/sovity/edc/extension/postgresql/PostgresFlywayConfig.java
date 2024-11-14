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
import de.sovity.edc.utils.config.CeConfigProps;
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
				// TODO: Switch to Vault for EE
                CeConfigProps.MY_EDC_JDBC_URL.getStringOrThrow(config),
                CeConfigProps.MY_EDC_JDBC_USER.getStringOrThrow(config),
                CeConfigProps.MY_EDC_JDBC_PASSWORD.getStringOrThrow(config)
            ),
            CeConfigProps.EDC_FLYWAY_REPAIR.getBoolean(config),
            CeConfigProps.EDC_FLYWAY_CLEAN_ENABLE.getBoolean(config),
            CeConfigProps.EDC_FLYWAY_CLEAN.getBoolean(config),
            CeConfigProps.EDC_FLYWAY_ADDITIONAL_MIGRATION_LOCATIONS.getStringOrEmpty(config),
            CeConfigProps.EDC_SERVER_DB_CONNECTION_POOL_SIZE.getInt(config),
            CeConfigProps.EDC_SERVER_DB_CONNECTION_TIMEOUT_IN_MS.getInt(config)
        );
    }
}
