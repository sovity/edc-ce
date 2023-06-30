/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.client;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;

public class TestUtils {
    private static final int DATA_PORT = getFreePort();
    private static final int PROTOCOL_PORT = getFreePort();
    private static final String DATA_PATH = "/api/v1/data";
    private static final String PROTOCOL_PATH = "/api/v1/ids";
    public static final String MANAGEMENT_API_KEY = "123456";
    public static final String MANAGEMENT_ENDPOINT = "http://localhost:" + DATA_PORT + DATA_PATH;


    public static final String PROTOCOL_HOST = "http://localhost:" + PROTOCOL_PORT;
    public static final String PROTOCOL_ENDPOINT = PROTOCOL_HOST + PROTOCOL_PATH + "/data";

    @NotNull
    public static Map<String, String> createConfiguration(
            Map<String, String> additionalConfigProperties
    ) {
        Map<String, String> config = new HashMap<>();
        config.put("web.http.port", String.valueOf(getFreePort()));
        config.put("web.http.path", "/api");
        config.put("web.http.management.port", String.valueOf(DATA_PORT));
        config.put("web.http.management.path", DATA_PATH);
        config.put("web.http.protocol.port", String.valueOf(PROTOCOL_PORT));
        config.put("web.http.protocol.path", PROTOCOL_PATH);
        config.put("edc.api.auth.key", MANAGEMENT_API_KEY);
        config.put("edc.ids.endpoint", PROTOCOL_ENDPOINT);
        config.put("edc.oauth.provider.audience", "idsc:IDS_CONNECTORS_ALL");
        config.putAll(additionalConfigProperties);
        return config;
    }

    public static EdcClient edcClient() {
        return EdcClient.builder()
                .managementApiUrl(TestUtils.MANAGEMENT_ENDPOINT)
                .managementApiKey(TestUtils.MANAGEMENT_API_KEY)
                .build();
    }
}
