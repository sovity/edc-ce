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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.sovity.edc.ext.brokerserver.db.utils.JdbcCredentials;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;

import javax.sql.DataSource;

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
        int maxPoolSize = config.getInteger(PostgresFlywayExtension.DB_CONNECTION_POOL_SIZE);
        int connectionTimeoutInMs = config.getInteger(PostgresFlywayExtension.DB_CONNECTION_TIMEOUT_IN_MS);
        return newDataSource(jdbcCredentials, maxPoolSize, connectionTimeoutInMs);
    }

    /**
     * Create a new {@link DataSource}.
     * <br>
     * This method is static, so we can use from test code.
     *
     * @param jdbcCredentials       jdbc credentials
     * @param maxPoolSize           max pool size
     * @param connectionTimeoutInMs connection timeout in ms
     * @return {@link DataSource}.
     */
    public static DataSource newDataSource(
            JdbcCredentials jdbcCredentials,
            int maxPoolSize,
            int connectionTimeoutInMs
    ) {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcCredentials.jdbcUrl());
        hikariConfig.setUsername(jdbcCredentials.jdbcUser());
        hikariConfig.setPassword(jdbcCredentials.jdbcPassword());
        hikariConfig.setMinimumIdle(1);
        hikariConfig.setMaximumPoolSize(maxPoolSize);
        hikariConfig.setIdleTimeout(30000);
        hikariConfig.setPoolName("edc-broker-server");
        hikariConfig.setMaxLifetime(50000);
        hikariConfig.setConnectionTimeout(connectionTimeoutInMs);

        return new HikariDataSource(hikariConfig);
    }
}
