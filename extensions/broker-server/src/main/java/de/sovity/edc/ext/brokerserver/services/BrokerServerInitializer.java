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
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;

@RequiredArgsConstructor
public class BrokerServerInitializer {
    private final DslContextFactory dslContextFactory;
    private final Config config;

    public void onStartup() {
        dslContextFactory.transaction(this::initializeConnectorList);
    }

    private void initializeConnectorList(DSLContext dsl) {
        var endpoints = config.getString(BrokerServerExtension.KNOWN_CONNECTORS).split(",");
        var connectorRecords = Arrays.stream(endpoints)
                .map(String::trim)
                .map(this::newConnectorRow)
                .toList();
        dsl.batchStore(connectorRecords).execute();
    }

    @NotNull
    private ConnectorRecord newConnectorRow(String endpoint) {
        var connectorId = getEverythingBeforeThePath(endpoint);

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

    /**
     * Returns everything before the URLs path.
     * <p>
     * Example: http://www.example.com/path/to/my/file.html -> http://www.example.com
     * Example 2: http://www.example.com:9000/path/to/my/file.html -> http://www.example.com:9000
     *
     * @param url url
     * @return protocol, host, port
     */
    private String getEverythingBeforeThePath(String url) {
        var uri = URI.create(url);
        String scheme = uri.getScheme(); // "http"
        String authority = uri.getAuthority(); // "www.example.com"
        int port = uri.getPort(); // -1 (no port specified)
        String everythingBeforePath = scheme + "://" + authority;
        if (port != -1) {
            everythingBeforePath += ":" + port;
        }
        return everythingBeforePath;
    }
}
