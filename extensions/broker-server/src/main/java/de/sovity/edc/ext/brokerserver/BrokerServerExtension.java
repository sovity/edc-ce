/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.ext.brokerserver;

import org.eclipse.edc.connector.api.management.configuration.ManagementApiConfiguration;
import org.eclipse.edc.protocol.ids.spi.service.DynamicAttributeTokenService;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.http.EdcHttpClient;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.web.spi.WebService;

public class BrokerServerExtension implements ServiceExtension {

    public static final String EXTENSION_NAME = "BrokerServerExtension";

    @Setting
    public static final String KNOWN_CONNECTORS = "edc.brokerserver.known.connectors";

    @Inject
    private ManagementApiConfiguration managementApiConfiguration;

    @Inject
    private WebService webService;

    @Inject
    private EdcHttpClient httpClient;

    @Inject
    private DynamicAttributeTokenService dynamicAttributeTokenService;

    @Inject
    private TypeManager typeManager;

    @Inject
    private RemoteMessageDispatcherRegistry dispatcherRegistry;

    /**
     * Manual Dependency Injection Result
     */
    private BrokerServerExtensionContext services;

    @Override
    public String name() {
        return EXTENSION_NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        services = BrokerServerExtensionContextBuilder.buildContext(
                context.getConfig(),
                context.getMonitor(),
                httpClient,
                dynamicAttributeTokenService,
                typeManager,
                dispatcherRegistry
        );

        var managementApiGroup = managementApiConfiguration.getContextAlias();
        webService.registerResource(managementApiGroup, services.brokerServerResource());

        dispatcherRegistry.register(services.remoteMessageDispatcher());
    }

    @Override
    public void start() {
        services.brokerServerInitializer().onStartup();
    }
}
