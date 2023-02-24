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

package de.sovity.edc.extension.postgresql.connection;

import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.system.configuration.Config;

public class JdbcConnectionProperties {

    @Setting(required = true)
    private static final String DATASOURCE_SETTING_JDBC_URL = "edc.datasource.%s.url";
    @Setting(required = true)
    private static final String DATASOURCE_SETTING_USER = "edc.datasource.%s.user";
    @Setting(required = true)
    private static final String DATASOURCE_SETTING_PASSWORD = "edc.datasource.%s.password";

    private final String jdbcUrl;
    private final String user;
    private final String password;

    public JdbcConnectionProperties(Config config, String entityName) {
        jdbcUrl = config.getString(String.format(DATASOURCE_SETTING_JDBC_URL, entityName));
        user = config.getString(String.format(DATASOURCE_SETTING_USER, entityName));
        password = config.getString(String.format(DATASOURCE_SETTING_PASSWORD, entityName));
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
