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
 *      sovity GmbH - init
 */

package de.sovity.edc.extension.e2e.connector.config;

import de.sovity.edc.extension.e2e.db.TestDatabase;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.UUID;

import static de.sovity.edc.extension.e2e.connector.config.DatasourceConfigUtils.configureDatasources;
import static de.sovity.edc.extension.e2e.connector.config.api.EdcApiConfigFactory.configureApi;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectorConfigFactory {

    public static ConnectorConfig forTestDatabase(String participantId, int firstPort, TestDatabase testDatabase) {
        var config = basicEdcConfig(participantId, firstPort);
        config.setProperties(configureDatasources(testDatabase.getJdbcCredentials()));
        return config;
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

        return new ConnectorConfig(
                participantId,
                apiConfig.getDefaultApiGroup(),
                apiConfig.getManagementApiGroup(),
                apiConfig.getProtocolApiGroup(),
                properties
        );
    }
}
