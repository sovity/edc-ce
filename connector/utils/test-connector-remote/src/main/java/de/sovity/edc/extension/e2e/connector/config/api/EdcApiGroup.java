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


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum EdcApiGroup {
    DEFAULT(""),
    PROTOCOL("protocol"),
    MANAGEMENT("management"),
    CONTROL("control");

    private final String name;

    public Map<String, String> getProperties(String path, int port) {
        if (this == EdcApiGroup.DEFAULT) {
            return Map.of(
                    "web.http.path", path,
                    "web.http.port", String.valueOf(port)
            );
        } else {
            return Map.of(
                    "web.http.%s.path".formatted(name), path,
                    "web.http.%s.port".formatted(name), String.valueOf(port));
        }
    }
}
