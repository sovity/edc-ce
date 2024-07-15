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

package de.sovity.edc.e2e.utils;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.extension.e2e.connector.ConnectorRemote;
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.connector.config.ConnectorRemoteConfig;
import de.sovity.edc.extension.e2e.db.EdcRuntimeExtensionWithTestDatabase;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.List;
import java.util.stream.Stream;

import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;

public class E2eTestExtension
    implements BeforeAllCallback, AfterAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {

    private final String consumerParticipantId;
    private ConnectorConfig consumerConfig;
    private final EdcRuntimeExtensionWithTestDatabase consumerExtension;

    private final String providerParticipantId;
    private ConnectorConfig providerConfig;
    private final EdcRuntimeExtensionWithTestDatabase providerExtension;

    private final List<Class<?>> partySupportedTypes = List.of(ConnectorConfig.class, EdcClient.class, ConnectorRemote.class);
    private final List<Class<?>> supportedTypes = Stream.concat(partySupportedTypes.stream(), Stream.of(E2eScenario.class)).toList();

    public E2eTestExtension() {
        this("consumer", "provider");
    }

    public E2eTestExtension(String consumerParticipantId, String providerParticipantId) {
        this.consumerParticipantId = consumerParticipantId;
        this.providerParticipantId = providerParticipantId;

        consumerExtension = new EdcRuntimeExtensionWithTestDatabase(
            ":launchers:connectors:sovity-dev",
            "consumer",
            testDatabase -> {
                consumerConfig = forTestDatabase(this.consumerParticipantId, testDatabase);
                return consumerConfig.getProperties();
            }
        );
        providerExtension = new EdcRuntimeExtensionWithTestDatabase(
            ":launchers:connectors:sovity-dev",
            "provider",
            testDatabase -> {
                providerConfig = forTestDatabase(this.providerParticipantId, testDatabase);
                return providerConfig.getProperties();
            }
        );
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        consumerExtension.afterAll(context);
        providerExtension.afterAll(context);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        consumerExtension.afterTestExecution(context);
        providerExtension.afterTestExecution(context);
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        consumerExtension.beforeAll(context);
        providerExtension.beforeAll(context);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        consumerExtension.beforeTestExecution(context);
        providerExtension.beforeTestExecution(context);
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

        if (isProvider || isConsumer) {
            return partySupportedTypes.contains(type);
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
                return newConnectorRemote(consumerParticipantId, consumerConfig);
            } else {
                return consumerExtension.supportsParameter(parameterContext, extensionContext);
            }
        }

        if (isProvider) {
            if (EdcClient.class.equals(type)) {
                return newEdcClient(providerConfig);
            } else if (ConnectorConfig.class.equals(type)) {
                return providerConfig;
            } else if (ConnectorRemote.class.equals(type)) {
                return newConnectorRemote(providerParticipantId, providerConfig);
            } else {
                return providerExtension.supportsParameter(parameterContext, extensionContext);
            }
        }

        if (E2eScenario.class.equals(type)) {
            return new E2eScenario(newEdcClient(consumerConfig), consumerConfig, newEdcClient(providerConfig), providerConfig);
        }

        throw new IllegalArgumentException("The parameters must be annotated by the EDC side: @Provider or @Consumer.");
    }

    private @NotNull ConnectorRemote newConnectorRemote(String participantId, ConnectorConfig config) {
        return new ConnectorRemote(
            new ConnectorRemoteConfig(
                participantId,
                config.getDefaultEndpoint(),
                config.getManagementEndpoint(),
                config.getProtocolEndpoint()));
    }

    private static boolean isProvider(ParameterContext parameterContext) {
        return parameterContext.getParameter().getDeclaredAnnotation(Provider.class) != null;
    }

    private static boolean isConsumer(ParameterContext parameterContext) {
        return parameterContext.getParameter().getDeclaredAnnotation(Consumer.class) != null;
    }

    private EdcClient newEdcClient(ConnectorConfig consumerConfig) {
        return EdcClient.builder()
            .managementApiUrl(consumerConfig.getManagementEndpoint().getUri().toString())
            .managementApiKey(consumerConfig.getProperties().get("edc.api.auth.key"))
            .build();
    }
}
