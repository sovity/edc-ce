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
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.extension.e2e.connector.config;

import de.sovity.edc.utils.config.ConfigProps;
import de.sovity.edc.utils.config.ConfigUtils;
import de.sovity.edc.utils.config.model.ConfigProp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.Header;

import java.util.Map;
import java.util.function.Supplier;


@Data
@Builder
@AllArgsConstructor
public class ConnectorConfig {
    /**
     * Connector Properties after applying defaults from {@link ConfigProps}
     */
    private Map<String, String> properties;

    @Builder.Default
    private Supplier<Pair<String, String>> managementApiAuthHeader = () -> Pair.of("X-Api-Key", getManagementApiKey());

    public ConnectorConfig setProperty(ConfigProp property, String value) {
        properties.put(property.getProperty(), value);
        return this;
    }

    public ConnectorConfig setProperty(String property, String value) {
        properties.put(property, value);
        return this;
    }

    public String getDefaultApiUrl() {
        return ConfigUtils.getDefaultApiUrl(properties);
    }

    public String getManagementApiUrl() {
        return ConfigUtils.getManagementApiUrl(properties);
    }

    public String getManagementApiKey() {
        return ConfigUtils.getManagementApiKey(properties);
    }

    public String getProtocolApiUrl() {
        return ConfigUtils.getProtocolApiUrl(properties);
    }

    public String getPublicApiUrl() {
        return ConfigUtils.getPublicApiUrl(properties);
    }

    public String getParticipantId() {
        return ConfigProps.EDC_PARTICIPANT_ID.getRaw(properties);
    }
}
