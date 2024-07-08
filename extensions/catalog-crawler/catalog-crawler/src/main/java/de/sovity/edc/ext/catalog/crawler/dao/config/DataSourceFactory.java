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

package de.sovity.edc.ext.catalog.crawler.dao.config;

import com.zaxxer.hikari.HikariDataSource;
import de.sovity.edc.ext.catalog.crawler.CrawlerExtension;
import de.sovity.edc.extension.postgresql.HikariDataSourceFactory;
import de.sovity.edc.extension.postgresql.JdbcCredentials;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
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
    public HikariDataSource newDataSource() {
        var jdbcCredentials = getJdbcCredentials();
        int maxPoolSize = config.getInteger(CrawlerExtension.DB_CONNECTION_POOL_SIZE);
        int connectionTimeoutInMs = config.getInteger(CrawlerExtension.DB_CONNECTION_TIMEOUT_IN_MS);
        return HikariDataSourceFactory.newDataSource(
                jdbcCredentials,
                maxPoolSize,
                connectionTimeoutInMs
        );
    }


    public JdbcCredentials getJdbcCredentials() {
        return new JdbcCredentials(
                getRequiredStringProperty(config, CrawlerExtension.JDBC_URL),
                getRequiredStringProperty(config, CrawlerExtension.JDBC_USER),
                getRequiredStringProperty(config, CrawlerExtension.JDBC_PASSWORD)
        );
    }

    private String getRequiredStringProperty(Config config, String name) {
        String value = config.getString(name, "");
        Validate.notBlank(value, "EDC Property '%s' is required".formatted(name));
        return value;
    }

}
