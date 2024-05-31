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

import de.sovity.edc.ext.brokerserver.api.model.ConnectorDetailPageQuery;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorDetailPageResult;
import de.sovity.edc.ext.brokerserver.dao.pages.connector.ConnectorDetailQueryService;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.Objects;

@RequiredArgsConstructor
public class ConnectorDetailApiService {
    private final ConnectorDetailQueryService connectorDetailQueryService;
    private final ConnectorOnlineStatusMapper connectorOnlineStatusMapper;

    public ConnectorDetailPageResult connectorDetailPage(DSLContext dsl, ConnectorDetailPageQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        var connectorDbRow = connectorDetailQueryService.queryConnectorDetailPage(dsl, query.getConnectorEndpoint());
        var dto = new ConnectorDetailPageResult();
        dto.setParticipantId(connectorDbRow.getParticipantId());
        dto.setEndpoint(connectorDbRow.getEndpoint());
        dto.setOrganizationName(connectorDbRow.getOrganizationName());
        dto.setCreatedAt(connectorDbRow.getCreatedAt());
        dto.setLastRefreshAttemptAt(connectorDbRow.getLastRefreshAttemptAt());
        dto.setLastSuccessfulRefreshAt(connectorDbRow.getLastSuccessfulRefreshAt());
        dto.setOnlineStatus(connectorOnlineStatusMapper.getOnlineStatus(connectorDbRow.getOnlineStatus()));
        dto.setNumDataOffers(connectorDbRow.getNumDataOffers());
        dto.setConnectorCrawlingTimeAvg(connectorDbRow.getConnectorCrawlingTimeAvg());
        return dto;
    }

}
