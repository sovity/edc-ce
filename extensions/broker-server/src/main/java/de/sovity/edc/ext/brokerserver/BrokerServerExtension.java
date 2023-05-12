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
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;

public class BrokerServerExtension implements ServiceExtension {

    public static final String EXTENSION_NAME = "BrokerServerExtension";

    @Setting
    public static final String KNOWN_CONNECTORS = "edc.brokerserver.known.connectors";

    @Inject
    private ManagementApiConfiguration managementApiConfiguration;

    @Inject
    private WebService webService;

    @Override
    public String name() {
        return EXTENSION_NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var services = BrokerServerExtensionContextBuilder.buildContext(context.getConfig());
        services.brokerServerInitializer().initializeConnectorList();

        String managementApiGroup = managementApiConfiguration.getContextAlias();
        webService.registerResource(managementApiGroup, services.brokerServerResource());
    }
}
