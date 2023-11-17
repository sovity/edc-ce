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

package de.sovity.edc.ext.brokerserver.services.api;

import de.sovity.edc.ext.brokerserver.services.logging.BrokerEventLogger;
import de.sovity.edc.ext.brokerserver.utils.UrlUtils;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Objects;

import static de.sovity.edc.ext.brokerserver.services.queue.ConnectorRefreshPriority.ADDED_ON_API_CALL;
import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class ConnectorApiService {
    private final ConnectorService connectorService;
    private final BrokerEventLogger brokerEventLogger;

    public void addConnectors(DSLContext dsl, List<String> connectorEndpoints) {
        var existingEndpoints = connectorService.getConnectorEndpoints(dsl);
        var endpoints = connectorEndpoints.stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(UrlUtils::isValidUrl)
            .filter(endpoint -> !existingEndpoints.contains(endpoint))
            .collect(toSet());
        connectorService.addConnectors(dsl, endpoints, ADDED_ON_API_CALL);
    }

    public void deleteConnectors(DSLContext dsl, List<String> connectorEndpoints) {
        connectorService.deleteConnectors(dsl, connectorEndpoints);
        brokerEventLogger.logConnectorsDeleted(dsl, connectorEndpoints);
    }
}
