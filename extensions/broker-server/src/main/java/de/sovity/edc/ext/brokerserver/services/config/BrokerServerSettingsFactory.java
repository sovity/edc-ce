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

package de.sovity.edc.ext.brokerserver.services.config;

import de.sovity.edc.ext.brokerserver.BrokerServerExtension;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.configuration.Config;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class BrokerServerSettingsFactory {
    private final Config config;
    private final Monitor monitor;

    public BrokerServerSettings buildBrokerServerSettings() {
        var hideOfflineDataOffersAfter = getDuration(BrokerServerExtension.HIDE_OFFLINE_DATA_OFFERS_AFTER, null);
        var catalogPagePageSize = config.getInteger(BrokerServerExtension.CATALOG_PAGE_PAGE_SIZE, 20);
        var dataSpaceConfig = buildDataSpaceConfig(config);
        var numThreads = config.getInteger(BrokerServerExtension.NUM_THREADS, 1);
        var killOfflineConnectorsAfter = getDuration(BrokerServerExtension.KILL_OFFLINE_CONNECTORS_AFTER, Duration.ofDays(5));

        return BrokerServerSettings.builder()
                .hideOfflineDataOffersAfter(hideOfflineDataOffersAfter)
                .catalogPagePageSize(catalogPagePageSize)
                .dataSpaceConfig(dataSpaceConfig)
                .numThreads(numThreads)
                .killOfflineConnectorsAfter(killOfflineConnectorsAfter)
                .build();
    }

    private DataSpaceConfig buildDataSpaceConfig(Config config) {
        var dataSpaceConfig = new DataSpaceConfig(getKnownDataSpaceEndpoints(config), getDefaultDataSpace(config));
        monitor.info("Default Dataspace Name: %s".formatted(dataSpaceConfig.defaultDataSpace()));
        dataSpaceConfig.dataSpaceConnectors().forEach(dataSpaceConnector -> monitor.info("Using Dataspace Name %s for %s."
                .formatted(dataSpaceConnector.dataSpaceName(), dataSpaceConnector.endpoint())));
        if (dataSpaceConfig.dataSpaceConnectors().isEmpty()) {
            monitor.info("No additional data space names configured.");
        }
        return dataSpaceConfig;
    }

    private List<DataSpaceConnector> getKnownDataSpaceEndpoints(Config config) {
        // Example: "Example1=http://connector-endpoint1.org,Example2=http://connector-endpoint2.org"
        var dataSpacesConfig = config.getString(BrokerServerExtension.KNOWN_DATASPACE_CONNECTORS, "");

        return Arrays.stream(dataSpacesConfig.split(","))
                .map(String::trim)
                .map(it -> it.split("="))
                .filter(it -> it.length == 2)
                .map(it -> {
                    var dataSpaceName = it[0].trim();
                    var dataSpaceEndpoint = it[1].trim();
                    return new DataSpaceConnector(dataSpaceEndpoint, dataSpaceName);
                })
                .filter(it -> StringUtils.isNotBlank(it.endpoint()) && StringUtils.isNotBlank(it.endpoint()))
                .toList();
    }

    private String getDefaultDataSpace(Config config) {
        return config.getString(BrokerServerExtension.DEFAULT_CONNECTOR_DATASPACE, "Default");
    }

    private Duration getDuration(@NonNull String configProperty, Duration defaultValue) {
        var value = config.getString(configProperty, "");

        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }

        return Duration.parse(value);
    }
}
