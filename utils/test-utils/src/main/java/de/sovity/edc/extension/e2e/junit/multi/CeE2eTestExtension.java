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

package de.sovity.edc.extension.e2e.junit.multi;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.extension.e2e.connector.config.ConnectorBootConfig.ConnectorBootConfigBuilder;
import de.sovity.edc.extension.e2e.connector.remotes.api_wrapper.E2eTestScenario;
import de.sovity.edc.extension.e2e.connector.remotes.api_wrapper.E2eTestScenarioConfig;
import de.sovity.edc.extension.e2e.junit.CeIntegrationTestExtension;
import lombok.Builder;
import lombok.Singular;
import lombok.val;
import org.eclipse.edc.junit.extensions.RuntimePerClassExtension;
import org.eclipse.edc.spi.system.configuration.Config;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockserver.integration.ClientAndServer;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.eclipse.edc.util.io.Ports.getFreePort;
import static org.mockserver.stop.Stop.stopQuietly;

@Builder
public class CeE2eTestExtension
    implements BeforeAllCallback, AfterAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {

    @Singular("additionalModule")
    private List<String> additionalModules;

    @Builder.Default
    private boolean skipDb = false;

    @Builder.Default
    private Consumer<ConnectorBootConfigBuilder> configCustomizer = it -> {
    };

    @Builder.Default
    private Consumer<ConnectorBootConfigBuilder> consumerConfigCustomizer = it -> {
    };

    @Builder.Default
    private Consumer<ConnectorBootConfigBuilder> providerConfigCustomizer = it -> {
    };

    private final InstancesForEachConnector<CeE2eTestSide> connectorInstances =
        new InstancesForEachConnector<>(Arrays.asList(CeE2eTestSide.values()));
    private final InstancesForJunitTest globalInstances = new InstancesForJunitTest();

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        for (CeE2eTestSide side : CeE2eTestSide.values()) {
            var extension = CeIntegrationTestExtension.builder()
                .participantId(side.getParticipantId())
                .additionalModules(additionalModules)
                .configOverrides(config -> {
                    configCustomizer.accept(config);
                    if (side == CeE2eTestSide.CONSUMER) {
                        consumerConfigCustomizer.accept(config);
                    } else {
                        providerConfigCustomizer.accept(config);
                    }
                })
                .skipDb(skipDb)
                .build();

            // Register DbRuntimePerClassExtension
            connectorInstances.put(side, extension);

            // Start EDC
            extension.beforeAll(context);
        }
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        // Register ClientAndServer
        globalInstances.putLazy(
            ClientAndServer.class,
            () -> ClientAndServer.startClientAndServer(getFreePort())
        );

        // Register ConnectorRemoteClient
        globalInstances.putLazy(E2eTestScenario.class, this::buildE2eTestScenario);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        if (globalInstances.isLazyInitialized(ClientAndServer.class)) {
            stopQuietly(globalInstances.get(ClientAndServer.class));
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        // for loop explicitly used because of checked exceptions
        for (var extension : connectorInstances.all(RuntimePerClassExtension.class)) {
            extension.afterAll(context);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        val sideOrNull = CeE2eTestSide.fromParameterContextOrNull(parameterContext);
        val clazz = parameterContext.getParameter().getType();

        if (sideOrNull != null) {
            if (connectorInstances.has(sideOrNull, clazz)) {
                return connectorInstances.has(sideOrNull, clazz);
            }

            return connectorInstances.get(sideOrNull, RuntimePerClassExtension.class)
                .supportsParameter(parameterContext, extensionContext);
        }

        return globalInstances.has(clazz);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        val sideOrNull = CeE2eTestSide.fromParameterContextOrNull(parameterContext);
        val clazz = parameterContext.getParameter().getType();

        if (sideOrNull != null) {
            if (connectorInstances.has(sideOrNull, clazz)) {
                return connectorInstances.get(sideOrNull, clazz);
            }

            return connectorInstances.get(sideOrNull, RuntimePerClassExtension.class)
                .resolveParameter(parameterContext, extensionContext);
        }

        if (globalInstances.has(clazz)) {
            return globalInstances.get(clazz);
        }

        throw new IllegalArgumentException(
            "The parameters must be annotated by the EDC side: @Provider or @Consumer or be one of the supported classes."
        );
    }

    private E2eTestScenario buildE2eTestScenario() {
        return E2eTestScenario.builder()
            .consumerClient(connectorInstances.get(CeE2eTestSide.CONSUMER, EdcClient.class))
            .providerClient(connectorInstances.get(CeE2eTestSide.PROVIDER, EdcClient.class))
            .mockServer(globalInstances.get(ClientAndServer.class))
            .config(E2eTestScenarioConfig.forProviderConfig(
                connectorInstances.get(CeE2eTestSide.PROVIDER, RuntimePerClassExtension.class).getService(Config.class)))
            .build();
    }
}
