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

import de.sovity.edc.ext.brokerserver.dao.ConnectorQueries;
import de.sovity.edc.ext.brokerserver.dao.DataOfferContractOfferQueries;
import de.sovity.edc.ext.brokerserver.dao.DataOfferQueries;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.CatalogQueryAvailableFilterFetcher;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.CatalogQueryContractOfferFetcher;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.CatalogQueryDataOfferFetcher;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.CatalogQueryFilterService;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.CatalogQueryService;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.CatalogQuerySortingService;
import de.sovity.edc.ext.brokerserver.dao.pages.connector.ConnectorPageQueryService;
import de.sovity.edc.ext.brokerserver.db.DataSourceFactory;
import de.sovity.edc.ext.brokerserver.db.DslContextFactory;
import de.sovity.edc.ext.brokerserver.services.BrokerServerInitializer;
import de.sovity.edc.ext.brokerserver.services.ConnectorCreator;
import de.sovity.edc.ext.brokerserver.services.KnownConnectorsInitializer;
import de.sovity.edc.ext.brokerserver.services.api.AssetPropertyParser;
import de.sovity.edc.ext.brokerserver.services.api.CatalogApiService;
import de.sovity.edc.ext.brokerserver.services.api.ConnectorApiService;
import de.sovity.edc.ext.brokerserver.services.api.PaginationMetadataUtils;
import de.sovity.edc.ext.brokerserver.services.api.PolicyDtoBuilder;
import de.sovity.edc.ext.brokerserver.services.api.filtering.CatalogFilterAttributeDefinitionService;
import de.sovity.edc.ext.brokerserver.services.api.filtering.CatalogFilterService;
import de.sovity.edc.ext.brokerserver.services.config.BrokerServerSettingsFactory;
import de.sovity.edc.ext.brokerserver.services.logging.BrokerEventLogger;
import de.sovity.edc.ext.brokerserver.services.logging.BrokerExecutionTimeLogger;
import de.sovity.edc.ext.brokerserver.services.queue.ConnectorQueue;
import de.sovity.edc.ext.brokerserver.services.queue.ConnectorQueueFiller;
import de.sovity.edc.ext.brokerserver.services.queue.ThreadPool;
import de.sovity.edc.ext.brokerserver.services.refreshing.ConnectorUpdateFailureWriter;
import de.sovity.edc.ext.brokerserver.services.refreshing.ConnectorUpdateSuccessWriter;
import de.sovity.edc.ext.brokerserver.services.refreshing.ConnectorUpdater;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.ContractOfferFetcher;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.ContractOfferRecordUpdater;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferBuilder;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferFetcher;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferLimitsEnforcer;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferPatchApplier;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferPatchBuilder;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferRecordUpdater;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferWriter;
import de.sovity.edc.ext.brokerserver.services.schedules.ConnectorRefreshJob;
import de.sovity.edc.ext.brokerserver.services.schedules.QuartzScheduleInitializer;
import de.sovity.edc.ext.brokerserver.services.schedules.utils.CronJobRef;
import lombok.NoArgsConstructor;
import org.eclipse.edc.connector.spi.catalog.CatalogService;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.spi.types.TypeManager;

import java.util.List;


