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

package de.sovity.edc.extension.postgresql.utils;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.sql.SQLException;
import javax.sql.DataSource;

@RequiredArgsConstructor
public class DatabaseUtils {

    public static class TableNames {
        public static final String UNIFIED_MIGRATION_TABLE = "flyway_schema_history";
        public static final String DEFAULT_MIGRATION_TABLE = "flyway_schema_history_default";
    }

    private final DataSource dataSource;

    public boolean tableExists(String tableName) throws SQLException {
        try (
                val connection = dataSource.getConnection();
                val stmt = connection.prepareStatement(
                        "select exists (select 1 from information_schema.tables where table_schema = 'public' and table_name = ?)")) {
            stmt.setString(1, tableName);
            val result = stmt.executeQuery();
            if (!result.next()) {
                return false;
            }
            return result.getBoolean("exists");
        }
    }

    public boolean hasBaseline(String tableName, String baseline) throws SQLException {
        try (
                val connection = dataSource.getConnection();
                val stmt = connection.prepareStatement(
                        "select exists (select 1 from public." + tableName + " where type = 'BASELINE' and version = ?)")) {
            stmt.setString(1, baseline);
            val result = stmt.executeQuery();
            if (!result.next()) {
                return false;
            }
            return result.getBoolean("exists");
        }
    }

    public boolean hasUnifiedMigrations() throws SQLException {
        return tableExists(TableNames.UNIFIED_MIGRATION_TABLE);
    }

    public boolean hasDefaultMigrations() throws SQLException {
        return tableExists(TableNames.DEFAULT_MIGRATION_TABLE);
    }

    public boolean hasSplitHistory() throws SQLException {
        return tableExists("flyway_schema_history_asset") ||
                tableExists("flyway_schema_history_contractdefinition") ||
                tableExists("flyway_schema_history_contractnegotiation") ||
                tableExists("flyway_schema_history_dataplaneinstance") ||
                tableExists("flyway_schema_history_policy") ||
                tableExists("flyway_schema_history_transferprocess");
    }

    public boolean hasLegacyMigrations() throws SQLException {
        return hasSplitHistory() || hasDefaultMigrations();
    }

    public boolean hasUnifiedBaseline(String s) throws SQLException {
        return hasBaseline(TableNames.UNIFIED_MIGRATION_TABLE, s);
    }
}
