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

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class TestDatabaseViaTestcontainers implements TestDatabase {

    private static final int POSTGRES_PORT = 5432;
    private static final String POSTGRES_USER = "postgres";
    private static final String POSTGRES_PASSWORD = "postgres";
    private static final String POSTGRES_DB = "edc";

    private final PostgreSQLContainer<?> container;

    public TestDatabaseViaTestcontainers(int hostPort) {
        var portBinding = new PortBinding(
                Ports.Binding.bindPort(hostPort),
                new ExposedPort(POSTGRES_PORT));
        container = new PostgreSQLContainer<>("postgres:15-alpine")
                .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(new HostConfig().withPortBindings(portBinding)))
                .withUsername(POSTGRES_USER)
                .withPassword(POSTGRES_PASSWORD)
                .withDatabaseName(POSTGRES_DB)
                .withExposedPorts(POSTGRES_PORT);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        container.stop();
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        container.start();
    }
    @Override
    public JdbcCredentials getJdbcCredentials() {
        return new JdbcCredentials(getJdbcUrl(), getJdbcUser(), getJdbcPassword());
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
