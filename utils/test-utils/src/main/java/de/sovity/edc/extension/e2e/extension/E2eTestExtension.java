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

package de.sovity.edc.extension.e2e.extension;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.db.EdcRuntimeExtensionWithTestDatabase;
import de.sovity.edc.extension.utils.Lazy;
import lombok.val;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockserver.integration.ClientAndServer;

import java.util.List;
import java.util.stream.Stream;

import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfig.fromConnectorConfig;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;
import static org.mockserver.stop.Stop.stopQuietly;

public class E2eTestExtension
    implements BeforeAllCallback, AfterAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {

    private E2eTestExtensionConfig config;

    private ConnectorConfig consumerConfig;
    private final EdcRuntimeExtensionWithTestDatabase consumerExtension;

    private ConnectorConfig providerConfig;
    private final EdcRuntimeExtensionWithTestDatabase providerExtension;

    private final List<Class<?>> partySupportedTypes =
        List.of(ConnectorConfig.class, EdcClient.class, ConnectorRemote.class, ClientAndServer.class);
    private final List<Class<?>> supportedTypes = Stream.concat(partySupportedTypes.stream(), Stream.of(E2eScenario.class)).toList();

    private Lazy<ClientAndServer> clientAndServer;

    public E2eTestExtension(String moduleName) {
        this(E2eTestExtensionConfig.builder().moduleName(moduleName).build());
    }

    public E2eTestExtension(
        E2eTestExtensionConfig config
    ) {
        this.config = config;

        consumerExtension = new EdcRuntimeExtensionWithTestDatabase(
            config.getModuleName(),
            config.getConsumerParticipantId(),
            testDatabase -> {
                consumerConfig = forTestDatabase(config.getConsumerParticipantId(), testDatabase);

                config.getConfigCustomizer().accept(consumerConfig);
                config.getConsumerConfigCustomizer().accept(consumerConfig);
                return consumerConfig.getProperties();
            }
        );

        providerExtension = new EdcRuntimeExtensionWithTestDatabase(
            config.getModuleName(),
            config.getProviderParticipantId(),
            testDatabase -> {
                providerConfig = forTestDatabase(config.getProviderParticipantId(), testDatabase);

                config.getConfigCustomizer().accept(providerConfig);
                config.getProviderConfigCustomizer().accept(providerConfig);
                return providerConfig.getProperties();
            }
        );
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        consumerExtension.beforeAll(context);
        providerExtension.beforeAll(context);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        clientAndServer = new Lazy<>(() -> ClientAndServer.startClientAndServer(getFreePort()));
        consumerExtension.beforeTestExecution(context);
        providerExtension.beforeTestExecution(context);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        if (clientAndServer.isInitialized()) {
            stopQuietly(clientAndServer.get());
        }
        consumerExtension.afterTestExecution(context);
        providerExtension.afterTestExecution(context);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        consumerExtension.afterAll(context);
        providerExtension.afterAll(context);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {

        val isProvider = isProvider(parameterContext);
        val isConsumer = isConsumer(parameterContext);

        if (isProvider && isConsumer) {
            throw new ParameterResolutionException("Either @Provider or @Consumer may be used.");
        }

        val type = parameterContext.getParameter().getType();

        if (isConsumer) {
            return partySupportedTypes.contains(type) || consumerExtension.supportsParameter(parameterContext, extensionContext);
        }

        if (isProvider) {
            return partySupportedTypes.contains(type) || providerExtension.supportsParameter(parameterContext, extensionContext);
        }

        if (supportedTypes.contains(type)) {
            return true;
        }

        return consumerExtension.supportsParameter(parameterContext, extensionContext) ||
            providerExtension.supportsParameter(parameterContext, extensionContext);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {

        val isConsumer = isConsumer(parameterContext);
        val isProvider = isProvider(parameterContext);

        val type = parameterContext.getParameter().getType();

        if (isConsumer) {
            if (EdcClient.class.equals(type)) {
                return newEdcClient(consumerConfig);
            } else if (ConnectorConfig.class.equals(type)) {
                return consumerConfig;
            } else if (ConnectorRemote.class.equals(type)) {
                return new ConnectorRemote(fromConnectorConfig(consumerConfig));
            } else {
                return consumerExtension.resolveParameter(parameterContext, extensionContext);
            }
        }

        if (isProvider) {
            if (EdcClient.class.equals(type)) {
                return newEdcClient(providerConfig);
            } else if (ConnectorConfig.class.equals(type)) {
                return providerConfig;
            } else if (ConnectorRemote.class.equals(type)) {
                return new ConnectorRemote(fromConnectorConfig(providerConfig));
            } else {
                return providerExtension.resolveParameter(parameterContext, extensionContext);
            }
        }

        if (E2eScenario.class.equals(type)) {
            return new E2eScenario(consumerConfig, providerConfig, clientAndServer.get());
        } else if (ClientAndServer.class.equals(type)) {
            return clientAndServer.get();
        }

        throw new IllegalArgumentException(
            "The parameters must be annotated by the EDC side: @Provider or @Consumer or be one of the supported classes.");
    }

    private static boolean isProvider(ParameterContext parameterContext) {
        return parameterContext.getParameter().getDeclaredAnnotation(Provider.class) != null;
    }

    private static boolean isConsumer(ParameterContext parameterContext) {
        return parameterContext.getParameter().getDeclaredAnnotation(Consumer.class) != null;
    }

    private EdcClient newEdcClient(ConnectorConfig consumerConfig) {
        return EdcClient.builder()
            .managementApiUrl(consumerConfig.getManagementApiUrl())
            .managementApiKey(consumerConfig.getManagementApiKey())
            .build();
    }
}
