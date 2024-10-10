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
import de.sovity.edc.extension.e2e.connector.config.ConnectorBootConfig;
import de.sovity.edc.extension.e2e.connector.config.ConnectorBootConfig.ConnectorBootConfigBuilder;
import de.sovity.edc.extension.e2e.connector.remotes.management_api.ManagementApiConnectorRemote;
import de.sovity.edc.extension.e2e.junit.multi.InstancesForJunitTest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.val;
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
public final class CeIntegrationTestExtension
    implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

    @Singular("additionalModule")
    private final List<String> additionalModules;

    @Nullable
    private final Consumer<ConnectorBootConfigBuilder> configOverrides;

    private final InstancesForJunitTest instances = new InstancesForJunitTest();

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        var extension = CeIntegrationTestUtils.defaultRuntimeWithCeConfig(additionalModules, configOverrides);
        instances.put(extension);
        extension.beforeAll(extensionContext);

        var config = extension.getRuntimePerClassExtensionFixed().getService(Config.class);

        instances.putLazy(EdcClient.class, () -> CeIntegrationTestUtils.getEdcClient(config));
        instances.putLazy(ManagementApiConnectorRemote.class, () -> CeIntegrationTestUtils.getManagementApiConnectorRemote(config));
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        instances.get(RuntimePerClassWithDbExtension.class).afterAll(extensionContext);
    }

    @Override
    public boolean supportsParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        val clazz = parameterContext.getParameter().getType();

        if (instances.has(clazz)) {
            return true;
        }

        return instances.get(RuntimePerClassWithDbExtension.class).supportsParameter(parameterContext, extensionContext);
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

        return instances.get(RuntimePerClassWithDbExtension.class).resolveParameter(parameterContext, extensionContext);
    }

}
