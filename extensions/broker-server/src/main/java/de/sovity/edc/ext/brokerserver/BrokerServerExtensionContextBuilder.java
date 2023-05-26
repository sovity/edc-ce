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

import de.sovity.edc.ext.brokerserver.dao.queries.ConnectorQueries;
import de.sovity.edc.ext.brokerserver.dao.queries.DataOfferQueries;
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
import de.sovity.edc.ext.brokerserver.services.logging.BrokerEventLogger;
import de.sovity.edc.ext.brokerserver.services.queue.ConnectorQueue;
import de.sovity.edc.ext.brokerserver.services.queue.ConnectorQueueFiller;
import de.sovity.edc.ext.brokerserver.services.refreshing.ConnectorSelfDescriptionFetcher;
import de.sovity.edc.ext.brokerserver.services.refreshing.ConnectorUpdateFailureWriter;
import de.sovity.edc.ext.brokerserver.services.refreshing.ConnectorUpdateSuccessWriter;
import de.sovity.edc.ext.brokerserver.services.refreshing.ConnectorUpdater;
import de.sovity.edc.ext.brokerserver.services.refreshing.ContractOfferFetcher;
import de.sovity.edc.ext.brokerserver.services.refreshing.sender.DescriptionRequestSender;
import de.sovity.edc.ext.brokerserver.services.refreshing.sender.IdsMultipartExtendedRemoteMessageDispatcher;
import de.sovity.edc.ext.brokerserver.services.schedules.ConnectorRefreshJob;
import de.sovity.edc.ext.brokerserver.services.schedules.QuartzScheduleInitializer;
import de.sovity.edc.ext.brokerserver.services.schedules.utils.CronJobRef;
import lombok.NoArgsConstructor;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.IdsMultipartSender;
import org.eclipse.edc.protocol.ids.spi.service.DynamicAttributeTokenService;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.http.EdcHttpClient;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.spi.types.TypeManager;

import java.util.List;


/**
 * Manual Dependency Injection.
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
            EdcHttpClient httpClient,
            DynamicAttributeTokenService dynamicAttributeTokenService,
            TypeManager typeManager,
            RemoteMessageDispatcherRegistry dispatcherRegistry
    ) {
        // Dao
        var dataOfferQueries = new DataOfferQueries();
        var dataSourceFactory = new DataSourceFactory(config);
        var dataSource = dataSourceFactory.newDataSource();
        var dslContextFactory = new DslContextFactory(dataSource);
        var connectorQueries = new ConnectorQueries();

        // IDS Message Client
        var objectMapper = typeManager.getMapper();
        var idsMultipartSender = new IdsMultipartSender(monitor, httpClient, dynamicAttributeTokenService, objectMapper);
        var remoteMessageDispatcher = new IdsMultipartExtendedRemoteMessageDispatcher(idsMultipartSender);
        var descriptionRequestSender = new DescriptionRequestSender();

        // Services
        var connectorSelfDescriptionFetcher = new ConnectorSelfDescriptionFetcher(dispatcherRegistry);
        var brokerEventLogger = new BrokerEventLogger();
        var connectorUpdateSuccessWriter = new ConnectorUpdateSuccessWriter(brokerEventLogger);
        var connectorUpdateFailureWriter = new ConnectorUpdateFailureWriter(brokerEventLogger);
        var contractOfferFetcher = new ContractOfferFetcher();
        var connectorUpdater = new ConnectorUpdater(
                connectorSelfDescriptionFetcher,
                connectorUpdateSuccessWriter,
                connectorUpdateFailureWriter,
                contractOfferFetcher,
                connectorQueries,
                dslContextFactory,
                monitor
        );
        var policyDtoBuilder = new PolicyDtoBuilder(objectMapper);
        var assetPropertyParser = new AssetPropertyParser(objectMapper);
        var paginationMetadataUtils = new PaginationMetadataUtils();
        var connectorQueue = new ConnectorQueue();
        var connectorQueueFiller = new ConnectorQueueFiller(connectorQueue, connectorQueries);
        var connectorCreator = new ConnectorCreator(connectorQueries);
        var knownConnectorsInitializer = new KnownConnectorsInitializer(config, connectorQueue, connectorCreator);

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
        var brokerServerInitializer = new BrokerServerInitializer(dslContextFactory, knownConnectorsInitializer, quartzScheduleInitializer);

        // UI Capabilities
        var catalogApiService = new CatalogApiService(
                paginationMetadataUtils,
                dataOfferQueries,
                policyDtoBuilder,
                assetPropertyParser
        );
        var connectorApiService = new ConnectorApiService(
                connectorQueries,
                paginationMetadataUtils
        );
        var brokerServerResource = new BrokerServerResourceImpl(
                dslContextFactory,
                connectorApiService,
                catalogApiService
        );
        return new BrokerServerExtensionContext(remoteMessageDispatcher, brokerServerResource, brokerServerInitializer);
    }
}
