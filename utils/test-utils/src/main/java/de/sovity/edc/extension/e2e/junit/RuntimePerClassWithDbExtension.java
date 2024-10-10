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

import de.sovity.edc.extension.e2e.connector.config.ConnectorBootConfig;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.db.TestDatabaseViaTestcontainers;
import de.sovity.edc.extension.e2e.junit.multi.InstancesForJunitTest;
import de.sovity.edc.utils.config.model.ConfigProp;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.val;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;

/**
 * Starts one DB and one EDC
 */
@Builder
@RequiredArgsConstructor
public final class RuntimePerClassWithDbExtension
    implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    @Builder.Default
    private final String runtimeName = "runtime";

    @Builder.Default
    private final Function<TestDatabase, ConnectorBootConfig> configFactory = db -> ConnectorBootConfig.builder().build();

    @Builder.Default
    private final List<ConfigProp> allConfigProps = emptyList();

    @Singular("additionalModule")
    private final List<String> additionalModules;

    private final InstancesForJunitTest instances = new InstancesForJunitTest();
    private final TestDatabase testDatabase = new TestDatabaseViaTestcontainers();

    @Getter
    private RuntimePerClassExtensionFixed runtimePerClassExtensionFixed = null;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        testDatabase.beforeAll(extensionContext);
        instances.put(testDatabase);
        instances.putLazy(DSLContext.class, () -> {
            val credentials = testDatabase.getJdbcCredentials();
            return DSL.using(credentials.jdbcUrl(), credentials.jdbcUser(), credentials.jdbcPassword());
        });

        var connectorConfig = configFactory.apply(testDatabase);
        instances.put(connectorConfig);

        runtimePerClassExtensionFixed = new RuntimePerClassExtensionFixed(new EmbeddedRuntimeFixed(
            runtimeName,
            connectorConfig,
            allConfigProps,
            additionalModules.toArray(String[]::new)
        ));
        runtimePerClassExtensionFixed.beforeAll(extensionContext);
        instances.put(runtimePerClassExtensionFixed);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        try {
            runtimePerClassExtensionFixed.afterAll(extensionContext);
        } finally {
            testDatabase.afterAll(extensionContext);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        val clazz = parameterContext.getParameter().getType();

        if (instances.has(clazz)) {
            return true;
        }

        return runtimePerClassExtensionFixed.supportsParameter(parameterContext, extensionContext);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        val clazz = parameterContext.getParameter().getType();

        if (instances.has(clazz)) {
            return instances.get(clazz);
        }

        return runtimePerClassExtensionFixed.resolveParameter(parameterContext, extensionContext);
    }

}
