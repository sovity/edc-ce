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

import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.sql.ConnectionFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DriverManagerConnectionFactory implements ConnectionFactory {

    private static final String CONNECTION_PROPERTY_USER = "user";
    private static final String CONNECTION_PROPERTY_PASSWORD = "password";

    private final JdbcConnectionProperties jdbcProperties;

    public DriverManagerConnectionFactory(JdbcConnectionProperties jdbcProperties) {
        this.jdbcProperties = jdbcProperties;
    }

    @Override
    public Connection create() {
        try {
            var properties = new Properties();
            properties.setProperty(CONNECTION_PROPERTY_USER, jdbcProperties.getUser());
            properties.setProperty(CONNECTION_PROPERTY_PASSWORD, jdbcProperties.getPassword());
            return DriverManager.getConnection(jdbcProperties.getJdbcUrl(), properties);
        } catch (SQLException e) {
            throw new EdcPersistenceException(e);
        }
    }
}
