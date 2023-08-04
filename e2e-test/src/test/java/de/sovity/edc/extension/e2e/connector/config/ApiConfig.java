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

import java.net.URI;
import java.util.Map;

public record ApiConfig(String baseUrl, String name, String path, int port) implements EdcConfig {

    private static final String SETTING_WEB_HTTP_PATH = "web.http.%s.path";
    private static final String SETTING_WEB_HTTP_PORT = "web.http.%s.port";
    private static final String SETTING_WEB_HTTP_DEFAULT_PATH = "web.http.path";
    private static final String SETTING_WEB_HTTP_DEFAULT_PORT = "web.http.port";

    public URI getUri() {
        return URI.create(String.format("%s:%s%s", baseUrl, port, path));
    }

    @Override
    public Map<String, String> toMap() {
        if ("".equals(name)) {
            return Map.of(
                    SETTING_WEB_HTTP_DEFAULT_PATH, path,
                    SETTING_WEB_HTTP_DEFAULT_PORT, String.valueOf(port)
            );
        } else {
            return Map.of(
                    String.format(SETTING_WEB_HTTP_PATH, name), path,
                    String.format(SETTING_WEB_HTTP_PORT, name), String.valueOf(port)
            );
        }
    }
}
