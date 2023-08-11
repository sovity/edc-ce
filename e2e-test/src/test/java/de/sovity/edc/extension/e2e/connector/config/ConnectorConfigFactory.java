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

import de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroup;
import de.sovity.edc.extension.e2e.connector.config.api.auth.NoneAuthProvider;
import de.sovity.edc.extension.e2e.connector.config.part.DatasourceConfigPart;
import de.sovity.edc.extension.e2e.connector.config.part.EdcApiGroupConfigPart;
import de.sovity.edc.extension.e2e.db.TestDatabase;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static de.sovity.edc.extension.e2e.connector.config.EdcConfig.PROPERTY_PARTICIPANT;
import static de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroup.Control;
import static de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroup.Default;
import static de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroup.Management;
import static de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroup.Protocol;

public class ConnectorConfigFactory {

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
    private static final int DEFAULT_API_START_PORT = 11000;
    private static final int PROTOCOL_API_START_PORT = 12000;
    private static final int MANAGEMENT_API_START_PORT = 13000;
    private static final int CONTROL_API_START_PORT = 14000;
    private static int buildCounter;

    private ConnectorConfigFactory() {
    }

    public static EdcConfig forTestDatabase(String participantId, TestDatabase testDatabase) {
        var apiGroupConfigMap = createApiGroupConfigMap();
        var dspCallbackAddress = apiGroupConfigMap.get(Protocol).getUri();
        var datasourceConfigs = getDatasourceConfigs(testDatabase);
        var migrationLocation = "classpath:migration/" + participantId;
        var edcConfig = EdcConfig.builder()
                .apiGroupConfigMap(apiGroupConfigMap)
                .datasourceConfigParts(datasourceConfigs)
                .configProperty(PROPERTY_PARTICIPANT, participantId)
                .configProperty("edc.api.auth.key", UUID.randomUUID().toString())
                .configProperty("edc.last.commit.info", "test env commit message")
                .configProperty("edc.build.date", "2023-05-08T15:30:00Z")
                .configProperty("edc.jsonld.https.enabled", "true")
                .configProperty("edc.dsp.callback.address", dspCallbackAddress.toString())
                .configProperty("edc.flyway.additional.migration.locations", migrationLocation)
                .build();
        buildCounter++;
        return edcConfig;
    }

    private static Map<EdcApiGroup, EdcApiGroupConfigPart> createApiGroupConfigMap() {
        return Map.of(
                Default, createApiGroupConfig(Default, DEFAULT_API_GROUP_PATH),
                Protocol, createApiGroupConfig(Protocol, PROTOCOL_API_GROUP_PATH),
                Management, createApiGroupConfig(Management, MANAGEMENT_API_GROUP_PATH),
                Control, createApiGroupConfig(Control, CONTROL_API_GROUP_PATH));
    }

    private static EdcApiGroupConfigPart createApiGroupConfig(
            EdcApiGroup edcApiGroup,
            String path) {
        return new EdcApiGroupConfigPart(
                edcApiGroup,
                BASE_URL,
                getPortForApiGroup(edcApiGroup),
                path,
                new NoneAuthProvider());
    }

    private static int getPortForApiGroup(EdcApiGroup edcApiGroup) {
        return switch (edcApiGroup) {
            case Default -> DEFAULT_API_START_PORT + buildCounter;
            case Protocol -> PROTOCOL_API_START_PORT + buildCounter;
            case Management -> MANAGEMENT_API_START_PORT + buildCounter;
            case Control -> CONTROL_API_START_PORT + buildCounter;
        };
    }

    private static List<DatasourceConfigPart> getDatasourceConfigs(TestDatabase testDatabase) {
        return DATASOURCE_NAMES.stream()
                .map(name -> new DatasourceConfigPart(
                        name,
                        testDatabase.getJdbcUrl(),
                        testDatabase.getJdbcUser(),
                        testDatabase.getJdbcPassword()))
                .toList();
    }
}
