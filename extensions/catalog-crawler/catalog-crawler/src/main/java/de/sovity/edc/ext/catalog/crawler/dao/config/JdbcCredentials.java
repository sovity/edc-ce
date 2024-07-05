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

import de.sovity.edc.ext.catalog.crawler.CrawlerExtension;
import org.apache.commons.lang3.Validate;
import org.eclipse.edc.spi.system.configuration.Config;

/**
 * JDBC Credentials
 *
 * @param jdbcUrl JDBC URL without credentials
 * @param jdbcUser JDBC User
 * @param jdbcPassword JDBC Password
 */
public record JdbcCredentials(
        String jdbcUrl,
        String jdbcUser,
        String jdbcPassword
) {
    public static JdbcCredentials fromConfig(Config config) {
        return new JdbcCredentials(
                getRequiredStringProperty(config, CrawlerExtension.JDBC_URL),
                getRequiredStringProperty(config, CrawlerExtension.JDBC_USER),
                getRequiredStringProperty(config, CrawlerExtension.JDBC_PASSWORD)
        );
    }

    private static String getRequiredStringProperty(Config config, String name) {
        String value = config.getString(name, "");
        Validate.notBlank(value, "EDC Property '%s' is required".formatted(name));
        return value;
    }
}
