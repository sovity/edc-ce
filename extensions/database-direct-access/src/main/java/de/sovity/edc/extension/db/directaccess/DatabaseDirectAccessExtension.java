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
 */

package de.sovity.edc.extension.db.directaccess;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.val;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

@Provides(DirectDatabaseAccess.class)
public class DatabaseDirectAccessExtension implements ServiceExtension {
    public static final String NAME = "DirectDatabaseAccess";

    /**
     * The JDBC URL.
     */
    @Setting(required = true)
    public static final String EDC_DATASOURCE_DEFAULT_URL = "edc.datasource.default.url";

    /**
     * The JDBC user.
     */
    @Setting(required = true)
    public static final String EDC_DATASOURCE_JDBC_USER = "edc.datasource.default.user";

    /**
     * The JDBC password.
     */
    @Setting(required = true)
    public static final String EDC_DATASOURCE_JDBC_PASSWORD = "edc.datasource.default.password";

    /**
     * Sets the connection pool size to use during the flyway migration.
     */
    @Setting(defaultValue = "3")
    public static final String EDC_SERVER_DB_CONNECTION_POOL_SIZE = "edc.server.db.connection.pool.size";

    /**
     * Sets the connection timeout for the datasource in milliseconds.
     */
    @Setting(defaultValue = "5000")
    public static final String EDC_SERVER_DB_CONNECTION_TIMEOUT_IN_MS = "edc.server.db.connection.timeout.in.ms";


    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        initializeDirectDatabaseAccess(context);
    }

    private void initializeDirectDatabaseAccess(ServiceExtensionContext context) {

        val dda = new DirectDatabaseAccess(() -> {

            val hikariConfig = new HikariConfig();
            val config = context.getConfig();
            hikariConfig.setJdbcUrl(config.getString(EDC_DATASOURCE_DEFAULT_URL));
            hikariConfig.setUsername(config.getString(EDC_DATASOURCE_JDBC_USER));
            hikariConfig.setPassword(config.getString(EDC_DATASOURCE_JDBC_PASSWORD));
            hikariConfig.setMinimumIdle(1);
            hikariConfig.setMaximumPoolSize(config.getInteger(EDC_SERVER_DB_CONNECTION_POOL_SIZE));
            hikariConfig.setIdleTimeout(30000);
            hikariConfig.setPoolName("direct-database-access");
            hikariConfig.setMaxLifetime(50000);
            hikariConfig.setConnectionTimeout(config.getInteger(EDC_SERVER_DB_CONNECTION_TIMEOUT_IN_MS));

            return new HikariDataSource(hikariConfig);
        });

        context.registerService(DirectDatabaseAccess.class, dda);
    }
}
