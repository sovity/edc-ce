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
import de.sovity.edc.extension.e2e.connector.config.ConnectorBootConfig;
import de.sovity.edc.extension.e2e.connector.remotes.api_wrapper.E2eTestScenario;
import de.sovity.edc.extension.e2e.connector.remotes.api_wrapper.E2eTestScenarioConfig;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.extension.e2e.junit.CeIntegrationTestUtils;
import de.sovity.edc.extension.e2e.junit.RuntimePerClassWithDbExtension;
import de.sovity.edc.utils.config.ConfigProps;
import lombok.val;
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

import static org.eclipse.edc.util.io.Ports.getFreePort;
import static org.mockserver.stop.Stop.stopQuietly;

public class CeE2eTestExtension
    implements BeforeAllCallback, AfterAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {

    private final InstancesForEachConnector<Side> connectorInstances = new InstancesForEachConnector<>(Arrays.asList(Side.values()));
    private final InstancesForJunitTest globalInstances = new InstancesForJunitTest();

    public CeE2eTestExtension() {
        this(CeE2eTestConfig.builder().build());
    }

    public CeE2eTestExtension(String moduleName) {
        this(CeE2eTestConfig.builder().moduleName(moduleName).build());
    }

    public CeE2eTestExtension(CeE2eTestConfig e2eConfig) {
        // Register E2eTestExtensionConfig
        globalInstances.put(e2eConfig);

        for (Side side : Side.values()) {
            var participantId = side.getParticipantId();

            var dbRuntimePerClassExtension = RuntimePerClassWithDbExtension.builder()
                .allConfigProps(ConfigProps.ALL_CE_PROPS)
                .configFactory(testDatabase -> {
                    // Register ConnectorConfig
                    var connectorConfig = buildConnectorConfig(e2eConfig, side, testDatabase, participantId);
                    connectorInstances.put(side, connectorConfig);
                    return connectorConfig;
                })
                .additionalModule(e2eConfig.getModuleName())
                .build();

            // Register DbRuntimePerClassExtension
            connectorInstances.put(side, dbRuntimePerClassExtension);
        }
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        // Start EDCs
        // for loop explicitly used because of checked exceptions
        for (var extension : connectorInstances.all(RuntimePerClassWithDbExtension.class)) {
            extension.beforeAll(context);
        }

        for (var side : Side.values()) {
            var config = getConfig(side);

            // Register EdcClient
            connectorInstances.put(side, CeIntegrationTestUtils.getEdcClient(config));

            // Register ManagementApiConnectorRemote
            connectorInstances.put(side, CeIntegrationTestUtils.getManagementApiConnectorRemote(config));
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
        globalInstances.putLazy(
            E2eTestScenario.class,
            () -> E2eTestScenario
                .builder()
                .consumerClient(connectorInstances.get(Side.CONSUMER, EdcClient.class))
                .providerClient(connectorInstances.get(Side.PROVIDER, EdcClient.class))
                .mockServer(globalInstances.get(ClientAndServer.class))
                .config(E2eTestScenarioConfig.forProviderConfig(getConfig(Side.PROVIDER)))
                .build()
        );
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
        for (var extension : connectorInstances.all(RuntimePerClassWithDbExtension.class)) {
            extension.afterAll(context);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        val sideOrNull = Side.fromParameterContextOrNull(parameterContext);
        val clazz = parameterContext.getParameter().getType();

        if (sideOrNull != null) {
            if (connectorInstances.has(sideOrNull, clazz)) {
                return connectorInstances.has(sideOrNull, clazz);
            }

            return connectorInstances.get(sideOrNull, RuntimePerClassWithDbExtension.class)
                .supportsParameter(parameterContext, extensionContext);
        }

        return globalInstances.has(clazz);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        val sideOrNull = Side.fromParameterContextOrNull(parameterContext);
        val clazz = parameterContext.getParameter().getType();

        if (sideOrNull != null) {
            if (connectorInstances.has(sideOrNull, clazz)) {
                return connectorInstances.get(sideOrNull, clazz);
            }

            return connectorInstances.get(sideOrNull, RuntimePerClassWithDbExtension.class)
                .resolveParameter(parameterContext, extensionContext);
        }

        if (globalInstances.has(clazz)) {
            return globalInstances.get(clazz);
        }

        throw new IllegalArgumentException(
            "The parameters must be annotated by the EDC side: @Provider or @Consumer or be one of the supported classes."
        );
    }

    private Config getConfig(Side side) {
        var extension = connectorInstances.get(side, RuntimePerClassWithDbExtension.class);
        return extension.getRuntimePerClassExtensionFixed().getService(Config.class);
    }

    private ConnectorBootConfig buildConnectorConfig(
        CeE2eTestConfig config,
        Side side,
        TestDatabase testDatabase,
        String participantId
    ) {
        return CeIntegrationTestUtils.defaultConfig(
            participantId,
            testDatabase,
            builder -> {
                config.getConfigCustomizer().accept(builder);
                if (side == Side.CONSUMER) {
                    config.getConsumerConfigCustomizer().accept(builder);
                } else {
                    config.getProviderConfigCustomizer().accept(builder);
                }
            }
        );
    }
}
