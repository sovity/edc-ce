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

import de.sovity.edc.ext.brokerserver.dao.pages.connector.ConnectorPageQueryService;
import de.sovity.edc.ext.brokerserver.dao.pages.connector.model.ConnectorRs;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorDetailPageQuery;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorDetailPageResult;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorListEntry;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorOnlineStatus;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorPageQuery;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorPageResult;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorPageSortingItem;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorPageSortingType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ConnectorApiService {
    private final ConnectorPageQueryService connectorPageQueryService;
    private final PaginationMetadataUtils paginationMetadataUtils;

    public ConnectorPageResult connectorPage(DSLContext dsl, ConnectorPageQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        var connectorDbRows = connectorPageQueryService.queryConnectorPage(dsl, query.getSearchQuery(), query.getSorting());

        var result = new ConnectorPageResult();
        result.setAvailableSortings(buildAvailableSortings());
        result.setPaginationMetadata(paginationMetadataUtils.buildDummyPaginationMetadata(connectorDbRows.size()));
        result.setConnectors(buildConnectorListEntries(connectorDbRows));
        return result;
    }

    public ConnectorDetailPageResult connectorDetailPage(DSLContext dsl, ConnectorDetailPageQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        var connectorDbRow = connectorPageQueryService.queryConnectorDetailPage(dsl, query.getConnectorEndpoint());
        var connector = buildConnectorListEntry(connectorDbRow);

        var result = new ConnectorDetailPageResult();
        result.setCreatedAt(connector.getCreatedAt());
        result.setEndpoint(connector.getEndpoint());
        result.setId(connector.getId());
        result.setLastRefreshAttemptAt(connector.getLastRefreshAttemptAt());
        result.setLastSuccessfulRefreshAt(connector.getLastSuccessfulRefreshAt());
        result.setNumContractOffers(connector.getNumContractOffers());
        result.setOnlineStatus(connector.getOnlineStatus());
        return result;
    }

    private List<ConnectorListEntry> buildConnectorListEntries(List<ConnectorRs> connectors) {
        return connectors.stream().map(this::buildConnectorListEntry).toList();
    }

    private ConnectorListEntry buildConnectorListEntry(ConnectorRs connector) {
        var dto = new ConnectorListEntry();
        dto.setId(connector.getConnectorId());
        dto.setEndpoint(connector.getEndpoint());
        dto.setCreatedAt(connector.getCreatedAt());
        dto.setLastRefreshAttemptAt(connector.getLastRefreshAttemptAt());
        dto.setLastSuccessfulRefreshAt(connector.getLastSuccessfulRefreshAt());
        dto.setOnlineStatus(getOnlineStatus(connector));
        dto.setNumContractOffers(connector.getNumDataOffers());
        return dto;
    }

    private ConnectorOnlineStatus getOnlineStatus(ConnectorRs connector) {
        return switch (connector.getOnlineStatus()) {
            case ONLINE -> ConnectorOnlineStatus.ONLINE;
            case OFFLINE -> ConnectorOnlineStatus.OFFLINE;
            default -> throw new IllegalStateException("Unknown ConnectorOnlineStatus from DAO for API: " + connector.getOnlineStatus());
        };
    }

    private List<ConnectorPageSortingItem> buildAvailableSortings() {
        return Stream.of(
                ConnectorPageSortingType.MOST_RECENT,
                ConnectorPageSortingType.TITLE
        ).map(it -> new ConnectorPageSortingItem(it, it.getTitle())).toList();
    }
}
