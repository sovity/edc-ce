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

package de.sovity.edc.extension.db.directaccess;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.sovity.edc.utils.config.ConfigProps;
import lombok.val;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

@Provides(DslContextFactory.class)
public class DatabaseDirectAccessExtension implements ServiceExtension {
    public static final String NAME = "DirectDatabaseAccess";


    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        initializeDirectDatabaseAccess(context);
    }

    private void initializeDirectDatabaseAccess(ServiceExtensionContext context) {

        val hikariConfig = new HikariConfig();
        val config = context.getConfig();
        hikariConfig.setJdbcUrl(ConfigProps.EDC_DATASOURCE_DEFAULT_URL.getStringOrThrow(config));
        hikariConfig.setUsername(ConfigProps.EDC_DATASOURCE_DEFAULT_USER.getStringOrThrow(config));
        hikariConfig.setPassword(ConfigProps.EDC_DATASOURCE_DEFAULT_PASSWORD.getStringOrThrow(config));
        hikariConfig.setMinimumIdle(1);
        hikariConfig.setMaximumPoolSize(config.getInteger(ConfigProps.EDC_SERVER_DB_CONNECTION_POOL_SIZE.getProperty(), 3));
        hikariConfig.setIdleTimeout(30000);
        hikariConfig.setPoolName("direct-database-access");
        hikariConfig.setMaxLifetime(50000);
        hikariConfig.setConnectionTimeout(1000);

        val dda = new DslContextFactory(new HikariDataSource(hikariConfig));

        context.registerService(DslContextFactory.class, dda);
    }
}
