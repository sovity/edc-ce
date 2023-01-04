/*
 *  Copyright (c) 2022 sovity GmbH
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
package transfer;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.BaseConnector;
import de.fraunhofer.iais.eis.ConnectorEndpoint;
import de.fraunhofer.iais.eis.ConnectorUnavailableMessage;
import de.fraunhofer.iais.eis.ConnectorUpdateMessage;
import de.fraunhofer.iais.eis.QueryMessage;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.iais.eis.ResourceUnavailableMessage;
import de.fraunhofer.iais.eis.ResourceUpdateMessage;
import okhttp3.OkHttpClient;
import org.eclipse.edc.protocol.ids.api.configuration.IdsApiConfiguration;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.IdsMultipartSender;
import org.eclipse.edc.protocol.ids.jsonld.JsonLd;
import org.eclipse.edc.protocol.ids.service.ConnectorServiceSettings;
import org.eclipse.edc.protocol.ids.spi.service.DynamicAttributeTokenService;
import org.eclipse.edc.protocol.ids.spi.transform.IdsTransformerRegistry;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.Hostname;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import sender.QueryMessageRequestSender;
import sender.RegisterConnectorRequestSender;
import sender.RegisterResourceRequestSender;
import sender.UnregisterResourceRequestSender;
import sender.message.dispatcher.IdsMultipartExtendedRemoteMessageDispatcher;
import transfer.broker.IdsBrokerService;
import transfer.broker.IdsBrokerServiceImpl;
import transfer.serializer.MultiContextJsonLdSerializer;
import transfer.transfer.BrokerDefinitionProvider;
import transfer.transfer.SynchronizerImpl;
import transfer.transfer.TransferProcess;
import transfer.transfer.TransferProcessImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class TransferExtension implements ServiceExtension {

    public static final String CATALOG_TRANSFER_EXTENSION = "BrokerExtension";
    private static final String TYPE_MANAGER_SERIALIZER_KEY = "ids-broker";

    private static final Map<String, String> CONTEXT_MAP = Map.of(
            "cat", "http://w3id.org/mds/data-categories#",
            "ids", "https://w3id.org/idsa/core/",
            "idsc", "https://w3id.org/idsa/code/");
    @Setting
    public static final String BROKER_BASE_URL_SETTING = "edc.broker.base.url";

    @Setting
    private static final String EDC_CATALOG_URL = "edc.catalog.url";

    @Setting
    private static final String UPDATE_INTERVAL_IN_SECONDS = "update.interval.in.seconds";

    @Setting
    private static final String EDC_CONNECTOR_NAME = "edc.connector.name";

    @Setting
    private static final String EDC_IDS_ENDPOINT = "edc.ids.endpoint";

    @Inject
    private IdsApiConfiguration idsApiConfiguration;

    @Inject
    private RemoteMessageDispatcherRegistry dispatcherRegistry;

    @Inject
    private IdentityService identityService;

    @Inject
    private IdsTransformerRegistry transformerRegistry;

    @Inject
    private Hostname hostname;

    @Inject
    private OkHttpClient okHttpClient;

    @Inject
    private DynamicAttributeTokenService dynamicAttributeTokenService;

    @Inject
    private BrokerDefinitionProvider assetProvider;

    @Inject
    private AssetIndex assetIndex;

    private IdsBrokerService idsBrokerService;

    private TransferProcess catalogTransferProcess;

    private URL brokerBaseUrl;

    private Monitor monitor;

    @Override
    public String name() {
        return CATALOG_TRANSFER_EXTENSION;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        brokerBaseUrl = readUrlFromSettings(context, BROKER_BASE_URL_SETTING);
        monitor = context.getMonitor();

        var updateInterval = context.getSetting(UPDATE_INTERVAL_IN_SECONDS, "5");

        registerSerializerBrokerMessages(context);
        registerBrokerMessageSenders(context);

        initializeIdsBrokerService(
                context,
                dispatcherRegistry,
                hostname);

        initializeCatalogTransferProcess(
                idsBrokerService,
                monitor,
                updateInterval);
    }

    private void initializeCatalogTransferProcess(
            IdsBrokerService idsBrokerService,
            Monitor monitor,
            String updateInterval) {

        var catalogSynchronizer = new SynchronizerImpl(
                idsBrokerService,
                monitor,
                assetProvider,
                assetIndex);
        catalogTransferProcess = new TransferProcessImpl(
                catalogSynchronizer,
                monitor,
                updateInterval);
    }

    private void initializeIdsBrokerService(
            ServiceExtensionContext context,
            RemoteMessageDispatcherRegistry dispatcherRegistry,
            Hostname hostname) {
        var connectorServiceSettings = new ConnectorServiceSettings(context, context.getMonitor());
        idsBrokerService = new IdsBrokerServiceImpl(
                dispatcherRegistry,
                connectorServiceSettings,
                hostname);
    }

    private URL readUrlFromSettings(ServiceExtensionContext context, String settingsPath) {
        try {
            var urlString = context.getSetting(settingsPath, null);
            if (urlString == null && !EDC_CATALOG_URL.equals(settingsPath)) {
                throw new EdcException(String.format("Could not initialize " +
                        "CatalogTransferExtension: " +
                        "No url specified using setting %s", settingsPath));
            } else if (urlString == null) {
                return null;
            }

            return new URL(urlString);
        } catch (MalformedURLException e) {
            throw new EdcException(String.format("Could not parse setting %s to Url",
                    settingsPath), e);
        }
    }

    private void registerSerializerBrokerMessages(ServiceExtensionContext context) {
        var typeManager = context.getTypeManager();
        typeManager.registerContext(TYPE_MANAGER_SERIALIZER_KEY, JsonLd.getObjectMapper());
        registerCommonTypes(typeManager);
        registerConnectorMessages(typeManager);
        registerResourceMessages(typeManager);
        registerQueryMessage(typeManager);
    }

    private void registerQueryMessage(TypeManager typeManager) {
        typeManager.registerSerializer(TYPE_MANAGER_SERIALIZER_KEY, QueryMessage.class,
                new MultiContextJsonLdSerializer<>(QueryMessage.class, CONTEXT_MAP));
    }

    private void registerCommonTypes(TypeManager typeManager) {
        typeManager.registerSerializer(TYPE_MANAGER_SERIALIZER_KEY, ResourceUpdateMessage.class,
                new MultiContextJsonLdSerializer<>(ResourceUpdateMessage.class, CONTEXT_MAP));
        typeManager.registerSerializer(TYPE_MANAGER_SERIALIZER_KEY, Artifact.class,
                new MultiContextJsonLdSerializer<>(Artifact.class, CONTEXT_MAP));
        typeManager.registerSerializer(TYPE_MANAGER_SERIALIZER_KEY, ResourceCatalog.class,
                new MultiContextJsonLdSerializer<>(ResourceCatalog.class, CONTEXT_MAP));
    }

    private void registerResourceMessages(TypeManager typeManager) {
        typeManager.registerSerializer(TYPE_MANAGER_SERIALIZER_KEY, ResourceUpdateMessage.class,
                new MultiContextJsonLdSerializer<>(ResourceUpdateMessage.class, CONTEXT_MAP));
        typeManager.registerSerializer(TYPE_MANAGER_SERIALIZER_KEY,
                ResourceUnavailableMessage.class,
                new MultiContextJsonLdSerializer<>(ResourceUnavailableMessage.class, CONTEXT_MAP));
        typeManager.registerSerializer(TYPE_MANAGER_SERIALIZER_KEY, Resource.class,
                new MultiContextJsonLdSerializer<>(Resource.class, CONTEXT_MAP));
    }

    private void registerConnectorMessages(TypeManager typeManager) {
        typeManager.registerSerializer(TYPE_MANAGER_SERIALIZER_KEY, ConnectorUpdateMessage.class,
                new MultiContextJsonLdSerializer<>(ConnectorUpdateMessage.class, CONTEXT_MAP));
        typeManager.registerSerializer(TYPE_MANAGER_SERIALIZER_KEY, BaseConnector.class,
                new MultiContextJsonLdSerializer<>(BaseConnector.class, CONTEXT_MAP));
        typeManager.registerSerializer(TYPE_MANAGER_SERIALIZER_KEY, ConnectorEndpoint.class,
                new MultiContextJsonLdSerializer<>(ConnectorEndpoint.class, CONTEXT_MAP));
        typeManager.registerSerializer(TYPE_MANAGER_SERIALIZER_KEY,
                ConnectorUnavailableMessage.class,
                new MultiContextJsonLdSerializer<>(ConnectorUnavailableMessage.class, CONTEXT_MAP));
    }

    private void registerBrokerMessageSenders(ServiceExtensionContext context) {
        var httpClient = context.getService(OkHttpClient.class);
        var monitor = context.getMonitor();
        var typeManager = context.getTypeManager();
        var objectMapper = typeManager.getMapper(TYPE_MANAGER_SERIALIZER_KEY);
        var connectorName = context.getSetting(EDC_CONNECTOR_NAME, "EDC");
        var endpoint = context.getSetting(EDC_IDS_ENDPOINT, "http://endpoint");

        var registerConnectorSender = new RegisterConnectorRequestSender(objectMapper, connectorName, endpoint);
        var registerResourceSender = new RegisterResourceRequestSender(objectMapper);
        var unregisterResourceSender = new UnregisterResourceRequestSender();
        var queryMessageRequestSender = new QueryMessageRequestSender();

        var idsMultipartSender = new IdsMultipartSender(monitor, httpClient, dynamicAttributeTokenService, objectMapper);
        var dispatcher = new IdsMultipartExtendedRemoteMessageDispatcher(idsMultipartSender);
        dispatcher.register(registerConnectorSender);
        dispatcher.register(registerResourceSender);
        dispatcher.register(unregisterResourceSender);
        dispatcher.register(queryMessageRequestSender);
        dispatcherRegistry.register(dispatcher);
    }

    @Override
    public void start() {
        try {
            idsBrokerService.registerConnectorAtBroker(brokerBaseUrl);
            catalogTransferProcess.startTransferProcess(brokerBaseUrl);
        } catch (Exception e) {
            monitor.severe(String.format("%s failed during startup: ", CATALOG_TRANSFER_EXTENSION), e);
        }
    }

    @Override
    public void prepare() {
        ServiceExtension.super.prepare();
    }
}
