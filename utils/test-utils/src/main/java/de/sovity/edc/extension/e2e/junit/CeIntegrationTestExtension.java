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

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.extension.e2e.connector.config.ConnectorBootConfig.ConnectorBootConfigBuilder;
import de.sovity.edc.extension.e2e.connector.remotes.management_api.ManagementApiConnectorRemote;
import de.sovity.edc.extension.e2e.db.JdbcCredentials;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.junit.edc.EmbeddedRuntimeFixed;
import de.sovity.edc.extension.e2e.junit.edc.RuntimePerClassExtensionFixed;
import de.sovity.edc.extension.e2e.junit.utils.InstancesForJunitTest;
import de.sovity.edc.extension.e2e.junit.utils.ParameterResolverList;
import de.sovity.edc.utils.config.ConfigProps;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.experimental.Delegate;
import lombok.val;
import org.eclipse.edc.junit.extensions.RuntimePerClassExtension;
import org.eclipse.edc.spi.system.configuration.Config;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.List;
import java.util.function.Consumer;

/**
 * Starts one DB and one EDC
 */
@Builder
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CeIntegrationTestExtension
    implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    @Getter
    @Builder.Default
    private final String participantId = "connector";

    @Singular("additionalModule")
    private final List<String> additionalModules;

    @Builder.Default
    private final boolean skipDb = false;

    @Nullable
    private final Consumer<ConnectorBootConfigBuilder> configOverrides;

    private final InstancesForJunitTest instances = new InstancesForJunitTest();

    @Delegate(types = ParameterResolver.class)
    private final ParameterResolverList parameterResolverList = new ParameterResolverList();

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        parameterResolverList.add(instances);

        // Start DB
        if (!skipDb) {
            var dbExtension = TestDatabaseExtension.builder().build();
            instances.put(dbExtension);
            parameterResolverList.add(dbExtension);
            dbExtension.beforeAll(extensionContext);
        }

        // Start Connector
        var bootConfig = CeIntegrationTestUtils.defaultConfig(participantId, getTestDatabaseOrMock(), configOverrides);
        var connectorExtension = new RuntimePerClassExtensionFixed(new EmbeddedRuntimeFixed(
            participantId,
            bootConfig,
            ConfigProps.ALL_CE_PROPS,
            additionalModules.toArray(String[]::new)
        ));
        instances.put(connectorExtension);
        parameterResolverList.add(connectorExtension);
        connectorExtension.beforeAll(extensionContext);

        // Configure Clients and Utilities
        var config = connectorExtension.getService(Config.class);
        instances.putLazy(EdcClient.class, () -> CeIntegrationTestUtils.getEdcClient(config));
        instances.putLazy(ManagementApiConnectorRemote.class, () -> CeIntegrationTestUtils.getManagementApiConnectorRemote(config));
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        try {
            instances.get(RuntimePerClassExtensionFixed.class).afterAll(extensionContext);
        } finally {
            if (instances.has(TestDatabaseExtension.class)) {
                instances.get(TestDatabaseExtension.class).afterAll(extensionContext);
            }
        }
    }

    private TestDatabase getTestDatabaseOrMock() {
        if (skipDb) {
            return () -> new JdbcCredentials("no-test-db", "no-test-db", "no-test-db");
        }
        return instances.get(TestDatabaseExtension.class).getTestDatabase();
    }
}
