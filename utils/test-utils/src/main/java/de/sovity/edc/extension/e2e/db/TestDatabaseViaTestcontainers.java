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

import de.sovity.edc.utils.versions.GradleVersions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class TestDatabaseViaTestcontainers implements TestDatabase {
    private static final String POSTGRES_USER = "postgres";
    private static final String POSTGRES_PASSWORD = "postgres";
    private static final String POSTGRES_DB = "edc";

    private final PostgreSQLContainer<?> container;

    public TestDatabaseViaTestcontainers() {
        container = new PostgreSQLContainer<>(GradleVersions.POSTGRES_IMAGE_TAG)
                .withUsername(POSTGRES_USER)
                .withPassword(POSTGRES_PASSWORD)
                .withDatabaseName(POSTGRES_DB);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        container.stop();
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        container.start();
    }
    @Override
    public JdbcCredentials getJdbcCredentials() {
        return new JdbcCredentials(container.getJdbcUrl(), container.getUsername(), container.getPassword());
    }

}
