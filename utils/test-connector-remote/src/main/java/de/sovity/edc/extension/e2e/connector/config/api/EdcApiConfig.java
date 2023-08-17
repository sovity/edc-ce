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

package de.sovity.edc.extension.e2e.connector.config.api;


import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value
@Builder
public class EdcApiConfig {
    EdcApiGroupConfig defaultApiGroup;
    EdcApiGroupConfig protocolApiGroup;
    EdcApiGroupConfig managementApiGroup;
    EdcApiGroupConfig controlApiGroup;

    public Map<String, String> getProperties() {
        return Stream.of(defaultApiGroup, protocolApiGroup, managementApiGroup, controlApiGroup)
                .map(EdcApiGroupConfig::getProperties)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
