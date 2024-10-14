/*
 * Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.extension.e2e.junit;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.extension.e2e.connector.config.ConnectorBootConfig;
import de.sovity.edc.extension.e2e.connector.config.ConnectorBootConfig.ConnectorBootConfigBuilder;
import de.sovity.edc.extension.e2e.connector.config.PortUtils;
import de.sovity.edc.extension.e2e.connector.remotes.management_api.ManagementApiConnectorRemote;
import de.sovity.edc.extension.e2e.connector.remotes.management_api.ManagementApiConnectorRemoteConfig;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.utils.config.ConfigProps;
import de.sovity.edc.utils.config.ConfigUtils;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.edc.spi.system.configuration.Config;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

@UtilityClass
public final class CeIntegrationTestUtils {

    public static ConnectorBootConfig defaultConfig(
        String participantId,
        @Nullable
        TestDatabase testDatabase,
        @Nullable
        Consumer<ConnectorBootConfigBuilder> overrides
    ) {
        val firstPort = PortUtils.getFreePortRange(5);
        val apiKey = "api-key-%s".formatted(UUID.randomUUID().toString());

        val configBuilder = ConnectorBootConfig.builder()
            // Network
            .property(ConfigProps.MY_EDC_NETWORK_TYPE, ConfigProps.NetworkType.UNIT_TEST)
            .property(ConfigProps.MY_EDC_FIRST_PORT, String.valueOf(firstPort))

            // API Auth
            .property(ConfigProps.EDC_API_AUTH_KEY, apiKey)

            // C2C Auth
            .property(ConfigProps.MY_EDC_C2C_IAM_TYPE, ConfigProps.C2cIamType.MOCK_IAM)
            .property(ConfigProps.MY_EDC_PARTICIPANT_ID, participantId)

            // DB
            .property(ConfigProps.MY_EDC_JDBC_URL, testDatabase.getJdbcCredentials().jdbcUrl())
            .property(ConfigProps.MY_EDC_JDBC_USER, testDatabase.getJdbcCredentials().jdbcUser())
            .property(ConfigProps.MY_EDC_JDBC_PASSWORD, testDatabase.getJdbcCredentials().jdbcPassword())
            .property(ConfigProps.EDC_FLYWAY_CLEAN_ENABLE, "true")
            .property(ConfigProps.EDC_FLYWAY_CLEAN, "true")

            // Metadata
            .property(ConfigProps.MY_EDC_TITLE, "Connector Title %s".formatted(participantId))
            .property(ConfigProps.MY_EDC_DESCRIPTION, "Connector Description %s".formatted(participantId))
            .property(ConfigProps.MY_EDC_CURATOR_URL, "http://curator.%s".formatted(participantId))
            .property(ConfigProps.MY_EDC_CURATOR_NAME, "Curator Name %s".formatted(participantId))
            .property(ConfigProps.MY_EDC_MAINTAINER_URL, "http://maintainer.%s".formatted(participantId))
            .property(ConfigProps.MY_EDC_MAINTAINER_NAME, "Maintainer Name %s".formatted(participantId));
        if (overrides != null) {
            overrides.accept(configBuilder);
        }
        return configBuilder.build();
    }

    public static EdcClient getEdcClient(Config config) {
        return EdcClient.builder()
            .managementApiUrl(ConfigUtils.getManagementApiUrl(config.getEntries()))
            .managementApiKey(ConfigUtils.getManagementApiKey(config.getEntries()))
            .customConfigurer(it -> it
                .setConnectTimeout(0)
                .setReadTimeout(0)
                .setWriteTimeout(0)
            )
            .build();
    }

    public static ManagementApiConnectorRemote getManagementApiConnectorRemote(Config config) {
        var connectorRemoteConfig = ManagementApiConnectorRemoteConfig.forConnector(config, () -> Pair.of(
            "X-Api-Key",
            ConfigUtils.getManagementApiKey(config.getEntries())
        ));
        return new ManagementApiConnectorRemote(connectorRemoteConfig);
    }
}
