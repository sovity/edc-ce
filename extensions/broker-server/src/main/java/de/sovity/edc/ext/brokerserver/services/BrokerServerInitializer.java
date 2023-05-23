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
import de.sovity.edc.ext.brokerserver.db.DslContextFactory;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.ConnectorRecord;
import de.sovity.edc.ext.brokerserver.services.refreshing.ConnectorUpdater;
import de.sovity.edc.ext.brokerserver.utils.UrlUtils;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
public class BrokerServerInitializer {
    private final DslContextFactory dslContextFactory;
    private final Config config;

    private final ConnectorUpdater connectorUpdater;

    public void onStartup() {
        List<String> connectorEndpoints = getPreconfiguredConnectorEndpoints();
        dslContextFactory.transaction(dsl -> initializeConnectorList(dsl, connectorEndpoints));

        // TODO fill queue rather than execute in loop
        connectorEndpoints.forEach(connectorUpdater::updateConnector);
    }

    private void initializeConnectorList(DSLContext dsl, List<String> connectorEndpoints) {
        var connectorRecords = connectorEndpoints.stream()
                .map(String::trim)
                .map(this::newConnectorRow)
                .toList();
        dsl.batchStore(connectorRecords).execute();
    }

    @NotNull
    private ConnectorRecord newConnectorRow(String endpoint) {
        var connectorId = UrlUtils.getEverythingBeforeThePath(endpoint);

        var connector = new ConnectorRecord();
        connector.setEndpoint(endpoint);
        connector.setConnectorId(connectorId);
        connector.setTitle("Unknown Connector");
        connector.setDescription("Awaiting initial crawling of given connector.");
        connector.setIdsId("");
        connector.setCreatedAt(OffsetDateTime.now());
        connector.setOnlineStatus(ConnectorOnlineStatus.OFFLINE);
        return connector;
    }

    private List<String> getPreconfiguredConnectorEndpoints() {
        return List.of(config.getString(BrokerServerExtension.KNOWN_CONNECTORS).split(","));
    }
}
