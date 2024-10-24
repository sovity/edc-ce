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
import de.sovity.edc.extension.e2e.connector.remotes.api_wrapper.E2eTestScenario;
import de.sovity.edc.extension.e2e.connector.remotes.api_wrapper.E2eTestScenarioConfig;
import de.sovity.edc.extension.e2e.connector.remotes.test_backend_controller.TestBackendRemote;
import de.sovity.edc.extension.e2e.junit.utils.InstancesForEachConnector;
import de.sovity.edc.extension.e2e.junit.utils.InstancesForJunitTest;
import de.sovity.edc.utils.config.ConfigUtils;
import lombok.Builder;
import lombok.Singular;
import lombok.experimental.Delegate;
import org.eclipse.edc.spi.system.configuration.Config;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockserver.integration.ClientAndServer;

import java.util.ArrayList;
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

    @Builder.Default
    private List<Runnable> cleanupHooks = new ArrayList<>();

    private final InstancesForEachConnector<CeE2eTestSide> instancesForEachConnector = new InstancesForEachConnector<>(
        Arrays.asList(CeE2eTestSide.values()),
        (parameterContext, extensionContext) -> CeE2eTestSide.fromParameterContextOrNull(parameterContext)
    );

    @Delegate(types = ParameterResolver.class)
    private final InstancesForJunitTest instances = new InstancesForJunitTest();

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        instances.put(instancesForEachConnector);
        instances.addParameterResolver(instancesForEachConnector);

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
            instancesForEachConnector.forSide(side).put(extension);
            instancesForEachConnector.forSide(side).addParameterResolver(extension);

            // Start EDC
            extension.beforeAll(context);
        }

        // Register TestBackendRemote
        instances.putLazy(TestBackendRemote.class, this::buildTestBackendRemote);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        // Register ClientAndServer
        instances.putLazy(
            ClientAndServer.class,
            () -> ClientAndServer.startClientAndServer(getFreePort())
        );

        // Register ConnectorRemoteClient
        instances.putLazy(E2eTestScenario.class, this::buildE2eTestScenario);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (instances.isLazyInitialized(ClientAndServer.class)) {
            stopQuietly(instances.get(ClientAndServer.class));
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        // for loop explicitly used because of checked exceptions
        for (var extension : instancesForEachConnector.all(CeIntegrationTestExtension.class)) {
            extension.afterAll(context);
        }
    }

    private E2eTestScenario buildE2eTestScenario() {
        return E2eTestScenario.builder()
            .consumerClient(instancesForEachConnector.forSide(CeE2eTestSide.CONSUMER).get(EdcClient.class))
            .providerClient(instancesForEachConnector.forSide(CeE2eTestSide.PROVIDER).get(EdcClient.class))
            .mockServer(instances.get(ClientAndServer.class))
            .config(E2eTestScenarioConfig.forProviderConfig(getConfig(CeE2eTestSide.PROVIDER)))
            .build();
    }

    private TestBackendRemote buildTestBackendRemote() {
        var defaultApiUrl = ConfigUtils.getDefaultApiUrl(getConfig(CeE2eTestSide.PROVIDER));
        return new TestBackendRemote(defaultApiUrl);
    }

    private Config getConfig(CeE2eTestSide ceE2eTestSide) {
        return instancesForEachConnector.forSide(ceE2eTestSide).get(Config.class);
    }
}
