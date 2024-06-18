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

import com.zaxxer.hikari.HikariDataSource;
import lombok.val;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

public class PostgresFlywayExtension implements ServiceExtension {

    /**
     * The JDBC URL.
     */
    @Setting(required = true)
    public static final String JDBC_URL = "edc.datasource.default.url";

    /**
     * The JDBC user.
     */
    @Setting(required = true)
    public static final String JDBC_USER = "edc.datasource.default.user";

    /**
     * The JDBC password.
     */
    @Setting(required = true)
    public static final String JDBC_PASSWORD = "edc.datasource.default.password";

    /**
     * Attempts to fix the history when a migration fails. Only supported in older migration scripts.
     */
    @Setting(defaultValue = "false")
    public static final String EDC_DATASOURCE_REPAIR_SETTING = "edc.flyway.repair";

    /**
     * Allows the deletion of the database. Goes in pair with {@link #FLYWAY_CLEAN}. Both options must be enabled for a clean to happen.
     */
    @Setting(defaultValue = "false")
    public static final String FLYWAY_CLEAN_ENABLE = "edc.flyway.clean.enable";

    /**
     * Request the deletion of the database. Goes in pair with {@link #FLYWAY_CLEAN_ENABLE}. Both options must be enabled for a clean to happen.
     */
    @Setting(defaultValue = "false")
    public static final String FLYWAY_CLEAN = "edc.flyway.clean";

    /**
     * Sets the connection pool size to use during the flyway migration.
     */
    @Setting(defaultValue = "3")
    public static final String DB_CONNECTION_POOL_SIZE = "edc.server.db.connection.pool.size";

    /**
     * Sets the connection timeout for the datasource in milliseconds.
     */
    @Setting(defaultValue = "5000")
    public static final String DB_CONNECTION_TIMEOUT_IN_MS = "edc.server.db.connection.timeout.in.ms";

    /**
     * Coma-separated list of additional migration scripts files.
     * <br/>
     * Additional Migration Scripts can be added by specifying the configuration property
     * `edc.flyway.additional.migration.locations`.
     * <br/>
     * Values are comma separated and need to be correct <a href="https://flywaydb.org/documentation/configuration/parameters/locations">FlyWay migration script locations</a>.
     * These migration scripts need to be compatible with the migrations in {@code resources/db/migration} and {@code resources/db/migration/legacy}.
     * <br/>
     */
    @Setting(defaultValue = "")
    public static final String EDC_FLYWAY_ADDITIONAL_MIGRATION_LOCATIONS = "edc.flyway.additional.migration.locations";


    private HikariDataSource dataSource;

    @Override
    public String name() {
        return "Postgres Flyway Extension";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        val legacyConfig = context.getConfig();
        val extensionConfig = PostgresFlywayConfig.fromConfig(legacyConfig);

        val dataSourceFactory = new DataSourceFactory(extensionConfig);
        dataSource = dataSourceFactory.newDataSource();

        val migrator = new FlywayMigrator(extensionConfig, legacyConfig, dataSource, context.getMonitor());
        migrator.updateDatabaseWithLegacyHandling();
    }

}
