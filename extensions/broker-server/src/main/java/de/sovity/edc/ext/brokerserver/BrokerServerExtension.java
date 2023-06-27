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

import static de.sovity.edc.ext.brokerserver.services.config.EdcConfigPropertyUtils.toEdcProp;

public class BrokerServerExtension implements ServiceExtension {

    public static final String EXTENSION_NAME = "BrokerServerExtension";

    @Setting
    public static final String KNOWN_CONNECTORS = toEdcProp("EDC_BROKER_SERVER_KNOWN_CONNECTORS");

    @Setting
    public static final String CRON_CONNECTOR_REFRESH = toEdcProp("EDC_BROKER_SERVER_CRON_CONNECTOR_REFRESH");

    @Setting
    public static final String NUM_THREADS = toEdcProp("EDC_BROKER_SERVER_NUM_THREADS");

    @Setting
    public static final String HIDE_OFFLINE_DATA_OFFERS_AFTER = toEdcProp("EDC_BROKER_SERVER_HIDE_OFFLINE_DATA_OFFERS_AFTER");

    @Setting
    public static final String MAX_DATA_OFFERS_PER_CONNECTOR = toEdcProp("EDC_BROKER_SERVER_MAX_DATA_OFFERS_PER_CONNECTOR");

    @Setting
    public static final String MAX_CONTRACT_OFFERS_PER_DATA_OFFER = toEdcProp("EDC_BROKER_SERVER_MAX_CONTRACT_OFFERS_PER_DATA_OFFER");

    @Setting
    public static final String CATALOG_PAGE_PAGE_SIZE = toEdcProp("EDC_BROKER_SERVER_CATALOG_PAGE_PAGE_SIZE");

    @Setting
    public static final String DEFAULT_CONNECTOR_DATASPACE = toEdcProp("EDC_BROKER_SERVER_DEFAULT_DATASPACE");

    @Setting
    public static final String KNOWN_DATASPACE_CONNECTORS = toEdcProp("EDC_BROKER_SERVER_KNOWN_DATASPACE_CONNECTORS");

    @Setting
    public static final String DELETE_OFFLINE_CONNECTORS_AFTER = toEdcProp("EDC_BROKER_SERVER_DELETE_OFFLINE_CONNECTORS_AFTER");

    @Setting
    public static final String SCHEDULED_DELETE_OFFLINE_CONNECTORS = toEdcProp("EDC_BROKER_SERVER_SCHEDULED_DELETE_OFFLINE_CONNECTORS");

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
