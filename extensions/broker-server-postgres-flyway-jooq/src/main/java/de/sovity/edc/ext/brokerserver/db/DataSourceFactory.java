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

import de.sovity.edc.ext.brokerserver.db.utils.JdbcCredentials;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.sql.datasource.ConnectionFactoryDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * Create {@link DataSource}s from EDC Config.
 */
@RequiredArgsConstructor
public class DataSourceFactory {
    private final Config config;

    public DataSource newDataSource() {
        var jdbcCredentials = JdbcCredentials.fromConfig(config);
        return new ConnectionFactoryDataSource(() -> newConnection(jdbcCredentials));
    }

    private Connection newConnection(JdbcCredentials jdbcCredentials) {
        try {
            return DriverManager.getConnection(
                    jdbcCredentials.jdbcUrl(),
                    jdbcCredentials.jdbcUser(),
                    jdbcCredentials.jdbcPassword()
            );
        } catch (SQLException e) {
            throw new EdcPersistenceException(e);
        }
    }
}
