/*
 * Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.extension.e2e.junit;

import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseViaTestcontainers;
import de.sovity.edc.extension.e2e.junit.utils.InstancesForJunitTest;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.val;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Starts one DB and one EDC
 */
@Builder
@RequiredArgsConstructor
public final class TestDatabaseExtension
    implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    @Delegate(types = ParameterResolver.class)
    private final InstancesForJunitTest instances = new InstancesForJunitTest();

    @Getter
    private final TestDatabase testDatabase = new TestDatabaseViaTestcontainers();

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        System.out.println("Starting Test DB");
        testDatabase.beforeAll(extensionContext);
        System.out.printf("Test DB Started with %s%n", testDatabase.getJdbcCredentials());
        instances.put(testDatabase);
        instances.putLazy(DSLContext.class, () -> {
            val credentials = testDatabase.getJdbcCredentials();
            System.out.println("jdbcUser " + credentials.jdbcUser());
            System.out.println("jdbcPassword " + credentials.jdbcPassword());
            return DSL.using(credentials.jdbcUrl(), credentials.jdbcUser(), credentials.jdbcPassword());
        });
        instances.putLazy(Connection.class, () -> {
            val credentials = testDatabase.getJdbcCredentials();
            try {
                return DriverManager.getConnection(credentials.jdbcUrl(), credentials.jdbcUser(), credentials.jdbcPassword());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        System.out.println("Shutting down Test DB");
        testDatabase.afterAll(extensionContext);
        System.out.println("Test DB shut down");
    }
}
