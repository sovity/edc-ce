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
import de.sovity.edc.ext.brokerserver.dao.models.ConnectorRecord;
import de.sovity.edc.ext.brokerserver.dao.stores.ConnectorStore;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;

import java.time.OffsetDateTime;
import java.util.Arrays;

@RequiredArgsConstructor
public class BrokerServerInitializer {
    private final ConnectorStore connectorStore;
    private final Config config;

    public void initializeConnectorList() {
        var knownConnectors = config.getString(BrokerServerExtension.KNOWN_CONNECTORS).split(",");

        Arrays.stream(knownConnectors).forEach(connectorId -> {
            connectorId = connectorId.trim();

            var connectorRecord = ConnectorRecord.builder()
                    .id(connectorId)
                    .idsId(connectorId)
                    .endpoint(connectorId)
                    .createdAt(OffsetDateTime.now())
                    .build();

            connectorStore.save(connectorRecord);
        });
    }
}
