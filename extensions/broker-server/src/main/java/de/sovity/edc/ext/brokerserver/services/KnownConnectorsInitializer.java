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

package de.sovity.edc.ext.brokerserver.services;

import de.sovity.edc.ext.brokerserver.BrokerServerExtension;
import de.sovity.edc.ext.brokerserver.services.queue.ConnectorQueue;
import de.sovity.edc.ext.brokerserver.services.queue.ConnectorRefreshPriority;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.edc.spi.system.configuration.Config;
import org.jooq.DSLContext;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class KnownConnectorsInitializer {
    private final Config config;
    private final ConnectorQueue connectorQueue;
    private final ConnectorCreator connectorCreator;

    public void addKnownConnectorsOnStartup(DSLContext dsl) {
        var connectorEndpoints = getKnownConnectorsConfigValue();
        connectorCreator.addConnectors(dsl, connectorEndpoints);
        connectorQueue.addAll(connectorEndpoints, ConnectorRefreshPriority.ADDED_ON_STARTUP);
    }

    private List<String> getKnownConnectorsConfigValue() {
        var knownConnectorsString = config.getString(BrokerServerExtension.KNOWN_CONNECTORS, "");
        return Arrays.stream(knownConnectorsString.split(",")).map(String::trim).filter(StringUtils::isNotBlank).distinct().toList();
    }
}
