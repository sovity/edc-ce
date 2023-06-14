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
import org.eclipse.edc.connector.spi.catalog.CatalogService;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.web.spi.WebService;

public class BrokerServerExtension implements ServiceExtension {

    public static final String EXTENSION_NAME = "BrokerServerExtension";

    @Setting
    public static final String KNOWN_CONNECTORS = "edc.broker.server.known.connectors";

    @Setting
    public static final String CRON_CONNECTOR_REFRESH = "edc.broker.server.cron.connector.refresh";

    @Setting
    public static final String NUM_THREADS = "edc.broker.server.num.threads";

    @Setting
    public static final String HIDE_OFFLINE_DATA_OFFERS_AFTER = "edc.broker.server.hide.offline.data.offers.after";

    @Setting
    public static final String MAX_DATA_OFFERS_PER_CONNECTOR = "edc.broker.server.max.data.offers.per.connector";

    @Setting
    public static final String MAX_CONTRACT_OFFERS_PER_CONNECTOR = "edc.broker.server.max.contract.offers.per.connector";

    @Setting
    public static final String CATALOG_PAGE_PAGE_SIZE = "edc.broker.server.catalog.page.page.size";

    @Inject
    private ManagementApiConfiguration managementApiConfiguration;

    @Inject
    private WebService webService;

    @Inject
    private TypeManager typeManager;

    @Inject
    private CatalogService catalogService;

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
                typeManager,
                catalogService
        );

        // This is a hack for tests, so we can access the running context from tests.
        BrokerServerExtensionContext.instance = services;

        var managementApiGroup = managementApiConfiguration.getContextAlias();
        webService.registerResource(managementApiGroup, services.brokerServerResource());
    }

    @Override
    public void start() {
        services.brokerServerInitializer().onStartup();
    }
}
