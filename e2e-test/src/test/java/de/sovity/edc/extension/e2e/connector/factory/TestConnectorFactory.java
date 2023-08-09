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

package de.sovity.edc.extension.e2e.connector.factory;

import de.sovity.edc.extension.e2e.connector.Connector;
import de.sovity.edc.extension.e2e.connector.TestConnector;
import de.sovity.edc.extension.e2e.connector.config.DatasourceConfig;
import de.sovity.edc.extension.e2e.connector.config.EdcApiGroup;
import de.sovity.edc.extension.e2e.connector.config.EdcApiGroupConfig;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.junit.extensions.EdcExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static de.sovity.edc.extension.e2e.connector.config.EdcApiGroup.CONTROL;
import static de.sovity.edc.extension.e2e.connector.config.EdcApiGroup.DEFAULT;
import static de.sovity.edc.extension.e2e.connector.config.EdcApiGroup.MANAGEMENT;
import static de.sovity.edc.extension.e2e.connector.config.EdcApiGroup.PROTOCOL;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;

@RequiredArgsConstructor
public class TestConnectorFactory implements ConnectorFactory {

    private static final String BASE_URL = "http://localhost";
    private static final List<String> DATASOURCE_NAMES = List.of(
            "asset",
            "contractdefinition",
            "contractnegotiation",
            "policy",
            "transferprocess",
            "dataplaneinstance",
            "default"
    );
    private static final String DEFAULT_API_GROUP_PATH = "/api";
    private static final String PROTOCOL_API_GROUP_PATH = "/dsp";
    private static final String MANAGEMENT_API_GROUP_PATH = "/api/management";
    private static final String CONTROL_API_GROUP_PATH = "/control";

    private final String participantId;
    private final EdcExtension edcContext;
    private final TestDatabase testDatabase;

    @Override
    public Connector createConnector() {
        var apiGroupConfigMap = createApiGroupConfigMap();
        var dspCallbackAddress = apiGroupConfigMap.get(PROTOCOL).getUri();
        var datasourceConfigs = getDatasourceConfigs(testDatabase);
        var migrationLocation = "classpath:migration/" + participantId;
        var connector = TestConnector.builder()
                .participantId(participantId)
                .apiGroupConfigMap(apiGroupConfigMap)
                .datasourceConfigs(datasourceConfigs)
                .configProperty("edc.participant.id", participantId)
                .configProperty("edc.api.auth.key", UUID.randomUUID().toString())
                .configProperty("edc.last.commit.info", "test env commit message")
                .configProperty("edc.build.date", "2023-05-08T15:30:00Z")
                .configProperty("edc.jsonld.https.enabled", "true")
                .configProperty("edc.dsp.callback.address", dspCallbackAddress.toString())
                .configProperty("edc.flyway.additional.migration.locations", migrationLocation)
                .build();
        System.out.println(connector.getConfig());
        edcContext.setConfiguration(connector.getConfig());
        return connector;
    }

    private Map<EdcApiGroup, EdcApiGroupConfig> createApiGroupConfigMap() {
        return Map.of(
                DEFAULT, createApiGroupConfig(DEFAULT, DEFAULT_API_GROUP_PATH),
                PROTOCOL, createApiGroupConfig(PROTOCOL, PROTOCOL_API_GROUP_PATH),
                MANAGEMENT, createApiGroupConfig(MANAGEMENT, MANAGEMENT_API_GROUP_PATH),
                CONTROL, createApiGroupConfig(CONTROL, CONTROL_API_GROUP_PATH));
    }

    private EdcApiGroupConfig createApiGroupConfig(EdcApiGroup edcApiGroup, String path) {
        return new EdcApiGroupConfig(edcApiGroup, BASE_URL, getFreePort(), path);
    }

    private List<DatasourceConfig> getDatasourceConfigs(TestDatabase testDatabase) {
        return DATASOURCE_NAMES.stream()
                .map(name -> new DatasourceConfig(
                        name,
                        testDatabase.getJdbcUrl(),
                        testDatabase.getJdbcUser(),
                        testDatabase.getJdbcPassword()))
                .toList();
    }
}
