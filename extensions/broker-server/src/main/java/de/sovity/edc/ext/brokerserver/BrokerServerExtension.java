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
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;

public class BrokerServerExtension implements ServiceExtension {

    public static final String EXTENSION_NAME = "BrokerServerExtension";

    @Setting
    public static final String KNOWN_CONNECTORS = "edc.brokerserver.known.connectors";

    @Inject
    private AssetIndex assetIndex; // ensures db is initialized before

    @Inject
    private ManagementApiConfiguration managementApiConfiguration;

    @Inject
    private WebService webService;

    /**
     * Manual Dependency Injection
     */
    private BrokerServerExtensionContext services;

    @Override
    public String name() {
        return EXTENSION_NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        services = BrokerServerExtensionContextBuilder.buildContext(context.getConfig());
        String managementApiGroup = managementApiConfiguration.getContextAlias();
        webService.registerResource(managementApiGroup, services.brokerServerResource());
    }

    @Override
    public void start() {
        services.brokerServerInitializer().onStartup();
    }
}
