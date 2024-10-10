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
import de.sovity.edc.extension.e2e.junit.multi.InstancesForJunitTest;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * Starts one DB and one EDC
 */
@Builder
@RequiredArgsConstructor
public final class TestDatabaseExtension
    implements BeforeAllCallback, AfterAllCallback, ParameterResolver {
    private final InstancesForJunitTest instances = new InstancesForJunitTest();

    @Getter
    private final TestDatabase testDatabase = new TestDatabaseViaTestcontainers();

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        testDatabase.beforeAll(extensionContext);
        instances.put(testDatabase);
        instances.putLazy(DSLContext.class, () -> {
            val credentials = testDatabase.getJdbcCredentials();
            return DSL.using(credentials.jdbcUrl(), credentials.jdbcUser(), credentials.jdbcPassword());
        });
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        testDatabase.afterAll(extensionContext);
    }

    @Override
    public boolean supportsParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        val clazz = parameterContext.getParameter().getType();

        return instances.has(clazz);
    }

    @Override
    public Object resolveParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        val clazz = parameterContext.getParameter().getType();

        if (instances.has(clazz)) {
            return instances.get(clazz);
        }

        throw new ParameterResolutionException("No instance of " + clazz);
    }

}
