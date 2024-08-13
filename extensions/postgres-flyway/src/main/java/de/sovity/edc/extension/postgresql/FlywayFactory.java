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

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.flywaydb.core.Flyway;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;

/**
 * Quickly launch {@link Flyway} from EDC Config
 */
@RequiredArgsConstructor
public class FlywayFactory {

    private final PostgresFlywayConfig config;

    public Flyway setupFlywayForUnifiedHistory(DataSource dataSource) {
        val locations = new ArrayList<String>();
        locations.add("classpath:db/migration");
        locations.addAll(getAdditionalFlywayMigrationLocations());

        return Flyway.configure()
                .dataSource(dataSource)
                .cleanDisabled(!config.flywayCleanEnabled())
                .baselineOnMigrate(true)
                .table("flyway_schema_history")
                .locations(locations.toArray(new String[0]))
                .load();
    }

    public Flyway setupFlywayForUnifiedHistoryFromLegacyDatabase(DataSource dataSource) {
        val locations = new ArrayList<String>();
        locations.add("classpath:db/migration");
        locations.addAll(getAdditionalFlywayMigrationLocations());

        return Flyway.configure()
                .dataSource(dataSource)
                .baselineVersion("8")
                .cleanDisabled(!config.flywayCleanEnabled())
                .table("flyway_schema_history")
                .locations(locations.toArray(new String[0]))
                .load();
    }

    public List<String> getAdditionalFlywayMigrationLocations() {
        String commaJoined = config.edcFlywayAdditionalMigrationLocations();
        return Arrays.stream(commaJoined.split(","))
                .map(String::trim)
                .filter(it -> !it.isEmpty())
                .toList();
    }
}
