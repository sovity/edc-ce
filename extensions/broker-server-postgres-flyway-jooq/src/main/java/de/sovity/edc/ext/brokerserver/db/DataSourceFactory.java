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

    /**
     * Create a new {@link DataSource} from EDC Config.
     *
     * @return {@link DataSource}.
     */
    public DataSource newDataSource() {
        var jdbcCredentials = JdbcCredentials.fromConfig(config);
        return fromJdbcCredentials(jdbcCredentials);
    }

    /**
     * Create a new {@link DataSource} from JDBC Credentials.
     * <br>
     * This method was extracted into a static method, so we can call it from our Test Code.
     *
     * @param jdbcCredentials jdbc credentials
     * @return {@link DataSource}
     */
    public static DataSource fromJdbcCredentials(JdbcCredentials jdbcCredentials) {
        return new ConnectionFactoryDataSource(() -> newConnection(jdbcCredentials));
    }

    private static Connection newConnection(JdbcCredentials jdbcCredentials) {
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
