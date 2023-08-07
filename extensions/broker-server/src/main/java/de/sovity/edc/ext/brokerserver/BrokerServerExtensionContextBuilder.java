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
import de.sovity.edc.ext.brokerserver.dao.pages.dataoffer.DataOfferDetailPageQueryService;
import de.sovity.edc.ext.brokerserver.dao.pages.dataoffer.ViewCountLogger;
import de.sovity.edc.ext.brokerserver.db.DataSourceFactory;
import de.sovity.edc.ext.brokerserver.db.DslContextFactory;
import de.sovity.edc.ext.brokerserver.services.BrokerServerInitializer;
import de.sovity.edc.ext.brokerserver.services.ConnectorCleaner;
import de.sovity.edc.ext.brokerserver.services.ConnectorCreator;
import de.sovity.edc.ext.brokerserver.services.ConnectorKiller;
import de.sovity.edc.ext.brokerserver.services.KnownConnectorsInitializer;
import de.sovity.edc.ext.brokerserver.services.OfflineConnectorKiller;
import de.sovity.edc.ext.brokerserver.services.api.AssetPropertyParser;
import de.sovity.edc.ext.brokerserver.services.api.CatalogApiService;
import de.sovity.edc.ext.brokerserver.services.api.ConnectorApiService;
import de.sovity.edc.ext.brokerserver.services.api.ConnectorService;
import de.sovity.edc.ext.brokerserver.services.api.DataOfferDetailApiService;
import de.sovity.edc.ext.brokerserver.services.api.PaginationMetadataUtils;
import de.sovity.edc.ext.brokerserver.services.api.PolicyDtoBuilder;
import de.sovity.edc.ext.brokerserver.services.api.filtering.CatalogFilterAttributeDefinitionService;
import de.sovity.edc.ext.brokerserver.services.api.filtering.CatalogFilterService;
import de.sovity.edc.ext.brokerserver.services.config.AdminApiKeyValidator;
import de.sovity.edc.ext.brokerserver.services.config.BrokerServerSettingsFactory;
import de.sovity.edc.ext.brokerserver.services.logging.BrokerEventLogger;
import de.sovity.edc.ext.brokerserver.services.logging.BrokerExecutionTimeLogger;
import de.sovity.edc.ext.brokerserver.services.queue.ConnectorQueue;
import de.sovity.edc.ext.brokerserver.services.queue.ConnectorQueueFiller;
import de.sovity.edc.ext.brokerserver.services.queue.ThreadPool;
import de.sovity.edc.ext.brokerserver.services.queue.ThreadPoolTaskQueue;
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
import de.sovity.edc.ext.brokerserver.services.schedules.DeadConnectorRefreshJob;
import de.sovity.edc.ext.brokerserver.services.schedules.OfflineConnectorKillerJob;
import de.sovity.edc.ext.brokerserver.services.schedules.OfflineConnectorRefreshJob;
import de.sovity.edc.ext.brokerserver.services.schedules.OnlineConnectorRefreshJob;
import de.sovity.edc.ext.brokerserver.services.schedules.QuartzScheduleInitializer;
import de.sovity.edc.ext.brokerserver.services.schedules.utils.CronJobRef;
import lombok.NoArgsConstructor;
import org.eclipse.edc.connector.spi.catalog.CatalogService;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.spi.types.TypeManager;
import org.jetbrains.annotations.NotNull;

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
        var brokerServerSettingsFactory = new BrokerServerSettingsFactory(config, monitor);
        var brokerServerSettings = brokerServerSettingsFactory.buildBrokerServerSettings();
        var adminApiKeyValidator = new AdminApiKeyValidator(brokerServerSettings);

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
        var dataOfferDetailPageQueryService = new DataOfferDetailPageQueryService(
                catalogQueryContractOfferFetcher, brokerServerSettings);


        // Services
        var objectMapper = typeManager.getMapper();
        var brokerEventLogger = new BrokerEventLogger();
        var brokerExecutionTimeLogger = new BrokerExecutionTimeLogger();
        var contractOfferRecordUpdater = new ContractOfferRecordUpdater();
        var dataOfferRecordUpdater = new DataOfferRecordUpdater();
        var dataOfferContractOfferQueries = new DataOfferContractOfferQueries();
        var dataOfferLimitsEnforcer = new DataOfferLimitsEnforcer(brokerServerSettings, brokerEventLogger);
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
        var policyDtoBuilder = new PolicyDtoBuilder();
        var assetPropertyParser = new AssetPropertyParser(objectMapper);
        var paginationMetadataUtils = new PaginationMetadataUtils();
        var threadPoolTaskQueue = new ThreadPoolTaskQueue();
        var threadPool = new ThreadPool(threadPoolTaskQueue, brokerServerSettings, monitor);
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
        var viewCountLogger = new ViewCountLogger();
        var connectorService = new ConnectorService(connectorCreator, connectorQueue);
        var connectorKiller = new ConnectorKiller();
        var connectorClearer = new ConnectorCleaner();
        var offlineConnectorKiller = new OfflineConnectorKiller(
                brokerServerSettings,
                connectorQueries,
                brokerEventLogger,
                connectorKiller,
                connectorClearer
        );

        // Schedules
        List<CronJobRef<?>> jobs = List.of(
                getOnlineConnectorRefreshCronJob(dslContextFactory, connectorQueueFiller),
                getOfflineConnectorRefreshCronJob(dslContextFactory, connectorQueueFiller),
                getDeadConnectorRefreshCronJob(dslContextFactory, connectorQueueFiller),
                getOfflineConnectorKillerCronJob(dslContextFactory, offlineConnectorKiller)
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
                connectorService,
                paginationMetadataUtils
        );
        var dataOfferDetailApiService = new DataOfferDetailApiService(
                dataOfferDetailPageQueryService,
                viewCountLogger,
                policyDtoBuilder,
                assetPropertyParser
        );
        var brokerServerResource = new BrokerServerResourceImpl(
                dslContextFactory,
                connectorApiService,
                catalogApiService,
                dataOfferDetailApiService,
                adminApiKeyValidator
        );

        return new BrokerServerExtensionContext(
                brokerServerResource,
                brokerServerInitializer,
                connectorUpdater,
                connectorCreator
        );
    }

    @NotNull
    private static CronJobRef<OfflineConnectorKillerJob> getOfflineConnectorKillerCronJob(DslContextFactory dslContextFactory, OfflineConnectorKiller offlineConnectorKiller) {
        return new CronJobRef<>(
                BrokerServerExtension.SCHEDULED_KILL_OFFLINE_CONNECTORS,
                OfflineConnectorKillerJob.class,
                () -> new OfflineConnectorKillerJob(dslContextFactory, offlineConnectorKiller)
        );
    }

    @NotNull
    private static CronJobRef<OnlineConnectorRefreshJob> getOnlineConnectorRefreshCronJob(DslContextFactory dslContextFactory, ConnectorQueueFiller connectorQueueFiller) {
        return new CronJobRef<>(
                BrokerServerExtension.CRON_ONLINE_CONNECTOR_REFRESH,
                OnlineConnectorRefreshJob.class,
                () -> new OnlineConnectorRefreshJob(dslContextFactory, connectorQueueFiller)
        );
    }

    @NotNull
    private static CronJobRef<OfflineConnectorRefreshJob> getOfflineConnectorRefreshCronJob(DslContextFactory dslContextFactory, ConnectorQueueFiller connectorQueueFiller) {
        return new CronJobRef<>(
                BrokerServerExtension.CRON_OFFLINE_CONNECTOR_REFRESH,
                OfflineConnectorRefreshJob.class,
                () -> new OfflineConnectorRefreshJob(dslContextFactory, connectorQueueFiller)
        );
    }

    @NotNull
    private static CronJobRef<DeadConnectorRefreshJob> getDeadConnectorRefreshCronJob(DslContextFactory dslContextFactory, ConnectorQueueFiller connectorQueueFiller) {
        return new CronJobRef<>(
                BrokerServerExtension.CRON_DEAD_CONNECTOR_REFRESH,
                DeadConnectorRefreshJob.class,
                () -> new DeadConnectorRefreshJob(dslContextFactory, connectorQueueFiller)
        );
    }
}
