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

import de.sovity.edc.ext.brokerserver.api.model.AuthorityPortalConnectorDataOfferInfo;
import de.sovity.edc.ext.brokerserver.api.model.AuthorityPortalConnectorDataOfferDetails;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.List;

@RequiredArgsConstructor
public class AuthorityPortalConnectorDataOfferApiService {
    private final AuthorityPortalConnectorQueryService authorityPortalConnectorQueryService;
    private final ConnectorOnlineStatusMapper connectorOnlineStatusMapper;

    public List<AuthorityPortalConnectorDataOfferInfo> getConnectorDataOffersByEndpoints(DSLContext dsl, List<String> endpoints) {
        return authorityPortalConnectorQueryService.getConnectorsDataOffers(dsl, endpoints).stream()
            .map(it -> new AuthorityPortalConnectorDataOfferInfo(
                it.getConnectorEndpoint(),
                it.getParticipantId(),
                connectorOnlineStatusMapper.getOnlineStatus(it.getOnlineStatus()),
                it.getOfflineSinceOrLastUpdatedAt(),
                it.getDataOffers().stream().map(dataOffer -> new AuthorityPortalConnectorDataOfferDetails(dataOffer.getDataOfferId(), dataOffer.getDataOfferName())).toList()
            ))
            .toList();
    }

}
