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

import org.apache.commons.lang3.Validate;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Objects;

public class TestDatabaseViaEnv implements TestDatabase {
    public static final String SKIP_TESTCONTAINERS = "SKIP_TESTCONTAINERS";
    public static final String TEST_POSTGRES_JDBC_URL = "TEST_POSTGRES_JDBC_URL";
    public static final String TEST_POSTGRES_JDBC_USER = "TEST_POSTGRES_JDBC_USER";
    public static final String TEST_POSTGRES_JDBC_PASSWORD = "TEST_POSTGRES_JDBC_PASSWORD";

    @Override
    public void afterAll(ExtensionContext context) throws Exception {

    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {

    }

    public String getJdbcUrl() {
        return getRequiredEnv(TEST_POSTGRES_JDBC_URL);
    }

    public String getJdbcUser() {
        return getRequiredEnv(TEST_POSTGRES_JDBC_USER);
    }

    public String getJdbcPassword() {
        return getRequiredEnv(TEST_POSTGRES_JDBC_PASSWORD);
    }

    private static String getRequiredEnv(String name) {
        String value = System.getenv(name);
        Validate.notBlank(value, "Need env var %s since %s is true", name, SKIP_TESTCONTAINERS);
        return value;
    }

    public static boolean isSkipTestcontainers() {
        return "true".equals(System.getenv(SKIP_TESTCONTAINERS));
    }
}
