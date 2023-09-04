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

package de.sovity.edc.extension.e2e.db;

import lombok.RequiredArgsConstructor;

import static de.sovity.edc.extension.e2e.env.EnvUtil.getEnvVar;

@RequiredArgsConstructor
public class TestDatabaseViaEnvVars implements TestDatabase {
    public static final String SKIP_TESTCONTAINERS = "SKIP_TESTCONTAINERS";
    public static final String TEST_POSTGRES_JDBC_URL = "TEST_POSTGRES_%d_JDBC_URL";
    public static final String TEST_POSTGRES_JDBC_USER = "TEST_POSTGRES_%d_JDBC_USER";
    public static final String TEST_POSTGRES_JDBC_PASSWORD = "TEST_POSTGRES_%d_JDBC_PASSWORD";

    private final int iDatabase;

    public JdbcCredentials getJdbcCredentials() {
        return new JdbcCredentials(
                getEnvVar(TEST_POSTGRES_JDBC_URL.formatted(iDatabase)),
                getEnvVar(TEST_POSTGRES_JDBC_USER.formatted(iDatabase)),
                getEnvVar(TEST_POSTGRES_JDBC_PASSWORD.formatted(iDatabase))
        );
    }

    public static boolean isSkipTestcontainers() {
        return "true".equals(System.getenv(SKIP_TESTCONTAINERS));
    }
}
