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
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

public class PostgresFlywayExtension implements ServiceExtension {
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

        val migrator = new FlywayMigrator(extensionConfig, dataSource, context.getMonitor());
        migrator.updateDatabaseWithLegacyHandling();
    }

    @Override
    public void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
