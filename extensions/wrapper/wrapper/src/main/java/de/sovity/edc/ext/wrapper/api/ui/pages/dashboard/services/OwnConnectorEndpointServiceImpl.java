/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services;

import de.sovity.edc.ext.wrapper.api.common.mappers.utils.OwnConnectorEndpointService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OwnConnectorEndpointServiceImpl implements OwnConnectorEndpointService {
    private final SelfDescriptionService selfDescriptionService;

    @Override
    public boolean isOwnConnectorEndpoint(String endpoint) {
        return selfDescriptionService.getConnectorEndpoint().equals(endpoint);
    }
}
