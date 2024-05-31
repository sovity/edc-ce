/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.ext.brokerserver.services.config;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminApiKeyValidator {
    private final BrokerServerSettings brokerServerSettings;

    public void validateAdminApiKey(String adminApiKey) {
        if (!brokerServerSettings.getAdminApiKey().equals(adminApiKey)) {
            throw new WebApplicationException("Invalid admin API key", Response.Status.UNAUTHORIZED);
        }
    }
}
