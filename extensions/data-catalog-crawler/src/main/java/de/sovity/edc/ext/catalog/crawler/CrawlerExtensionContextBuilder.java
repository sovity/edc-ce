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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.sovity.edc.ext.catalog.crawler.dao.CatalogPatchApplier;
import de.sovity.edc.ext.catalog.crawler.dao.config.DataSourceFactory;
import de.sovity.edc.ext.catalog.crawler.dao.config.DslContextFactory;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorQueries;
import de.sovity.edc.ext.catalog.crawler.dao.contract_offers.ContractOfferQueries;
import de.sovity.edc.ext.catalog.crawler.dao.contract_offers.ContractOfferRecordUpdater;
import de.sovity.edc.ext.catalog.crawler.dao.data_offers.DataOfferQueries;
import de.sovity.edc.ext.catalog.crawler.dao.data_offers.DataOfferRecordUpdater;
import de.sovity.edc.ext.catalog.crawler.dao.CatalogCleaner;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorStatusUpdater;
import de.sovity.edc.ext.catalog.crawler.crawling.OfflineConnectorCleaner;
import de.sovity.edc.ext.catalog.crawler.orchestration.config.CrawlerConfigFactory;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventLogger;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerExecutionTimeLogger;
import de.sovity.edc.ext.catalog.crawler.orchestration.queue.ConnectorQueue;
import de.sovity.edc.ext.catalog.crawler.orchestration.queue.ConnectorQueueFiller;
import de.sovity.edc.ext.catalog.crawler.orchestration.queue.ThreadPool;
import de.sovity.edc.ext.catalog.crawler.orchestration.queue.ThreadPoolTaskQueue;
import de.sovity.edc.ext.catalog.crawler.crawling.ConnectorCrawler;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.FetchedCatalogBuilder;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.FetchedCatalogMappingUtils;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.FetchedCatalogService;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.CatalogPatchBuilder;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.ConnectorUpdateCatalogWriter;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.ConnectorUpdateFailureWriter;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.ConnectorUpdateSuccessWriter;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.DataOfferLimitsEnforcer;
import de.sovity.edc.ext.catalog.crawler.orchestration.schedules.DeadConnectorRefreshJob;
import de.sovity.edc.ext.catalog.crawler.orchestration.schedules.OfflineConnectorKillerJob;
import de.sovity.edc.ext.catalog.crawler.orchestration.schedules.OfflineConnectorRefreshJob;
import de.sovity.edc.ext.catalog.crawler.orchestration.schedules.OnlineConnectorRefreshJob;
import de.sovity.edc.ext.catalog.crawler.orchestration.schedules.QuartzScheduleInitializer;
import de.sovity.edc.ext.catalog.crawler.orchestration.schedules.utils.CronJobRef;
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
 * EDC {@link Inject} only works in {@link CrawlerExtension}.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CrawlerExtensionContextBuilder {

    public static CrawlerExtensionContext buildContext(
            Config config,
            Monitor monitor,
            TypeManager typeManager,
            TypeTransformerRegistry typeTransformerRegistry,
            JsonLd jsonLd,
            CatalogService catalogService
    ) {
        var crawlerConfigFactory = new CrawlerConfigFactory(config);
        var crawlerConfig = crawlerConfigFactory.buildCrawlerConfig();

        // Dao
        var dataOfferQueries = new DataOfferQueries();
        var dataSourceFactory = new DataSourceFactory(config);
        var dataSource = dataSourceFactory.newDataSource();
        var dslContextFactory = new DslContextFactory(dataSource);
        var connectorQueries = new ConnectorQueries();

        // Services
        var objectMapperJsonLd = getJsonLdObjectMapper(typeManager);
        var assetMapper = newAssetMapper(typeTransformerRegistry, jsonLd);
        var policyMapper = newPolicyMapper(typeTransformerRegistry, objectMapperJsonLd);
        var crawlerEventLogger = new CrawlerEventLogger();
        var crawlerExecutionTimeLogger = new CrawlerExecutionTimeLogger();
        var dataOfferMappingUtils = new FetchedCatalogMappingUtils(
                policyMapper,
                assetMapper,
                objectMapperJsonLd
        );
        var contractOfferRecordUpdater = new ContractOfferRecordUpdater();
        var dataOfferRecordUpdater = new DataOfferRecordUpdater(connectorQueries);
        var contractOfferQueries = new ContractOfferQueries();
        var dataOfferLimitsEnforcer = new DataOfferLimitsEnforcer(crawlerConfig, crawlerEventLogger);
        var dataOfferPatchBuilder = new CatalogPatchBuilder(
                contractOfferQueries,
                dataOfferQueries,
                dataOfferRecordUpdater,
                contractOfferRecordUpdater
        );
        var dataOfferPatchApplier = new CatalogPatchApplier();
        var dataOfferWriter = new ConnectorUpdateCatalogWriter(dataOfferPatchBuilder, dataOfferPatchApplier);
        var connectorUpdateSuccessWriter = new ConnectorUpdateSuccessWriter(
                crawlerEventLogger,
                dataOfferWriter,
                dataOfferLimitsEnforcer
        );
        var fetchedDataOfferBuilder = new FetchedCatalogBuilder(dataOfferMappingUtils);
        var dspDataOfferBuilder = new DspDataOfferBuilder(jsonLd);
        var dspCatalogService = new DspCatalogService(
                catalogService,
                dspDataOfferBuilder
        );
        var dataOfferFetcher = new FetchedCatalogService(dspCatalogService, fetchedDataOfferBuilder);
        var connectorUpdateFailureWriter = new ConnectorUpdateFailureWriter(crawlerEventLogger, monitor);
        var connectorUpdater = new ConnectorCrawler(
                dataOfferFetcher,
                connectorUpdateSuccessWriter,
                connectorUpdateFailureWriter,
                connectorQueries,
                dslContextFactory,
                monitor,
                crawlerExecutionTimeLogger
        );

        var threadPoolTaskQueue = new ThreadPoolTaskQueue();
        var threadPool = new ThreadPool(threadPoolTaskQueue, crawlerConfig, monitor);
        var connectorQueue = new ConnectorQueue(connectorUpdater, threadPool);
        var connectorQueueFiller = new ConnectorQueueFiller(connectorQueue, connectorQueries);
        var connectorStatusUpdater = new ConnectorStatusUpdater();
        var catalogCleaner = new CatalogCleaner();
        var offlineConnectorKiller = new OfflineConnectorCleaner(
                crawlerConfig,
                connectorQueries,
                crawlerEventLogger,
                connectorStatusUpdater,
                catalogCleaner
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
        var crawlerInitializer = new CrawlerInitializer(quartzScheduleInitializer);

        return new CrawlerExtensionContext(
                crawlerInitializer,
                dataSource,
                connectorUpdater,
                policyMapper,
                fetchedDataOfferBuilder,
                dataOfferRecordUpdater
        );
    }

    @NotNull
    private static PolicyMapper newPolicyMapper(TypeTransformerRegistry typeTransformerRegistry, ObjectMapper objectMapperJsonLd) {
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
        return new PolicyMapper(
                constraintExtractor,
                atomicConstraintMapper,
                typeTransformerRegistry
        );
    }

    @NotNull
    private static AssetMapper newAssetMapper(TypeTransformerRegistry typeTransformerRegistry, JsonLd jsonLd) {
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
        return new AssetMapper(
                typeTransformerRegistry,
                uiAssetMapper,
                jsonLd
        );
    }

    @NotNull
    private static CronJobRef<OfflineConnectorKillerJob> getOfflineConnectorKillerCronJob(DslContextFactory dslContextFactory,
                                                                                          OfflineConnectorCleaner offlineConnectorCleaner) {
        return new CronJobRef<>(
                CrawlerExtension.SCHEDULED_KILL_OFFLINE_CONNECTORS,
                OfflineConnectorKillerJob.class,
                () -> new OfflineConnectorKillerJob(dslContextFactory, offlineConnectorCleaner)
        );
    }

    @NotNull
    private static CronJobRef<OnlineConnectorRefreshJob> getOnlineConnectorRefreshCronJob(
            DslContextFactory dslContextFactory,
            ConnectorQueueFiller connectorQueueFiller
    ) {
        return new CronJobRef<>(
                CrawlerExtension.CRON_ONLINE_CONNECTOR_REFRESH,
                OnlineConnectorRefreshJob.class,
                () -> new OnlineConnectorRefreshJob(dslContextFactory, connectorQueueFiller)
        );
    }

    @NotNull
    private static CronJobRef<OfflineConnectorRefreshJob> getOfflineConnectorRefreshCronJob(
            DslContextFactory dslContextFactory,
            ConnectorQueueFiller connectorQueueFiller
    ) {
        return new CronJobRef<>(
                CrawlerExtension.CRON_OFFLINE_CONNECTOR_REFRESH,
                OfflineConnectorRefreshJob.class,
                () -> new OfflineConnectorRefreshJob(dslContextFactory, connectorQueueFiller)
        );
    }

    @NotNull
    private static CronJobRef<DeadConnectorRefreshJob> getDeadConnectorRefreshCronJob(DslContextFactory dslContextFactory,
                                                                                      ConnectorQueueFiller connectorQueueFiller) {
        return new CronJobRef<>(
                CrawlerExtension.CRON_DEAD_CONNECTOR_REFRESH,
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
