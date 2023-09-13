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

import de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroupConfig;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;


@Data
@AllArgsConstructor
public class ConnectorConfig {
    private String participantId;
    private EdcApiGroupConfig defaultEndpoint;
    private EdcApiGroupConfig managementEndpoint;
    private EdcApiGroupConfig protocolEndpoint;
    private Map<String, String> properties;

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public void setProperties(Map<String, String> properties) {
        this.properties.putAll(properties);
    }
}
