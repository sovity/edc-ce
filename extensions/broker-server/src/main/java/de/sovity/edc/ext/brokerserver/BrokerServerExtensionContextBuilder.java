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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.sovity.edc.ext.brokerserver.dao.ConnectorQueries;
import de.sovity.edc.ext.brokerserver.dao.ContractOfferQueries;
import de.sovity.edc.ext.brokerserver.dao.DataOfferQueries;
import de.sovity.edc.ext.brokerserver.db.DataSourceFactory;
import de.sovity.edc.ext.brokerserver.db.DslContextFactory;
import de.sovity.edc.ext.brokerserver.services.BrokerServerInitializer;
import de.sovity.edc.ext.brokerserver.services.ConnectorCleaner;
import de.sovity.edc.ext.brokerserver.services.ConnectorKiller;
import de.sovity.edc.ext.brokerserver.services.KnownConnectorsInitializer;
import de.sovity.edc.ext.brokerserver.services.OfflineConnectorKiller;
import de.sovity.edc.ext.brokerserver.services.api.ConnectorOnlineStatusMapper;
import de.sovity.edc.ext.brokerserver.services.api.DataOfferMappingUtils;
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
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.CatalogFetcher;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.ContractOfferRecordUpdater;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferLimitsEnforcer;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferPatchApplier;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferPatchBuilder;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferRecordUpdater;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.DataOfferWriter;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.FetchedCatalogBuilder;
import de.sovity.edc.ext.brokerserver.services.schedules.DeadConnectorRefreshJob;
import de.sovity.edc.ext.brokerserver.services.schedules.OfflineConnectorKillerJob;
import de.sovity.edc.ext.brokerserver.services.schedules.OfflineConnectorRefreshJob;
import de.sovity.edc.ext.brokerserver.services.schedules.OnlineConnectorRefreshJob;
import de.sovity.edc.ext.brokerserver.services.schedules.QuartzScheduleInitializer;
import de.sovity.edc.ext.brokerserver.services.schedules.utils.CronJobRef;
import de.sovity.edc.ext.wrapper.api.common.mappers.AssetMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.OperatorMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.AssetJsonLdUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.AtomicConstraintMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.ConstraintExtractor;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.EdcPropertyUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.LiteralMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.MarkdownToTextConverter;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.PolicyValidator;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.TextUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.UiAssetMapper;
import de.sovity.edc.utils.catalog.DspCatalogService;
import de.sovity.edc.utils.catalog.mapper.DspDataOfferBuilder;
import lombok.NoArgsConstructor;
import org.eclipse.edc.connector.spi.catalog.CatalogService;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.CoreConstants;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
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
            TypeTransformerRegistry typeTransformerRegistry,
            JsonLd jsonLd,
            CatalogService catalogService
    ) {
        var brokerServerSettingsFactory = new BrokerServerSettingsFactory(config, monitor);
        var brokerServerSettings = brokerServerSettingsFactory.buildBrokerServerSettings();

        // Dao
        var dataOfferQueries = new DataOfferQueries();
        var dataSourceFactory = new DataSourceFactory(config);
        var dataSource = dataSourceFactory.newDataSource();
        var dslContextFactory = new DslContextFactory(dataSource);
        var connectorQueries = new ConnectorQueries();

        // Services
        var objectMapperJsonLd = getJsonLdObjectMapper(typeManager);
        var brokerEventLogger = new BrokerEventLogger();
        var brokerExecutionTimeLogger = new BrokerExecutionTimeLogger();
        var contractOfferRecordUpdater = new ContractOfferRecordUpdater(dataOfferMappingUtils);
        var dataOfferRecordUpdater = new DataOfferRecordUpdater(connectorQueries);
        var contractOfferQueries = new ContractOfferQueries();
        var dataOfferLimitsEnforcer = new DataOfferLimitsEnforcer(brokerServerSettings, brokerEventLogger);
        var dataOfferPatchBuilder = new DataOfferPatchBuilder(
                contractOfferQueries,
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
        var edcPropertyUtils = new EdcPropertyUtils();
        var assetJsonLdUtils = new AssetJsonLdUtils();
        var markdownToTextConverter = new MarkdownToTextConverter();
        var textUtils = new TextUtils();
        var uiAssetMapper = new UiAssetMapper(
                edcPropertyUtils,
                assetJsonLdUtils,
                markdownToTextConverter,
                textUtils,
                endpoint -> false
        );
        var assetMapper = new AssetMapper(
                typeTransformerRegistry,
                uiAssetMapper,
                jsonLd
        );
        var fetchedDataOfferBuilder = new FetchedCatalogBuilder(assetMapper);
        var dspDataOfferBuilder = new DspDataOfferBuilder(jsonLd);
        var dspCatalogService = new DspCatalogService(
                catalogService,
                dspDataOfferBuilder
        );
        var dataOfferFetcher = new CatalogFetcher(dspCatalogService, fetchedDataOfferBuilder);
        var connectorUpdateFailureWriter = new ConnectorUpdateFailureWriter(brokerEventLogger, monitor);
        var connectorUpdater = new ConnectorUpdater(
                dataOfferFetcher,
                connectorUpdateSuccessWriter,
                connectorUpdateFailureWriter,
                connectorQueries,
                dslContextFactory,
                monitor,
                brokerExecutionTimeLogger
        );

        var threadPoolTaskQueue = new ThreadPoolTaskQueue();
        var threadPool = new ThreadPool(threadPoolTaskQueue, brokerServerSettings, monitor);
        var connectorQueue = new ConnectorQueue(connectorUpdater, threadPool);
        var connectorQueueFiller = new ConnectorQueueFiller(connectorQueue, connectorQueries);
        var knownConnectorsInitializer = new KnownConnectorsInitializer(
                config,
                connectorQueue
        );
        var connectorKiller = new ConnectorKiller();
        var connectorClearer = new ConnectorCleaner(connectorQueries);
        var offlineConnectorKiller = new OfflineConnectorKiller(
                brokerServerSettings,
                connectorQueries,
                brokerEventLogger,
                connectorKiller,
                connectorClearer
        );
        var operatorMapper = new OperatorMapper();
        var literalMapper = new LiteralMapper(
                objectMapperJsonLd
        );
        var atomicConstraintMapper = new AtomicConstraintMapper(
                literalMapper,
                operatorMapper
        );
        var policyValidator = new PolicyValidator();
        var constraintExtractor = new ConstraintExtractor(
                policyValidator,
                atomicConstraintMapper
        );
        var policyMapper = new PolicyMapper(
                constraintExtractor,
                atomicConstraintMapper,
                typeTransformerRegistry
        );
        var dataOfferMappingUtils = new DataOfferMappingUtils(
                policyMapper,
                assetMapper
        );
        var connectorOnlineStatusMapper = new ConnectorOnlineStatusMapper();

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

        return new BrokerServerExtensionContext(
                brokerServerInitializer,
                connectorUpdater,
                policyMapper,
                fetchedDataOfferBuilder,
                dataOfferRecordUpdater
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

    private static ObjectMapper getJsonLdObjectMapper(TypeManager typeManager) {
        var objectMapper = typeManager.getMapper(CoreConstants.JSON_LD);

        // Fixes Dates in JSON-LD Object Mapper
        // The Core EDC uses longs over OffsetDateTime, so they never fixed the date format
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }
}
