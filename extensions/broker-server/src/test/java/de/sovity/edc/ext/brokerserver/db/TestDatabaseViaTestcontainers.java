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

import de.sovity.edc.utils.versions.GradleVersions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class TestDatabaseViaTestcontainers implements TestDatabase {
    private PostgreSQLContainer<?> container = new PostgreSQLContainer<>(GradleVersions.POSTGRES_IMAGE_TAG)
        .withUsername("edc")
        .withPassword("edc");

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        container.stop();
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        container.start();
    }

    public String getJdbcUrl() {
        return container.getJdbcUrl();
    }

    public String getJdbcUser() {
        return container.getUsername();
    }

    public String getJdbcPassword() {
        return container.getPassword();
    }
}
