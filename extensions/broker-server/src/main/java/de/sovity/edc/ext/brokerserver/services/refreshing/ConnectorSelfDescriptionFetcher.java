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

package de.sovity.edc.ext.brokerserver.services.refreshing;

import de.sovity.edc.ext.brokerserver.services.refreshing.sender.message.DescriptionRequestMessage;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Fetch Connector Metadata.
 */
@RequiredArgsConstructor
public class ConnectorSelfDescriptionFetcher {
    private static final String CONTEXT_SD_REQUEST = "SelfDescriptionRequest";

    private final RemoteMessageDispatcherRegistry dispatcherRegistry;

    public ConnectorSelfDescription fetch(String connectorEndpoint) {
        try {
            var connectorEndpointUrl = new URL(connectorEndpoint);
            var descriptionRequestMessage = new DescriptionRequestMessage(connectorEndpointUrl);
            var descriptionResponse = dispatcherRegistry.send(String.class, descriptionRequestMessage, () -> CONTEXT_SD_REQUEST).get();

            // TODO parse self-description
            return new ConnectorSelfDescription("TODO", "TODO", descriptionResponse);
        } catch (MalformedURLException e) {
            throw new EdcException("Invalid connector-endpoint URL", e);
        } catch (Exception e) {
            throw new EdcException("Failed to fetch connector self-description", e);
        }
    }
}
