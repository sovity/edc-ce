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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import static de.sovity.edc.extension.e2e.connector.config.DatasourceConfigUtils.configureDatasources;
import static de.sovity.edc.extension.e2e.connector.config.api.EdcApiConfigFactory.configureApi;
import static org.eclipse.edc.junit.testfixtures.TestUtils.MAX_TCP_PORT;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectorConfigFactory {

    private static final Random RANDOM = new Random();

    public static ConnectorConfig forTestDatabase(String participantId, TestDatabase testDatabase) {
        val firstPort = getFreePortRange(5);
        var config = basicEdcConfig(participantId, firstPort);
        config.setProperties(configureDatasources(testDatabase.getJdbcCredentials()));
        return config;
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

    public static ConnectorConfig basicEdcConfig(String participantId, int firstPort) {
        var apiKey = UUID.randomUUID().toString();
        var apiConfig = configureApi(firstPort, apiKey);

        var properties = new HashMap<String, String>();
        properties.put("edc.participant.id", participantId);
        properties.put("edc.api.auth.key", apiKey);
        properties.put("edc.dsp.callback.address", apiConfig.getProtocolApiGroup().getUri().toString());
        properties.putAll(apiConfig.getProperties());

        properties.put("edc.jsonld.https.enabled", "true");
        properties.put("edc.last.commit.info", "test env commit message");
        properties.put("edc.build.date", "2023-05-08T15:30:00Z");

        properties.put("my.edc.participant.id", participantId);
        properties.put("my.edc.title", "Connector Title %s".formatted(participantId));
        properties.put("my.edc.description", "Connector Description %s".formatted(participantId));
        properties.put("my.edc.curator.url", "http://curator.%s".formatted(participantId));
        properties.put("my.edc.curator.name", "Curator Name %s".formatted(participantId));
        properties.put("my.edc.maintainer.url", "http://maintainer.%s".formatted(participantId));
        properties.put("my.edc.maintainer.name", "Maintainer Name %s".formatted(participantId));

        properties.put("my.edc.datasource.placeholder.baseurl", apiConfig.getProtocolApiGroup().getUri().toString());

        properties.put("web.http.port", String.valueOf(apiConfig.getDefaultApiGroup().port()));
        properties.put("web.http.path", String.valueOf(apiConfig.getDefaultApiGroup().path()));
        properties.put("web.http.protocol.port", String.valueOf(apiConfig.getProtocolApiGroup().port()));
        properties.put("web.http.protocol.path", String.valueOf(apiConfig.getProtocolApiGroup().path()));
        properties.put("web.http.management.port", String.valueOf(apiConfig.getManagementApiGroup().port()));
        properties.put("web.http.management.path", String.valueOf(apiConfig.getManagementApiGroup().path()));
        properties.put("web.http.control.port", String.valueOf(apiConfig.getControlApiGroup().port()));
        properties.put("web.http.control.path", String.valueOf(apiConfig.getControlApiGroup().path()));

        return new ConnectorConfig(
            participantId,
            apiConfig.getDefaultApiGroup(),
            apiConfig.getManagementApiGroup(),
            apiConfig.getProtocolApiGroup(),
            properties
        );
    }
}
