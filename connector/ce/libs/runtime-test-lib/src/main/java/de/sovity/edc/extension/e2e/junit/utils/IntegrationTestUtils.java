/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.utils;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.extension.e2e.connector.remotes.management_api.ManagementApiConnectorRemote;
import de.sovity.edc.extension.e2e.connector.remotes.management_api.ManagementApiConnectorRemoteConfig;
import de.sovity.edc.runtime.config.ConfigUtils;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class IntegrationTestUtils {

    public static EdcClient getEdcClient(ConfigUtils configUtils) {
        return EdcClient.builder()
            .managementApiUrl(configUtils.getManagementApiUrl())
            .managementApiKey(configUtils.getManagementApiKey())
            .customConfigurer(it -> it
                .setConnectTimeout(0)
                .setReadTimeout(0)
                .setWriteTimeout(0)
            )
            .build();
    }

    public static ManagementApiConnectorRemote getManagementApiConnectorRemote(ConfigUtils configUtils) {
        var connectorRemoteConfig = ManagementApiConnectorRemoteConfig.forConnector(configUtils);
        return new ManagementApiConnectorRemote(connectorRemoteConfig);
    }
}
