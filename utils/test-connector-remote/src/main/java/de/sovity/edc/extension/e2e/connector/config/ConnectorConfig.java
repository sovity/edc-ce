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
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;


@Getter
@RequiredArgsConstructor
public class ConnectorConfig {
    private final String participantId;
    private final EdcApiGroupConfig defaultEndpoint;
    private final EdcApiGroupConfig managementEndpoint;
    private final EdcApiGroupConfig protocolEndpoint;
    private final Map<String, String> properties;

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public void setProperties(Map<String, String> properties) {
        this.properties.putAll(properties);
    }
}
