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
package catalogtransfer.broker;

import org.eclipse.edc.protocol.ids.service.ConnectorServiceSettings;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.system.Hostname;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import sender.message.QueryMessage;
import sender.message.RegisterConnectorMessage;
import sender.message.RegisterResourceMessage;
import sender.message.UnregisterResourceMessage;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class IdsBrokerServiceImpl implements IdsBrokerService {

    private static final String CONTEXT_BROKER_REGISTRATION = "BrokerRegistration";

    private final RemoteMessageDispatcherRegistry dispatcherRegistry;
    private final ConnectorServiceSettings connectorServiceSettings;
    private final Hostname hostname;

    public IdsBrokerServiceImpl(
            RemoteMessageDispatcherRegistry dispatcherRegistry,
            ConnectorServiceSettings connectorServiceSettings,
            Hostname hostname) {
        this.dispatcherRegistry = dispatcherRegistry;
        this.connectorServiceSettings = connectorServiceSettings;
        this.hostname = hostname;
    }

    @Override
    public void registerConnectorAtBroker(URL brokerBaseUrl) {
        try {
            var brokerInfrastructureUrl = new URL(String.format("%s/infrastructure",
                    brokerBaseUrl));
            var connectorBaseUrl = new URI(String.format("http://%s/", hostname.get()));
            var registerConnectorMessage = new RegisterConnectorMessage(brokerInfrastructureUrl,
                    connectorBaseUrl,
                    connectorServiceSettings.getCurator(),
                    connectorServiceSettings.getMaintainer());
            dispatcherRegistry.send(Object.class, registerConnectorMessage,
                    () -> CONTEXT_BROKER_REGISTRATION);
        } catch (MalformedURLException e) {
            throw new EdcException("Could not build brokerInfrastructureUrl", e);
        } catch (URISyntaxException e) {
            throw new EdcException("Could not create connectorBaseUrl. Hostname can be set using:" +
                    " edc.hostname", e);
        }
    }

    @Override
    public void unregisterConnectorAtBroker(URL brokerBaseUrl) {

    }

    @Override
    public void registerResourceAtBroker(URL brokerBaseUrl, String resourceId, Asset asset) {
        try {
            var resourceUriId = new URI(String.format("https://test.connector.de/catalog/%s", resourceId));
            var brokerInfrastructureUrl = new URL(String.format("%s/infrastructure",
                    brokerBaseUrl));
            var connectorBaseUrl = new URI(String.format("http://%s/", hostname.get()));

            var registerResourceMessage = new RegisterResourceMessage(
                    brokerInfrastructureUrl,
                    connectorBaseUrl,
                    resourceUriId,
                    asset);
            dispatcherRegistry.send(Object.class, registerResourceMessage,
                    () -> CONTEXT_BROKER_REGISTRATION);
        } catch (MalformedURLException e) {
            throw new EdcException("Could not build brokerInfrastructureUrl", e);
        } catch (URISyntaxException e) {
            throw new EdcException("Could not create connectorBaseUrl. Hostname can be set using:" +
                    " edc.hostname", e);
        }
    }

    @Override
    public void unregisterResourceAtBroker(URL brokerBaseUrl, String resourceId) {
        try {
            var resourceUriId = new URI(resourceId);
            var brokerInfrastructureUrl = new URL(String.format("%s/infrastructure",
                    brokerBaseUrl));
            var connectorBaseUrl = new URI(String.format("http://%s/", hostname.get()));
            var unregisterResourceMessage = new UnregisterResourceMessage(
                    brokerInfrastructureUrl,
                    connectorBaseUrl,
                    resourceUriId);
            dispatcherRegistry.send(Object.class, unregisterResourceMessage,
                    () -> CONTEXT_BROKER_REGISTRATION);
        } catch (MalformedURLException e) {
            throw new EdcException("Could not build brokerInfrastructureUrl", e);
        } catch (URISyntaxException e) {
            throw new EdcException("Could not create connectorBaseUrl. Hostname can be set using:" +
                    " edc.hostname", e);
        }
    }

    @Override
    public List<String> findResourceIdsByQuery(URL brokerBaseUrl, String fullTextQueryString) {
        try {
            var brokerInfrastructureUrl = new URL(String.format("%s/infrastructure",
                    brokerBaseUrl));
            var connectorBaseUrl = new URI(String.format("http://%s/", hostname.get()));
            var queryMessage = new QueryMessage(
                    brokerInfrastructureUrl,
                    connectorBaseUrl,
                    fullTextQueryString);
            var resultMessageCompletableFuture = dispatcherRegistry.send(
                    String.class,
                    queryMessage,
                    () -> CONTEXT_BROKER_REGISTRATION);
            return parseQueryResponse(resultMessageCompletableFuture.get());
        } catch (MalformedURLException e) {
            throw new EdcException("Could not build brokerInfrastructureUrl", e);
        } catch (URISyntaxException e) {
            throw new EdcException("Could not create connectorBaseUrl. Hostname can be set using:" +
                    " edc.hostname", e);
        } catch (ExecutionException | InterruptedException e) {
            throw new EdcException("Failed to receive result message", e);
        }
    }

    public List<String> parseQueryResponse(String content) {
        var result = new ArrayList<String>();
        var scanner = new Scanner(content);
        var firstLine = true;
        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();
            if (firstLine) {
                firstLine = false;
            } else {
                line = line.replace("<", "");
                line = line.replace(">", "");
                result.add(line);
            }
        }
        scanner.close();

        return result;
    }
}
