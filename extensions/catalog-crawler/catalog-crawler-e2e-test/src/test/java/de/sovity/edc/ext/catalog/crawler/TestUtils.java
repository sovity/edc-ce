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

package de.sovity.edc.ext.catalog.crawler;

import de.sovity.edc.ext.catalog.crawler.config.TestDatabase;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;

public class TestUtils {
    private static final int MANAGEMENT_PORT = getFreePort();
    private static final int PROTOCOL_PORT = getFreePort();
    private static final String MANAGEMENT_PATH = "/api/management";
    private static final String PROTOCOL_PATH = "/api/dsp";
    public static final String MANAGEMENT_API_KEY = "123456";
    public static final String MANAGEMENT_ENDPOINT = "http://localhost:" + MANAGEMENT_PORT + MANAGEMENT_PATH;


    public static final String PROTOCOL_HOST = "http://localhost:" + PROTOCOL_PORT;
    public static final String PROTOCOL_ENDPOINT = PROTOCOL_HOST + PROTOCOL_PATH;
    public static final String PARTICIPANT_ID = "MDSL1234ZZ.C4321AA";
    public static final String CURATOR_NAME = "My Org";

    public static final ConnectorRef CONNECTOR_REF = new ConnectorRef(
            PARTICIPANT_ID,
            "test",
            CURATOR_NAME,
            PARTICIPANT_ID.split("\\.")[0],
            PROTOCOL_ENDPOINT
    );

    @NotNull
    public static Map<String, String> createConfiguration(
            TestDatabase testDatabase,
            Map<String, String> additionalConfigProperties
    ) {
        Map<String, String> config = new HashMap<>();

        config.put("web.http.port", String.valueOf(getFreePort()));
        config.put("web.http.path", "/api");
        config.put("web.http.management.port", String.valueOf(MANAGEMENT_PORT));
        config.put("web.http.management.path", MANAGEMENT_PATH);
        config.put("web.http.protocol.port", String.valueOf(PROTOCOL_PORT));
        config.put("web.http.protocol.path", PROTOCOL_PATH);
        config.put("edc.api.auth.key", MANAGEMENT_API_KEY);
        config.put("edc.dsp.callback.address", PROTOCOL_ENDPOINT);

        config.put("edc.participant.id", PARTICIPANT_ID);

        config.put(CrawlerExtension.JDBC_URL, testDatabase.getJdbcUrl());
        config.put(CrawlerExtension.JDBC_USER, testDatabase.getJdbcUser());
        config.put(CrawlerExtension.JDBC_PASSWORD, testDatabase.getJdbcPassword());
        config.put(CrawlerExtension.DB_CONNECTION_POOL_SIZE, "20");
        config.put(CrawlerExtension.DB_CONNECTION_TIMEOUT_IN_MS, "3000");
        config.put(CrawlerExtension.NUM_THREADS, "0");
        config.putAll(additionalConfigProperties);
        return config;
    }
}
