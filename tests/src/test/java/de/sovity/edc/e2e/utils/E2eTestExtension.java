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
import de.sovity.edc.extension.e2e.connector.config.ConnectorConfig;
import de.sovity.edc.extension.e2e.db.EdcRuntimeExtensionWithTestDatabase;
import lombok.val;
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

    private final String consumerParticipantId = "consumer";
    private ConnectorConfig consumerConfig;

    private final EdcRuntimeExtensionWithTestDatabase consumerExtension = new EdcRuntimeExtensionWithTestDatabase(
        ":launchers:connectors:sovity-dev",
        "consumer",
        testDatabase -> {
            consumerConfig = forTestDatabase(consumerParticipantId, testDatabase);
            return consumerConfig.getProperties();
        }
    );


    private final String providerParticipantId = "provider";
    private ConnectorConfig providerConfig;

    private final EdcRuntimeExtensionWithTestDatabase providerExtension = new EdcRuntimeExtensionWithTestDatabase(
        ":launchers:connectors:sovity-dev",
        "provider",
        testDatabase -> {
            providerConfig = forTestDatabase(providerParticipantId, testDatabase);
            return providerConfig.getProperties();
        }
    );

    private final List<Class<?>> partySupportedTypes = List.of(ConnectorConfig.class, EdcClient.class);
    private final List<Class<?>> supportedTypes = Stream.concat(partySupportedTypes.stream(), Stream.of(E2eScenario.class)).toList();


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
            if (type.equals(EdcClient.class)) {
                return newEdcClient(consumerConfig);
            } else if (type.equals(ConnectorConfig.class)) {
                return consumerConfig;
            } else {
                return consumerExtension.supportsParameter(parameterContext, extensionContext);
            }
        }

        if (isProvider) {
            if (type.equals(EdcClient.class)) {
                return newEdcClient(providerConfig);
            } else if (type.equals(ConnectorConfig.class)) {
                return providerConfig;
            } else {
                return providerExtension.supportsParameter(parameterContext, extensionContext);
            }
        }

        if (type.equals(E2eScenario.class)) {
            return new E2eScenario(newEdcClient(consumerConfig), consumerConfig, newEdcClient(providerConfig), providerConfig);
        }

        throw new IllegalArgumentException("The parameters must be annotated by the EDC side: @Provider or @Consumer.");
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
