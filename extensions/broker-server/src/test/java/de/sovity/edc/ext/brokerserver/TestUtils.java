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

package de.sovity.edc.ext.brokerserver;

import de.sovity.edc.ext.brokerserver.client.BrokerServerClient;
import de.sovity.edc.ext.brokerserver.client.gen.ApiException;
import de.sovity.edc.ext.brokerserver.db.PostgresFlywayExtension;
import de.sovity.edc.ext.brokerserver.db.TestDatabase;
import org.assertj.core.api.ThrowableAssert;
import org.eclipse.edc.protocol.ids.api.configuration.IdsApiConfigurationExtension;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;

public class TestUtils {

    private static final int DATA_PORT = getFreePort();
    private static final int PROTOCOL_PORT = getFreePort();
    private static final String DATA_PATH = "/api/v1/data";
    private static final String PROTOCOL_PATH = "/api/v1/ids";
    public static final String MANAGEMENT_API_KEY = "123456";
    public static final String MANAGEMENT_ENDPOINT = "http://localhost:" + DATA_PORT + DATA_PATH;

    public static final String ADMIN_API_KEY = "123456";


    public static final String PROTOCOL_HOST = "http://localhost:" + PROTOCOL_PORT;
    public static final String PROTOCOL_ENDPOINT = PROTOCOL_HOST + PROTOCOL_PATH + "/data";

    @NotNull
    public static Map<String, String> createConfiguration(
            TestDatabase testDatabase,
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
        config.put(IdsApiConfigurationExtension.IDS_WEBHOOK_ADDRESS, PROTOCOL_HOST);
        config.put("edc.oauth.provider.audience", "idsc:IDS_CONNECTORS_ALL");
        config.put(PostgresFlywayExtension.JDBC_URL, testDatabase.getJdbcUrl());
        config.put(PostgresFlywayExtension.JDBC_USER, testDatabase.getJdbcUser());
        config.put(PostgresFlywayExtension.JDBC_PASSWORD, testDatabase.getJdbcPassword());
        config.put(PostgresFlywayExtension.DB_CONNECTION_POOL_SIZE, "20");
        config.put(PostgresFlywayExtension.DB_CONNECTION_TIMEOUT_IN_MS, "3000");
        config.put(PostgresFlywayExtension.FLYWAY_CLEAN_ENABLE, "true");
        config.put(PostgresFlywayExtension.FLYWAY_CLEAN, "true");
        config.put(BrokerServerExtension.NUM_THREADS, "0");
        config.put(BrokerServerExtension.ADMIN_API_KEY, ADMIN_API_KEY);
        config.putAll(getCoreEdcJdbcConfig(testDatabase));
        config.putAll(additionalConfigProperties);
        return config;
    }

    private static Map<String, String> getCoreEdcJdbcConfig(TestDatabase testDatabase) {
        Map<String, String> config = new HashMap<>();
        List.of("asset",
                "contractdefinition",
                "contractnegotiation",
                "policy",
                "transferprocess",
                "dataplaneinstance"
        ).forEach(it -> {
            config.put("edc.datasource.%s.name".formatted(it), it);
            config.put("edc.datasource.%s.url".formatted(it), testDatabase.getJdbcUrl());
            config.put("edc.datasource.%s.user".formatted(it), testDatabase.getJdbcUser());
            config.put("edc.datasource.%s.password".formatted(it), testDatabase.getJdbcPassword());
        });
        return config;
    }

    public static BrokerServerClient brokerServerClient() {
        return BrokerServerClient.builder()
            .managementApiUrl(TestUtils.MANAGEMENT_ENDPOINT)
            .managementApiKey(TestUtils.MANAGEMENT_API_KEY)
            .build();
    }


    public static void assertIs401(ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable)
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> {
                    var apiException = (ApiException) ex;
                    assertThat(apiException.getCode()).isEqualTo(401);
                });
    }
}
