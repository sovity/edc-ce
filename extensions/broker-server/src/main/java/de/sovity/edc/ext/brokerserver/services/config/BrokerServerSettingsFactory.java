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
import org.apache.commons.lang3.StringUtils;
import org.eclipse.edc.spi.system.configuration.Config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BrokerServerSettingsFactory {
    private Config config;

    public BrokerServerSettings buildBrokerServerSettings(Config config) {
        this.config = config;

        var hideOfflineDataOffersAfter = getDurationOrNull(BrokerServerExtension.HIDE_OFFLINE_DATA_OFFERS_AFTER);
        var catalogPagePageSize = config.getInteger(BrokerServerExtension.CATALOG_PAGE_PAGE_SIZE, 20);
        var dataSpaceConfig = buildDataSpaceConfig(config);
        var numThreads = config.getInteger(BrokerServerExtension.NUM_THREADS, 1);

        return BrokerServerSettings.builder()
                .hideOfflineDataOffersAfter(hideOfflineDataOffersAfter)
                .catalogPagePageSize(catalogPagePageSize)
                .dataSpaceConfig(dataSpaceConfig)
                .numThreads(numThreads)
                .build();
    }

    private DataSpaceConfig buildDataSpaceConfig(Config config) {
        return new DataSpaceConfig(getKnownDataSpaceEndpoints(config), getDefaultDataSpace(config));
    }

    private List<DataSpaceConnector> getKnownDataSpaceEndpoints(Config config) {
        // Example: "Example1=http://connector-endpoint1.org;Example2=http://connector-endpoint2.org"
        var defaultDataSpaces = new ArrayList<DataSpaceConnector>();
        var dataSpacesConfig = config.getString(BrokerServerExtension.KNOWN_DATASPACES_ENDPOINTS, "");

        var allDataSpaces = dataSpacesConfig.split(";");
        for (var dataSpace : allDataSpaces) {
            var dataSpaceParts = dataSpace.split("=");
            if (dataSpaceParts.length != 2) {
                continue;
            }

            var dataSpaceName = dataSpaceParts[0].trim();
            var dataSpaceEndpoint = dataSpaceParts[1].trim();
            if (StringUtils.isBlank(dataSpaceName) || StringUtils.isBlank(dataSpaceEndpoint)) {
                continue;
            }

            defaultDataSpaces.add(new DataSpaceConnector(dataSpaceEndpoint, dataSpaceName));
        }

        return defaultDataSpaces;
    }

    private String getDefaultDataSpace(Config config) {
        return config.getString(BrokerServerExtension.DEFAULT_CONNECTOR_DATASPACE, "Default");
    }

    private Duration getDurationOrNull(@NonNull String configProperty) {
        var durationAsString = config.getString(configProperty, "");
        if (StringUtils.isBlank(durationAsString)) {
            return null;
        }

        return Duration.parse(durationAsString);
    }
}
