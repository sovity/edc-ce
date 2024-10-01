/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.extension.e2e.connector.config;

import de.sovity.edc.extension.e2e.db.TestDatabase;
import de.sovity.edc.utils.config.ConfigProps;
import de.sovity.edc.utils.config.ConfigService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.eclipse.edc.junit.testfixtures.TestUtils.MAX_TCP_PORT;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectorConfigFactory {

    private static final Random RANDOM = new Random();

    public static ConnectorConfig forTestDatabase(String participantId, TestDatabase testDatabase) {
        val firstPort = getFreePortRange(5);

        // The initialization of the Map is split into several statements
        // due to the parameter limit of Map.of(...)
        var propertiesInput = new HashMap<>(Map.of(
            ConfigProps.MY_EDC_NETWORK_TYPE, ConfigProps.NetworkType.UNIT_TEST,
            ConfigProps.MY_EDC_FIRST_PORT, String.valueOf(firstPort),
            ConfigProps.EDC_API_AUTH_KEY, "api-key-%s".formatted(UUID.randomUUID().toString()),
            ConfigProps.MY_EDC_C2C_IAM_TYPE, "mock-iam",
            ConfigProps.MY_EDC_PARTICIPANT_ID, participantId
        ));

        propertiesInput.putAll(Map.of(
            ConfigProps.MY_EDC_JDBC_URL, testDatabase.getJdbcCredentials().jdbcUrl(),
            ConfigProps.MY_EDC_JDBC_USER, testDatabase.getJdbcCredentials().jdbcUser(),
            ConfigProps.MY_EDC_JDBC_PASSWORD, testDatabase.getJdbcCredentials().jdbcPassword(),
            ConfigProps.EDC_FLYWAY_CLEAN_ENABLE, "true",
            ConfigProps.EDC_FLYWAY_CLEAN, "true"
        ));

        propertiesInput.putAll(Map.of(
            ConfigProps.MY_EDC_TITLE, "Connector Title %s".formatted(participantId),
            ConfigProps.MY_EDC_DESCRIPTION, "Connector Description %s".formatted(participantId),
            ConfigProps.MY_EDC_CURATOR_URL, "http://curator.%s".formatted(participantId),
            ConfigProps.MY_EDC_CURATOR_NAME, "Curator Name %s".formatted(participantId),
            ConfigProps.MY_EDC_MAINTAINER_URL, "http://maintainer.%s".formatted(participantId),
            ConfigProps.MY_EDC_MAINTAINER_NAME, "Maintainer Name %s".formatted(participantId)
        ));

        var properties = ConfigService.applyDefaults(propertiesInput, ConfigProps.ALL_CE_PROPS);

        return ConnectorConfig.builder()
            .properties(properties)
            .build();
    }

    public static synchronized int getFreePortRange(int size) {
        // pick a random in a reasonable range
        int firstPort = RANDOM.nextInt(10_000, 50_000);

        int currentPort = firstPort;
        do {
            if (canUsePort(currentPort)) {
                currentPort++;
            } else {
                firstPort = currentPort++;
            }
        } while (currentPort <= firstPort + size);

        return firstPort;
    }

    private static boolean canUsePort(int port) {

        if (port <= 0 || port >= MAX_TCP_PORT) {
            throw new IllegalArgumentException("Lower bound must be > 0 and < " + MAX_TCP_PORT + " and be < upperBound");
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
