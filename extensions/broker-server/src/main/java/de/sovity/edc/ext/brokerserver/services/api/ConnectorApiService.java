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

import de.sovity.edc.ext.brokerserver.dao.stores.ConnectorQueries;
import de.sovity.edc.ext.brokerserver.db.DslContextFactory;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.ConnectorRecord;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorListEntry;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorOnlineStatus;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorPageQuery;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorPageResult;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorPageSortingItem;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorPageSortingType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ConnectorApiService {
    private final DslContextFactory dslContextFactory;
    private final ConnectorQueries connectorQueries;
    private final PaginationMetadataUtils paginationMetadataUtils;

    public ConnectorPageResult connectorPage(ConnectorPageQuery query) {
        return dslContextFactory.transactionResult(dsl -> {
            Objects.requireNonNull(query, "query must not be null");

            var connectors = connectorQueries.findAll(dsl)
                    .map(ConnectorApiService::buildConnectorListEntry)
                    .sorted(Comparator.comparing(ConnectorListEntry::getTitle))
                    .toList();

            var result = new ConnectorPageResult();
            result.setAvailableSortings(buildAvailableSortings());
            result.setPaginationMetadata(paginationMetadataUtils.buildDummyPaginationMetadata(connectors.size()));
            result.setConnectors(connectors);
            return result;
        });
    }

    @NotNull
    private static ConnectorListEntry buildConnectorListEntry(ConnectorRecord it) {
        ConnectorListEntry dto = new ConnectorListEntry();
        dto.setId(it.getEndpoint());
        dto.setIdsId(it.getIdsId());
        dto.setEndpoint(it.getEndpoint());
        dto.setTitle(it.getTitle());
        dto.setDescription(it.getDescription());
        dto.setCreatedAt(it.getCreatedAt());
        dto.setLastFetchAt(it.getLastUpdate());
        dto.setOnlineStatus(getOnlineStatus(it));
        dto.setOfflineSince(it.getOfflineSince());
        dto.setNumContractOffers(-1);
        return dto;
    }

    private static ConnectorOnlineStatus getOnlineStatus(ConnectorRecord it) {
        return switch (it.getOnlineStatus()) {
            case ONLINE -> ConnectorOnlineStatus.ONLINE;
            case OFFLINE -> ConnectorOnlineStatus.OFFLINE;
            default -> throw new IllegalStateException("Unknown ConnectorOnlineStatus from DAO for API: " + it.getOnlineStatus());
        };
    }

    @NotNull
    private static List<ConnectorPageSortingItem> buildAvailableSortings() {
        return Arrays.stream(ConnectorPageSortingType.values()).map(it -> new ConnectorPageSortingItem(it, it.getTitle())).toList();
    }
}
