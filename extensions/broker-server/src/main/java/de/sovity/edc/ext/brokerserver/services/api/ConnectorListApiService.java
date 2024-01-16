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

import de.sovity.edc.ext.brokerserver.api.model.ConnectorListEntry;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorPageQuery;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorPageResult;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorPageSortingItem;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorPageSortingType;
import de.sovity.edc.ext.brokerserver.dao.pages.connector.ConnectorListQueryService;
import de.sovity.edc.ext.brokerserver.dao.pages.connector.model.ConnectorListEntryRs;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ConnectorListApiService {
    private final ConnectorListQueryService connectorListQueryService;
    private final ConnectorOnlineStatusMapper connectorOnlineStatusMapper;
    private final PaginationMetadataUtils paginationMetadataUtils;

    public ConnectorPageResult connectorListPage(DSLContext dsl, ConnectorPageQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        var availableSortings = buildAvailableSortings();
        var sorting = query.getSorting();
        if (sorting == null) {
            sorting = availableSortings.get(0).getSorting();
        }

        var connectorDbRows = connectorListQueryService.queryConnectorPage(dsl, query.getSearchQuery(), sorting);

        var result = new ConnectorPageResult();
        result.setAvailableSortings(availableSortings);
        result.setPaginationMetadata(paginationMetadataUtils.buildDummyPaginationMetadata(connectorDbRows.size()));
        result.setConnectors(buildConnectorListEntries(connectorDbRows));
        return result;
    }

    private List<ConnectorListEntry> buildConnectorListEntries(List<ConnectorListEntryRs> connectors) {
        return connectors.stream().map(this::buildConnectorListEntry).toList();
    }

    private ConnectorListEntry buildConnectorListEntry(ConnectorListEntryRs connector) {
        var dto = new ConnectorListEntry();
        dto.setParticipantId(connector.getParticipantId());
        dto.setEndpoint(connector.getEndpoint());
        dto.setOrganizationName(connector.getOrganizationName());
        dto.setCreatedAt(connector.getCreatedAt());
        dto.setLastRefreshAttemptAt(connector.getLastRefreshAttemptAt());
        dto.setLastSuccessfulRefreshAt(connector.getLastSuccessfulRefreshAt());
        dto.setOnlineStatus(connectorOnlineStatusMapper.getOnlineStatus(connector.getOnlineStatus()));
        dto.setNumDataOffers(connector.getNumDataOffers());
        return dto;
    }

    private List<ConnectorPageSortingItem> buildAvailableSortings() {
        return Stream.of(
            ConnectorPageSortingType.MOST_RECENT,
            ConnectorPageSortingType.TITLE
        ).map(it -> new ConnectorPageSortingItem(it, it.getTitle())).toList();
    }
}
