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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.experimental.UtilityClass;

import javax.sql.DataSource;

@UtilityClass
public class HikariDataSourceFactory {
    /**
     * Create a new {@link DataSource}.
     * <br>
     * This method is static, so we can use from test code.
     *
     * @param jdbcCredentials jdbc credentials
     * @param maxPoolSize max pool size
     * @param connectionTimeoutInMs connection timeout in ms
     * @return {@link DataSource}.
     */
    public static HikariDataSource newDataSource(
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
        hikariConfig.setPoolName("edc-server");
        hikariConfig.setMaxLifetime(50000);
        hikariConfig.setConnectionTimeout(connectionTimeoutInMs);

        return new HikariDataSource(hikariConfig);
    }
}
