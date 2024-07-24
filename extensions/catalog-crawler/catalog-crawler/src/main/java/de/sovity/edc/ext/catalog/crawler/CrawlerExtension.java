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

package de.sovity.edc.ext.catalog.crawler;

import de.sovity.edc.ext.wrapper.api.common.mappers.PlaceholderEndpointService;
import org.eclipse.edc.connector.api.management.configuration.transform.ManagementApiTypeTransformerRegistry;
import org.eclipse.edc.connector.spi.catalog.CatalogService;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;

import static de.sovity.edc.ext.catalog.crawler.orchestration.config.EdcConfigPropertyUtils.toEdcProp;

@Provides({CrawlerExtensionContext.class})
public class CrawlerExtension implements ServiceExtension {

    public static final String EXTENSION_NAME = "Authority Portal Data Catalog Crawler";

    @Setting(required = true)
    public static final String EXTENSION_ENABLED = toEdcProp("CRAWLER_EXTENSION_ENABLED");

    @Setting(required = true)
    public static final String ENVIRONMENT_ID = toEdcProp("CRAWLER_ENVIRONMENT_ID");

    @Setting(required = true)
    public static final String JDBC_URL = toEdcProp("CRAWLER_DB_JDBC_URL");

    @Setting(required = true)
    public static final String JDBC_USER = toEdcProp("CRAWLER_DB_JDBC_USER");

    @Setting(required = true)
    public static final String JDBC_PASSWORD = toEdcProp("CRAWLER_DB_JDBC_PASSWORD");

    @Setting
    public static final String DB_CONNECTION_POOL_SIZE = toEdcProp("CRAWLER_DB_CONNECTION_POOL_SIZE");

    @Setting
    public static final String DB_CONNECTION_TIMEOUT_IN_MS = toEdcProp("CRAWLER_DB_CONNECTION_TIMEOUT_IN_MS");

    @Setting
    public static final String DB_MIGRATE = toEdcProp("CRAWLER_DB_MIGRATE");

    @Setting
    public static final String DB_CLEAN = toEdcProp("CRAWLER_DB_CLEAN");

    @Setting
    public static final String DB_CLEAN_ENABLED = toEdcProp("CRAWLER_DB_CLEAN_ENABLED");

    @Setting
    public static final String DB_ADDITIONAL_FLYWAY_MIGRATION_LOCATIONS = toEdcProp("CRAWLER_DB_ADDITIONAL_FLYWAY_LOCATIONS");

    @Setting
    public static final String NUM_THREADS = toEdcProp("CRAWLER_NUM_THREADS");

    @Setting
    public static final String MAX_DATA_OFFERS_PER_CONNECTOR = toEdcProp("CRAWLER_MAX_DATA_OFFERS_PER_CONNECTOR");

    @Setting
    public static final String MAX_CONTRACT_OFFERS_PER_DATA_OFFER = toEdcProp("CRAWLER_MAX_CONTRACT_OFFERS_PER_DATA_OFFER");

    @Setting
    public static final String CRON_ONLINE_CONNECTOR_REFRESH = toEdcProp("CRAWLER_CRON_ONLINE_CONNECTOR_REFRESH");

    @Setting
    public static final String CRON_OFFLINE_CONNECTOR_REFRESH = toEdcProp("CRAWLER_CRON_OFFLINE_CONNECTOR_REFRESH");

    @Setting
    public static final String CRON_DEAD_CONNECTOR_REFRESH = toEdcProp("CRAWLER_CRON_DEAD_CONNECTOR_REFRESH");

    @Setting
    public static final String SCHEDULED_KILL_OFFLINE_CONNECTORS = toEdcProp("CRAWLER_SCHEDULED_KILL_OFFLINE_CONNECTORS");
    @Setting
    public static final String KILL_OFFLINE_CONNECTORS_AFTER = toEdcProp("CRAWLER_KILL_OFFLINE_CONNECTORS_AFTER");

    @Inject
    private TypeManager typeManager;

    @Inject
    private ManagementApiTypeTransformerRegistry typeTransformerRegistry;

    @Inject
    private JsonLd jsonLd;

    @Inject
    private CatalogService catalogService;

    /**
     * Manual Dependency Injection Result
     */
    private CrawlerExtensionContext services;

    @Override
    public String name() {
        return EXTENSION_NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        if (!Boolean.TRUE.equals(context.getConfig().getBoolean(EXTENSION_ENABLED, false))) {
            context.getMonitor().info("Crawler extension is disabled.");
            return;
        }

        services = CrawlerExtensionContextBuilder.buildContext(
            context.getConfig(),
            context.getMonitor(),
            typeManager,
            typeTransformerRegistry,
            jsonLd,
            catalogService,
            new PlaceholderEndpointService("http://0.0.0.0/")
        );

        // Provide access for the tests
        context.registerService(CrawlerExtensionContext.class, services);
    }

    @Override
    public void start() {
        if (services == null) {
            return;
        }
        services.crawlerInitializer().onStartup();
    }

    @Override
    public void shutdown() {
        if (services == null) {
            return;
        }
        services.dataSource().close();
    }
}
