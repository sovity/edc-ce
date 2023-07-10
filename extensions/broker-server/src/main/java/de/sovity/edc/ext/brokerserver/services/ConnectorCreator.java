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

import de.sovity.edc.ext.brokerserver.dao.ConnectorQueries;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorContractOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorDataOffersExceeded;
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.ConnectorRecord;
import de.sovity.edc.ext.brokerserver.utils.CollectionUtils2;
import de.sovity.edc.ext.brokerserver.utils.UrlUtils;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class ConnectorCreator {
    private final ConnectorQueries connectorQueries;

    public void addConnector(DSLContext dsl, String connectorEndpoint) {
        addConnectors(dsl, List.of(connectorEndpoint));
    }

    public void addConnectors(DSLContext dsl, Collection<String> connectorEndpoints) {
        // Don't create connectors that already exist
        var existingConnectors = connectorQueries.findExistingConnectors(dsl, connectorEndpoints);
        var newConnectors = CollectionUtils2.difference(connectorEndpoints, existingConnectors);

        var connectorRecords = newConnectors.stream()
                .map(String::trim)
                .map(this::newConnectorRow)
                .toList();

        if (!connectorRecords.isEmpty()) {
            dsl.batchStore(connectorRecords).execute();
        }
    }

    @NotNull
    private ConnectorRecord newConnectorRow(String endpoint) {
        var connector = new ConnectorRecord();
        connector.setEndpoint(endpoint);
        connector.setConnectorId(UrlUtils.getEverythingBeforeThePath(endpoint));
        connector.setCreatedAt(OffsetDateTime.now());
        connector.setOnlineStatus(ConnectorOnlineStatus.OFFLINE);
        connector.setDataOffersExceeded(ConnectorDataOffersExceeded.OK);
        connector.setContractOffersExceeded(ConnectorContractOffersExceeded.OK);
        return connector;
    }
}
