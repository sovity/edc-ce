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

import de.sovity.edc.extension.e2e.connector.config.api.auth.AuthProvider;
import lombok.With;

import java.net.URI;
import java.util.Map;

public record EdcApiGroupConfig(
        EdcApiGroup edcApiGroup,
        String baseUrl,
        int port,
        String path,
        @With
        AuthProvider authProvider
) {

    public URI getUri() {
        return URI.create("%s:%s%s".formatted(baseUrl, port, path));
    }

    public Map<String, String> getProperties() {
        return edcApiGroup.getProperties(path, port);
    }
}
