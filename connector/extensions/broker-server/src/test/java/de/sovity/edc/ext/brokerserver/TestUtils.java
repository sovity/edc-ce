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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;

public class TestUtils {
    private static final int MANAGEMENT_PORT = getFreePort();
    private static final int PROTOCOL_PORT = getFreePort();
    private static final String MANAGEMENT_PATH = "/api/management";
    private static final String PROTOCOL_PATH = "/api/dsp";
    public static final String MANAGEMENT_API_KEY = "123456";
    public static final String MANAGEMENT_ENDPOINT = "http://localhost:" + MANAGEMENT_PORT + MANAGEMENT_PATH;
    public static final String ADMIN_API_KEY = "123456";


    public static final String PROTOCOL_HOST = "http://localhost:" + PROTOCOL_PORT;
    public static final String PROTOCOL_ENDPOINT = PROTOCOL_HOST + PROTOCOL_PATH;
    public static final String PARTICIPANT_ID = "MDSL1234ZZ.C4321AA";
    public static final String CURATOR_NAME = "My Org";

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
        config.put("edc.oauth.provider.audience", "idsc:IDS_CONNECTORS_ALL");

        config.put("edc.participant.id", PARTICIPANT_ID);
        config.put("my.edc.participant.id", PARTICIPANT_ID);
        config.put("my.edc.title", "My Connector");
        config.put("my.edc.description", "My Connector Description");
        config.put("my.edc.curator.url", "https://connector.my-org");
        config.put("my.edc.curator.name", CURATOR_NAME);
        config.put("my.edc.maintainer.url", "https://maintainer-org");
        config.put("my.edc.maintainer.name", "Maintainer Org");

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
