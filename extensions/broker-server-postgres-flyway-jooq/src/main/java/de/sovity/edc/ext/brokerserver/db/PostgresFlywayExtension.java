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

package de.sovity.edc.ext.brokerserver.db;

import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

public class PostgresFlywayExtension implements ServiceExtension {
    @Setting(required = true)
    public static final String JDBC_URL = "edc.datasource.default.url";
    @Setting(required = true)
    public static final String JDBC_USER = "edc.datasource.default.user";
    @Setting(required = true)
    public static final String JDBC_PASSWORD = "edc.datasource.default.password";
    @Setting
    public static final String FLYWAY_REPAIR = "edc.flyway.repair";
    @Setting
    public static final String FLYWAY_CLEAN_ENABLE = "edc.flyway.clean.enable";
    @Setting
    public static final String FLYWAY_CLEAN = "edc.flyway.clean";
    @Setting
    public static final String DB_CONNECTION_POOL_SIZE = "edc.broker.server.db.connection.pool.size";
    @Setting
    public static final String DB_CONNECTION_TIMEOUT_IN_MS = "edc.broker.server.db.connection.timeout.in.ms";

    @Override
    public String name() {
        return "Postgres Flyway Extension (Broker Server)";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var config = context.getConfig();
        var monitor = context.getMonitor();

        var dataSourceFactory = new DataSourceFactory(config);
        var dataSource = dataSourceFactory.newDataSource();

        var flywayFactory = new FlywayFactory(config);
        var flyway = flywayFactory.setupFlyway(dataSource);
        var flywayMigrator = new FlywayMigrator(flyway, config, monitor);
        flywayMigrator.migrateAndRepair();
    }
}
