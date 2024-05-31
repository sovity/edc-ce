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

package de.sovity.edc.ext.brokerserver.services.api;

import de.sovity.edc.ext.brokerserver.api.model.AuthorityPortalConnectorInfo;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.List;

@RequiredArgsConstructor
public class AuthorityPortalConnectorMetadataApiService {
    private final AuthorityPortalConnectorQueryService authorityPortalConnectorQueryService;
    private final ConnectorOnlineStatusMapper connectorOnlineStatusMapper;

    public List<AuthorityPortalConnectorInfo> getMetadataByEndpoints(DSLContext dsl, List<String> endpoints) {

        return authorityPortalConnectorQueryService.getConnectorMetadata(dsl, endpoints).stream()
            .map(it -> new AuthorityPortalConnectorInfo(
                it.getConnectorEndpoint(),
                it.getParticipantId(),
                it.getDataOfferCount(),
                connectorOnlineStatusMapper.getOnlineStatus(it.getOnlineStatus()),
                it.getOfflineSinceOrLastUpdatedAt()
            ))
            .toList();
    }
}
