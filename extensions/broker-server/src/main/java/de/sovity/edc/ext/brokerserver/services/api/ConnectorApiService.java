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
import de.sovity.edc.ext.brokerserver.api.model.ConnectorListEntry;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorOnlineStatus;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorPageQuery;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorPageResult;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorPageSortingItem;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorPageSortingType;
import de.sovity.edc.ext.brokerserver.dao.pages.connector.ConnectorPageQueryService;
import de.sovity.edc.ext.brokerserver.dao.pages.connector.model.ConnectorDetailsRs;
import de.sovity.edc.ext.brokerserver.dao.pages.connector.model.ConnectorListEntryRs;
import de.sovity.edc.ext.brokerserver.services.logging.BrokerEventLogger;
import de.sovity.edc.ext.brokerserver.utils.UrlUtils;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static de.sovity.edc.ext.brokerserver.services.queue.ConnectorRefreshPriority.ADDED_ON_API_CALL;
import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class ConnectorApiService {
    private final ConnectorPageQueryService connectorPageQueryService;
    private final ConnectorService connectorService;
    private final PaginationMetadataUtils paginationMetadataUtils;
    private final BrokerEventLogger brokerEventLogger;

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
        var connector = buildConnectorDetailPageEntry(connectorDbRow);

        var result = new ConnectorDetailPageResult();
        result.setCreatedAt(connector.getCreatedAt());
        result.setEndpoint(connector.getEndpoint());
        result.setId(connector.getId());
        result.setLastRefreshAttemptAt(connector.getLastRefreshAttemptAt());
        result.setLastSuccessfulRefreshAt(connector.getLastSuccessfulRefreshAt());
        result.setNumContractOffers(connector.getNumContractOffers());
        result.setOnlineStatus(connector.getOnlineStatus());
        result.setConnectorCrawlingTimeAvg(connector.getConnectorCrawlingTimeAvg());
        return result;
    }

    private List<ConnectorListEntry> buildConnectorListEntries(List<ConnectorListEntryRs> connectors) {
        return connectors.stream().map(this::buildConnectorListEntry).toList();
    }

    private ConnectorListEntry buildConnectorListEntry(ConnectorListEntryRs connector) {
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

    private ConnectorDetailPageResult buildConnectorDetailPageEntry(ConnectorDetailsRs connector) {
        var dto = new ConnectorDetailPageResult();
        dto.setId(connector.getConnectorId());
        dto.setEndpoint(connector.getEndpoint());
        dto.setCreatedAt(connector.getCreatedAt());
        dto.setLastRefreshAttemptAt(connector.getLastRefreshAttemptAt());
        dto.setLastSuccessfulRefreshAt(connector.getLastSuccessfulRefreshAt());
        dto.setOnlineStatus(getOnlineStatus(connector));
        dto.setNumContractOffers(connector.getNumDataOffers());
        dto.setConnectorCrawlingTimeAvg(connector.getConnectorCrawlingTimeAvg());
        return dto;
    }

    private ConnectorOnlineStatus getOnlineStatus(ConnectorListEntryRs connector) {
        return switch (connector.getOnlineStatus()) {
            case ONLINE -> ConnectorOnlineStatus.ONLINE;
            case OFFLINE -> ConnectorOnlineStatus.OFFLINE;
            case DEAD -> ConnectorOnlineStatus.DEAD;
            default -> throw new IllegalStateException("Unknown ConnectorOnlineStatus from DAO for API: " + connector.getOnlineStatus());
        };
    }

    private ConnectorOnlineStatus getOnlineStatus(ConnectorDetailsRs connector) {
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

    public void addConnectors(DSLContext dsl, List<String> connectorEndpoints) {
        var existingEndpoints = connectorService.getConnectorEndpoints(dsl);
        var endpoints = connectorEndpoints.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(UrlUtils::isValidUrl)
                .filter(endpoint -> !existingEndpoints.contains(endpoint))
                .collect(toSet());
        connectorService.addConnectors(dsl, endpoints, ADDED_ON_API_CALL);
    }

    public void deleteConnectors(DSLContext dsl, List<String> connectorEndpoints) {
        connectorService.deleteConnectors(dsl, connectorEndpoints);
        brokerEventLogger.logConnectorsDeleted(dsl, connectorEndpoints);
    }
}
