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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.eclipse.edc.monitor.logger.LoggerMonitor;
import org.eclipse.edc.spi.system.configuration.Config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FlywayTestUtils {

    public static void migrate(TestDatabase testDatabase) {
        var monitor = new LoggerMonitor();
        var config = mock(Config.class);
        when(config.getBoolean(eq(PostgresFlywayExtension.FLYWAY_CLEAN_ENABLE), any())).thenReturn(true);
        when(config.getBoolean(eq(PostgresFlywayExtension.FLYWAY_CLEAN), any())).thenReturn(true);

        var flywayFactory = new FlywayFactory(config);
        var dataSource = testDatabase.getDataSource();
        var flyway = flywayFactory.setupFlyway(dataSource);
        var flywayMigrator = new FlywayMigrator(flyway, config, monitor);
        flywayMigrator.migrateAndRepair();
    }
}
