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

import de.sovity.edc.extension.postgresql.migration.DatabaseMigrationManager;
import de.sovity.edc.extension.postgresql.migration.FlywayService;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

public class PostgresFlywayExtension implements ServiceExtension {


    @Setting
    public static final String EDC_DATASOURCE_REPAIR_SETTING = "edc.flyway.repair";
    @Setting
    public static final String FLYWAY_CLEAN_ENABLED = "edc.flyway.clean.enable";
    @Setting
    public static final String FLYWAY_CLEAN = "edc.flyway.clean";

    @Override
    public String name() {
        return "Postgres Flyway Extension";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var flywayService = new FlywayService(
                context.getMonitor(),
                context.getSetting(EDC_DATASOURCE_REPAIR_SETTING, false),
                context.getSetting(FLYWAY_CLEAN_ENABLED, false),
                context.getSetting(FLYWAY_CLEAN, false)
        );
        var migrationManager = new DatabaseMigrationManager(context.getConfig(), flywayService);
        migrationManager.migrateAllDataSources();
    }

}
