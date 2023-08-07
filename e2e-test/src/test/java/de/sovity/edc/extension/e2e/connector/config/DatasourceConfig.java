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

import java.util.Map;

public record DatasourceConfig(
        String name,
        String jdbcUrl,
        String jdbcUser,
        String jdbcPassword) implements EdcConfig {

    private static final String SETTING_DATASOURCE_NAME = "edc.datasource.%s.name";
    private static final String SETTING_DATASOURCE_URL = "edc.datasource.%s.url";
    private static final String SETTING_DATASOURCE_USER = "edc.datasource.%s.user";
    private static final String SETTING_DATASOURCE_PASSWORD = "edc.datasource.%s.password";

    @Override
    public Map<String, String> toMap() {
        return Map.of(
                String.format(SETTING_DATASOURCE_NAME, name), name,
                String.format(SETTING_DATASOURCE_URL, name), jdbcUrl,
                String.format(SETTING_DATASOURCE_USER, name), jdbcUser,
                String.format(SETTING_DATASOURCE_PASSWORD, name), jdbcPassword
        );
    }
}