/**
 * Manual Dependency Injection (DYDI).
 * <p>
 * We want to develop as Java Backend Development is done, but we have
 * no CDI / DI Framework to rely on.
 * <p>
 * EDC {@link Inject} only works in {@link BrokerServerExtension}.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BrokerServerExtensionContextBuilder {

    public static BrokerServerExtensionContext buildContext(
            Config config,
            Monitor monitor,
            TypeManager typeManager,
            CatalogService catalogService
    ) {
        var brokerServerSettingsFactory = new BrokerServerSettingsFactory();
        var brokerServerSettings = brokerServerSettingsFactory.buildBrokerServerSettings(config);

        // Dao
        var dataOfferQueries = new DataOfferQueries();
        var dataSourceFactory = new DataSourceFactory(config);
        var dataSource = dataSourceFactory.newDataSource();
        var dslContextFactory = new DslContextFactory(dataSource);
        var connectorQueries = new ConnectorQueries();
        var catalogQuerySortingService = new CatalogQuerySortingService();
        var catalogQueryFilterService = new CatalogQueryFilterService(brokerServerSettings);
        var catalogQueryContractOfferFetcher = new CatalogQueryContractOfferFetcher();
        var catalogQueryDataOfferFetcher = new CatalogQueryDataOfferFetcher(
                catalogQuerySortingService,
                catalogQueryFilterService,
                catalogQueryContractOfferFetcher
        );
        var catalogQueryAvailableFilterFetcher = new CatalogQueryAvailableFilterFetcher(catalogQueryFilterService);
        var catalogQueryService = new CatalogQueryService(
                catalogQueryDataOfferFetcher,
                catalogQueryAvailableFilterFetcher,
                brokerServerSettings
        );
        var connectorPageQueryService = new ConnectorPageQueryService();


        // Services
        var objectMapper = typeManager.getMapper();
        var brokerEventLogger = new BrokerEventLogger();
        var brokerExecutionTimeLogger = new BrokerExecutionTimeLogger();
        var contractOfferRecordUpdater = new ContractOfferRecordUpdater();
        var dataOfferRecordUpdater = new DataOfferRecordUpdater();
        var dataOfferContractOfferQueries = new DataOfferContractOfferQueries();
        var dataOfferLimitsEnforcer = new DataOfferLimitsEnforcer(config, brokerEventLogger);
        var dataOfferPatchBuilder = new DataOfferPatchBuilder(
                dataOfferContractOfferQueries,
                dataOfferQueries,
                dataOfferRecordUpdater,
                contractOfferRecordUpdater
        );
        var dataOfferPatchApplier = new DataOfferPatchApplier();
        var dataOfferWriter = new DataOfferWriter(dataOfferPatchBuilder, dataOfferPatchApplier);
        var connectorUpdateSuccessWriter = new ConnectorUpdateSuccessWriter(
                brokerEventLogger,
                dataOfferWriter,
                dataOfferLimitsEnforcer
        );
        var connectorUpdateFailureWriter = new ConnectorUpdateFailureWriter(brokerEventLogger, monitor);
        var contractOfferFetcher = new ContractOfferFetcher(catalogService);
        var fetchedDataOfferBuilder = new DataOfferBuilder(objectMapper);
        var dataOfferFetcher = new DataOfferFetcher(contractOfferFetcher, fetchedDataOfferBuilder);
        var connectorUpdater = new ConnectorUpdater(
                dataOfferFetcher,
                connectorUpdateSuccessWriter,
                connectorUpdateFailureWriter,
                connectorQueries,
                dslContextFactory,
                monitor,
                brokerExecutionTimeLogger
        );
        var policyDtoBuilder = new PolicyDtoBuilder(objectMapper);
        var assetPropertyParser = new AssetPropertyParser(objectMapper);
        var paginationMetadataUtils = new PaginationMetadataUtils();
        var threadPool = new ThreadPool(brokerServerSettings, monitor);
        var connectorQueue = new ConnectorQueue(connectorUpdater, threadPool);
        var connectorQueueFiller = new ConnectorQueueFiller(connectorQueue, connectorQueries);
        var connectorCreator = new ConnectorCreator(connectorQueries);
        var knownConnectorsInitializer = new KnownConnectorsInitializer(
                config,
                connectorQueue,
                connectorCreator
        );
        var catalogFilterAttributeDefinitionService = new CatalogFilterAttributeDefinitionService();
        var catalogFilterService = new CatalogFilterService(catalogFilterAttributeDefinitionService);

        // Schedules
        List<CronJobRef<?>> jobs = List.of(
                new CronJobRef<>(
                        BrokerServerExtension.CRON_CONNECTOR_REFRESH,
                        ConnectorRefreshJob.class,
                        () -> new ConnectorRefreshJob(dslContextFactory, connectorQueueFiller)
                )
        );

        // Startup
        var quartzScheduleInitializer = new QuartzScheduleInitializer(config, monitor, jobs);
        var brokerServerInitializer = new BrokerServerInitializer(
                dslContextFactory,
                knownConnectorsInitializer,
                quartzScheduleInitializer
        );

        // UI Capabilities
        var catalogApiService = new CatalogApiService(
                paginationMetadataUtils,
                catalogQueryService,
                policyDtoBuilder,
                assetPropertyParser,
                catalogFilterService,
                brokerServerSettings
        );
        var connectorApiService = new ConnectorApiService(
                connectorPageQueryService,
                paginationMetadataUtils
        );
        var brokerServerResource = new BrokerServerResourceImpl(
                dslContextFactory,
                connectorApiService,
                catalogApiService
        );
        return new BrokerServerExtensionContext(
                brokerServerResource,
                brokerServerInitializer,
                connectorUpdater,
                connectorCreator
        );
    }
}
