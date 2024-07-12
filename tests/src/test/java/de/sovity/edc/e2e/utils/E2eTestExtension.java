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
import java.util.Map;

import static de.sovity.edc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;

public class E2eTestExtension
    implements BeforeAllCallback, AfterAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {

    private final String consumerParticipantId = "consumer";
    private ConnectorConfig consumerConfig;
    private EdcClient consumerClient;

    private final EdcRuntimeExtensionWithTestDatabase consumerExtension = new EdcRuntimeExtensionWithTestDatabase(
        ":launchers:connectors:sovity-dev",
        "consumer",
        testDatabase -> {
            consumerConfig = forTestDatabase(consumerParticipantId, testDatabase);
            consumerClient = EdcClient.builder()
                .managementApiUrl(consumerConfig.getManagementEndpoint().getUri().toString())
                .managementApiKey(consumerConfig.getProperties().get("edc.api.auth.key"))
                .build();
            return consumerConfig.getProperties();
        }
    );


    private final String providerParticipantId = "provider";
    private ConnectorConfig providerConfig;
    private EdcClient providerClient;

    private final EdcRuntimeExtensionWithTestDatabase providerExtension = new EdcRuntimeExtensionWithTestDatabase(
        ":launchers:connectors:sovity-dev",
        "provider",
        testDatabase -> {
            providerConfig = forTestDatabase(providerParticipantId, testDatabase);
            providerClient = EdcClient.builder()
                .managementApiUrl(providerConfig.getManagementEndpoint().getUri().toString())
                .managementApiKey(providerConfig.getProperties().get("edc.api.auth.key"))
                .build();
            return providerConfig.getProperties();
        }
    );

    private final List<Class<?>> supportedTypes = List.of(ConnectorConfig.class, EdcClient.class, E2eScenario.class);

    Map<Class<?>, Map<Class<?>, Object>> getRegistry() {
        return Map.of(
            Provider.class, Map.of(
                ConnectorConfig.class, providerConfig,
                EdcClient.class, providerClient
            ),
            Consumer.class, Map.of(
                ConnectorConfig.class, consumerConfig,
                EdcClient.class, consumerClient
            )
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

        val isProvider = parameterContext.getParameter().getDeclaredAnnotation(Provider.class) != null;
        val isConsumer = parameterContext.getParameter().getDeclaredAnnotation(Consumer.class) != null;

        if (isProvider && isConsumer) {
            return false;
        }

        val type = parameterContext.getParameter().getType();

        if (isProvider) {
            return getRegistry().getOrDefault(Provider.class, Map.of()).getOrDefault(type, null) != null;
        }
        if (isConsumer) {
            return getRegistry().getOrDefault(Consumer.class, Map.of()).getOrDefault(type, null) != null;
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

        val isConsumer = parameterContext.getParameter().getDeclaredAnnotation(Consumer.class) != null;
        val isProvider = parameterContext.getParameter().getDeclaredAnnotation(Provider.class) != null;

        val type = parameterContext.getParameter().getType();

        if (isConsumer) {
            val maybe = getRegistry().getOrDefault(Consumer.class, Map.of()).getOrDefault(type, null);

            if (maybe != null) {
                return maybe;
            } else {
                return consumerExtension.supportsParameter(parameterContext, extensionContext);
            }
        }
        if (isProvider) {
            val maybe = getRegistry().getOrDefault(Provider.class, Map.of()).getOrDefault(type, null);

            if (maybe != null) {
                return maybe;
            } else {
                return providerExtension.supportsParameter(parameterContext, extensionContext);
            }
        }

        if (type.equals(E2eScenario.class)) {
            return new E2eScenario(consumerClient, consumerConfig, providerClient, providerConfig);
        }

        throw new IllegalArgumentException("The parameters must be annotated by the EDC side: @Provider or @Consumer.");
    }
}
