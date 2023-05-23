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

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

/**
 * Quickly launch {@link Flyway} from EDC Config
 */
@RequiredArgsConstructor
public class FlywayFactory {

    /**
     * Configure and launch {@link Flyway}.
     *
     * @param dataSource data source
     * @return {@link Flyway}
     */
    public Flyway setupFlyway(DataSource dataSource) {
        return Flyway.configure()
                .baselineOnMigrate(true)
                .dataSource(dataSource)
                .table("flyway_schema_history")
                .locations("classpath:db/migration")
                .load();
    }
}
