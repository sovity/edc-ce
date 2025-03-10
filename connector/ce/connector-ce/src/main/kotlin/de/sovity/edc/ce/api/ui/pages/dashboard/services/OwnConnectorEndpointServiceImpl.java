/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.dashboard.services;

import de.sovity.edc.ce.libs.mappers.asset.OwnConnectorEndpointService;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OwnConnectorEndpointServiceImpl implements OwnConnectorEndpointService {
    private final SelfDescriptionService selfDescriptionService;

    @Override
    public boolean isOwnConnectorEndpoint(String endpoint) {
        return selfDescriptionService.getConnectorEndpoint().equals(endpoint);
    }
}
