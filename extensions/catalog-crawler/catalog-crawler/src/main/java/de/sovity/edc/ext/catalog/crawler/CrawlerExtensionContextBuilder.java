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
import de.sovity.edc.ext.catalog.crawler.crawling.ConnectorCrawler;
import de.sovity.edc.ext.catalog.crawler.crawling.OfflineConnectorCleaner;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.FetchedCatalogBuilder;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.FetchedCatalogMappingUtils;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.FetchedCatalogService;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerEventLogger;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.CrawlerExecutionTimeLogger;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.CatalogPatchBuilder;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.ConnectorUpdateCatalogWriter;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.ConnectorUpdateFailureWriter;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.ConnectorUpdateSuccessWriter;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.DataOfferLimitsEnforcer;
import de.sovity.edc.ext.catalog.crawler.dao.CatalogCleaner;
import de.sovity.edc.ext.catalog.crawler.dao.CatalogPatchApplier;
import de.sovity.edc.ext.catalog.crawler.dao.config.DataSourceFactory;
import de.sovity.edc.ext.catalog.crawler.dao.config.DslContextFactory;
import de.sovity.edc.ext.catalog.crawler.dao.config.FlywayService;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorQueries;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorStatusUpdater;
import de.sovity.edc.ext.catalog.crawler.dao.contract_offers.ContractOfferQueries;
import de.sovity.edc.ext.catalog.crawler.dao.contract_offers.ContractOfferRecordUpdater;
import de.sovity.edc.ext.catalog.crawler.dao.data_offers.DataOfferQueries;
import de.sovity.edc.ext.catalog.crawler.dao.data_offers.DataOfferRecordUpdater;
import de.sovity.edc.ext.catalog.crawler.orchestration.config.CrawlerConfigFactory;
import de.sovity.edc.ext.catalog.crawler.orchestration.queue.ConnectorQueue;
import de.sovity.edc.ext.catalog.crawler.orchestration.queue.ConnectorQueueFiller;
import de.sovity.edc.ext.catalog.crawler.orchestration.queue.ThreadPool;
import de.sovity.edc.ext.catalog.crawler.orchestration.queue.ThreadPoolTaskQueue;
import de.sovity.edc.ext.catalog.crawler.orchestration.schedules.DeadConnectorRefreshJob;
import de.sovity.edc.ext.catalog.crawler.orchestration.schedules.OfflineConnectorCleanerJob;
import de.sovity.edc.ext.catalog.crawler.orchestration.schedules.OfflineConnectorRefreshJob;
import de.sovity.edc.ext.catalog.crawler.orchestration.schedules.OnlineConnectorRefreshJob;
import de.sovity.edc.ext.catalog.crawler.orchestration.schedules.QuartzScheduleInitializer;
import de.sovity.edc.ext.catalog.crawler.orchestration.schedules.utils.CronJobRef;
import de.sovity.edc.ext.wrapper.api.common.mappers.AssetMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.PlaceholderEndpointService;
import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.AssetEditRequestMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.AssetJsonLdBuilder;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.AssetJsonLdParser;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.AssetJsonLdUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.EdcPropertyUtils;
import de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.ShortDescriptionBuilder;
import de.sovity.edc.ext.wrapper.api.common.mappers.dataaddress.DataSourceMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.dataaddress.http.HttpDataSourceMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.dataaddress.http.HttpHeaderMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.AtomicConstraintMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.ExpressionExtractor;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.ExpressionMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.LiteralMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.OperatorMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.PolicyValidator;
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
        CatalogService catalogService,
        PlaceholderEndpointService placeholderEndpointService
    ) {
        // Config
        var crawlerConfigFactory = new CrawlerConfigFactory(config);
        var crawlerConfig = crawlerConfigFactory.buildCrawlerConfig();

        // DB
        var dataSourceFactory = new DataSourceFactory(config);
        var dataSource = dataSourceFactory.newDataSource();
        var flywayService = new FlywayService(config, monitor, dataSource);
        flywayService.validateOrMigrateInTests();

        // Dao
        var dataOfferQueries = new DataOfferQueries();
        var dslContextFactory = new DslContextFactory(dataSource);
        var connectorQueries = new ConnectorQueries(crawlerConfig);

        // Services
        var objectMapperJsonLd = getJsonLdObjectMapper(typeManager);
        var assetMapper = newAssetMapper(typeTransformerRegistry, jsonLd, placeholderEndpointService);
        var policyMapper = newPolicyMapper(typeTransformerRegistry, objectMapperJsonLd);
        var crawlerEventLogger = new CrawlerEventLogger();
        var crawlerExecutionTimeLogger = new CrawlerExecutionTimeLogger();
        var dataOfferMappingUtils = new FetchedCatalogMappingUtils(
            policyMapper,
            assetMapper,
            objectMapperJsonLd
        );
        var contractOfferRecordUpdater = new ContractOfferRecordUpdater();
        var shortDescriptionBuilder = new ShortDescriptionBuilder();
        var dataOfferRecordUpdater = new DataOfferRecordUpdater(shortDescriptionBuilder);
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
        var offlineConnectorCleaner = new OfflineConnectorCleaner(
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
            getOfflineConnectorCleanerCronJob(dslContextFactory, offlineConnectorCleaner)
        );

        // Startup
        var quartzScheduleInitializer = new QuartzScheduleInitializer(config, monitor, jobs);
        var crawlerInitializer = new CrawlerInitializer(quartzScheduleInitializer);

        return new CrawlerExtensionContext(
            crawlerInitializer,
            dataSource,
            dslContextFactory,
            connectorUpdater,
            policyMapper,
            fetchedDataOfferBuilder,
            dataOfferRecordUpdater
        );
    }

    @NotNull
    private static PolicyMapper newPolicyMapper(
        TypeTransformerRegistry typeTransformerRegistry,
        ObjectMapper objectMapperJsonLd
    ) {
        var operatorMapper = new OperatorMapper();
        var literalMapper = new LiteralMapper(objectMapperJsonLd);
        var atomicConstraintMapper = new AtomicConstraintMapper(
            literalMapper,
            operatorMapper
        );
        var policyValidator = new PolicyValidator();
        var expressionMapper = new ExpressionMapper(atomicConstraintMapper);
        var constraintExtractor = new ExpressionExtractor(
            policyValidator,
            expressionMapper
        );
        return new PolicyMapper(
            constraintExtractor,
            expressionMapper,
            typeTransformerRegistry
        );
    }

    @NotNull
    private static AssetMapper newAssetMapper(
        TypeTransformerRegistry typeTransformerRegistry,
        JsonLd jsonLd,
        PlaceholderEndpointService placeholderEndpointService
    ) {
        var edcPropertyUtils = new EdcPropertyUtils();
        var assetJsonLdUtils = new AssetJsonLdUtils();
        var assetEditRequestMapper = new AssetEditRequestMapper();
        var shortDescriptionBuilder = new ShortDescriptionBuilder();
        var assetJsonLdParser = new AssetJsonLdParser(
            assetJsonLdUtils,
            shortDescriptionBuilder,
            endpoint -> false
        );
        var httpHeaderMapper = new HttpHeaderMapper();
        var httpDataSourceMapper = new HttpDataSourceMapper(httpHeaderMapper, placeholderEndpointService);
        var dataSourceMapper = new DataSourceMapper(
            edcPropertyUtils,
            httpDataSourceMapper
        );
        var assetJsonLdBuilder = new AssetJsonLdBuilder(
            dataSourceMapper,
            assetJsonLdParser,
            assetEditRequestMapper
        );
        return new AssetMapper(
            typeTransformerRegistry,
            assetJsonLdBuilder,
            assetJsonLdParser,
            jsonLd
        );
    }

    @NotNull
    private static CronJobRef<OfflineConnectorCleanerJob> getOfflineConnectorCleanerCronJob(DslContextFactory dslContextFactory,
                                                                                            OfflineConnectorCleaner offlineConnectorCleaner) {
        return new CronJobRef<>(
            CrawlerExtension.SCHEDULED_KILL_OFFLINE_CONNECTORS,
            OfflineConnectorCleanerJob.class,
            () -> new OfflineConnectorCleanerJob(dslContextFactory, offlineConnectorCleaner)
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
