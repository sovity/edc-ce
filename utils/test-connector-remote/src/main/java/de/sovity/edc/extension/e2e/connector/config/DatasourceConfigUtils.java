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

import de.sovity.edc.extension.e2e.db.JdbcCredentials;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasourceConfigUtils {
    private static final List<String> DATASOURCE_NAMES = List.of(
            "default",
            "asset",
            "contractdefinition",
            "contractnegotiation",
            "policy",
            "transferprocess",
            "dataplaneinstance"
    );

    public static Map<String, String> configureDatasources(JdbcCredentials credentials) {
        var properties = new HashMap<String, String>();
        properties.put("edc.flyway.clean.enable", "true");
        properties.put("edc.flyway.clean", "true");
        DATASOURCE_NAMES.forEach(name -> {
            properties.put("edc.datasource.%s.name".formatted(name), name);
            properties.put("edc.datasource.%s.url".formatted(name), credentials.jdbcUrl());
            properties.put("edc.datasource.%s.user".formatted(name), credentials.jdbcUser());
            properties.put("edc.datasource.%s.password".formatted(name), credentials.jdbcPassword());
        });
        return properties;
    }

}
