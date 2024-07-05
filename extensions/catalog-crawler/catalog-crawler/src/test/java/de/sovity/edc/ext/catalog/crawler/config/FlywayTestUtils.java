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

package de.sovity.edc.ext.catalog.crawler.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.eclipse.edc.spi.system.configuration.Config;
import org.flywaydb.core.Flyway;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FlywayTestUtils {

    public static void migrate(TestDatabase testDatabase) {
        var config = Flyway.configure()
                .dataSource(testDatabase.getDataSource())
                .baselineOnMigrate(true)
                .cleanDisabled(false)
                .table("crawler_test_migration_history")
                .locations("classpath:db/migration", "classpath:db/migration-test-utils");
        var flyway = new Flyway(config);
        flyway.clean();
        flyway.migrate();
    }
}
