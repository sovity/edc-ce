/*
 * Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.extension.e2e.connector.config;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class WellKnowProperties {
    private final Map<String, String> properties;

    public String getDatabaseJdbcUrl() {
        return properties.get("edc.datasource.default.url");
    }

    public String getDatabaseUser() {
        return properties.get("edc.datasource.default.user");
    }

    public String getDatabasePassword() {
        return properties.get("edc.datasource.default.password");
    }
}
