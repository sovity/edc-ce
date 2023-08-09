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

public record EdcApiGroupConfig(
        EdcApiGroup edcApiGroup,
        String baseUrl,
        int port,
        String path) implements EdcConfig {

    private static final String SETTING_WEB_HTTP_PATH = "web.http.%s.path";
    private static final String SETTING_WEB_HTTP_PORT = "web.http.%s.port";
    private static final String SETTING_WEB_HTTP_DEFAULT_PATH = "web.http.path";
    private static final String SETTING_WEB_HTTP_DEFAULT_PORT = "web.http.port";

    public static EdcApiGroupConfig mgntFromUri(URI uri) {
        return fromUri(EdcApiGroup.MANAGEMENT, uri);
    }

    public static EdcApiGroupConfig protocolFromUri(URI uri) {
        return fromUri(EdcApiGroup.PROTOCOL, uri);
    }

    public static EdcApiGroupConfig fromUri(EdcApiGroup edcApiGroup, URI uri) {
        return new EdcApiGroupConfig(
                edcApiGroup,
                String.format("%s://%s", uri.getScheme(), uri.getHost()),
                uri.getPort(),
                uri.getPath());
    }

    public URI getUri() {
        return URI.create(String.format("%s:%s%s", baseUrl, port, path));
    }

    @Override
    public Map<String, String> toEdcSettingMap() {
        if ("".equals(edcApiGroup.getDataSourcePropertyName())) {
            return Map.of(
                    SETTING_WEB_HTTP_DEFAULT_PATH, path,
                    SETTING_WEB_HTTP_DEFAULT_PORT, String.valueOf(port)
            );
        } else {
            var webHttpPathProperty = String.format(
                    SETTING_WEB_HTTP_PATH,
                    edcApiGroup.getDataSourcePropertyName());
            var webHttpPortProperty = String.format(
                    SETTING_WEB_HTTP_PORT,
                    edcApiGroup.getDataSourcePropertyName());
            return Map.of(
                    webHttpPathProperty, path,
                    webHttpPortProperty, String.valueOf(port));
        }
    }
}
