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

package de.sovity.edc.extension.e2e.connector.config.api.auth;


public record ApiKeyAuthProvider(
        String headerName,
        String apiKey) implements AuthProvider {
    @Override
    public String getAuthorizationHeader() {
        return headerName;
    }

    @Override
    public String getAuthorizationHeaderValue() {
        return apiKey;
    }
}
