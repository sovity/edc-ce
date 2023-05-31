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

import de.sovity.edc.ext.brokerserver.dao.models.DataOfferContractOfferDbRow;
import de.sovity.edc.ext.brokerserver.dao.models.DataOfferDbRow;
import de.sovity.edc.ext.brokerserver.dao.queries.DataOfferQueries;
import de.sovity.edc.ext.wrapper.api.broker.model.CatalogPageQuery;
import de.sovity.edc.ext.wrapper.api.broker.model.CatalogPageResult;
import de.sovity.edc.ext.wrapper.api.broker.model.CatalogPageSortingItem;
import de.sovity.edc.ext.wrapper.api.broker.model.CatalogPageSortingType;
import de.sovity.edc.ext.wrapper.api.broker.model.CnfFilter;
import de.sovity.edc.ext.wrapper.api.broker.model.ConnectorOnlineStatus;
import de.sovity.edc.ext.wrapper.api.broker.model.DataOfferListEntry;
import de.sovity.edc.ext.wrapper.api.broker.model.DataOfferListEntryContractOffer;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class CatalogApiService {
    private final PaginationMetadataUtils paginationMetadataUtils;
    private final DataOfferQueries dataOfferQueries;
    private final PolicyDtoBuilder policyDtoBuilder;
    private final AssetPropertyParser assetPropertyParser;

    public CatalogPageResult catalogPage(DSLContext dsl, CatalogPageQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        var dataOfferDbRows = dataOfferQueries.forCatalogPage(dsl, query.getSearchQuery(), query.getSorting());

        var result = new CatalogPageResult();
        result.setAvailableSortings(buildAvailableSortings());
        result.setPaginationMetadata(paginationMetadataUtils.buildDummyPaginationMetadata(dataOfferDbRows.size()));
        result.setAvailableFilters(new CnfFilter(List.of()));
        result.setDataOffers(buildDataOfferListEntries(dataOfferDbRows));
        return result;
    }

    private List<DataOfferListEntry> buildDataOfferListEntries(List<DataOfferDbRow> dataOfferDbRows) {
        return dataOfferDbRows.stream()
                .map(this::buildDataOfferListEntry)
                .toList();
    }

    private DataOfferListEntry buildDataOfferListEntry(DataOfferDbRow dataOfferDbRow) {
        var dataOffer = new DataOfferListEntry();
        dataOffer.setAssetId(dataOfferDbRow.getAssetId());
        dataOffer.setCreatedAt(dataOfferDbRow.getCreatedAt());
        dataOffer.setUpdatedAt(dataOfferDbRow.getUpdatedAt());
        dataOffer.setProperties(assetPropertyParser.parsePropertiesFromJsonString(dataOfferDbRow.getAssetPropertiesJson()));
        dataOffer.setContractOffers(buildDataOfferListEntryContractOffers(dataOfferDbRow));
        dataOffer.setConnectorEndpoint(dataOfferDbRow.getConnectorEndpoint());
        dataOffer.setConnectorOfflineSinceOrLastUpdatedAt(dataOfferDbRow.getConnectorOfflineSinceOrLastUpdatedAt());
        dataOffer.setConnectorOnlineStatus(getOnlineStatus(dataOfferDbRow));
        return dataOffer;
    }

    private List<DataOfferListEntryContractOffer> buildDataOfferListEntryContractOffers(DataOfferDbRow dataOfferDbRow) {
        return dataOfferDbRow.getContractOffers().stream()
                .map(this::buildDataOfferListEntryContractOffer)
                .toList();
    }

    private DataOfferListEntryContractOffer buildDataOfferListEntryContractOffer(DataOfferContractOfferDbRow contractOfferDbRow) {
        var contractOffer = new DataOfferListEntryContractOffer();
        contractOffer.setContractOfferId(contractOfferDbRow.getContractOfferId());
        contractOffer.setContractPolicy(policyDtoBuilder.buildPolicyFromJson(contractOfferDbRow.getPolicyJson()));
        contractOffer.setCreatedAt(contractOfferDbRow.getCreatedAt());
        contractOffer.setUpdatedAt(contractOfferDbRow.getUpdatedAt());
        return contractOffer;
    }

    private ConnectorOnlineStatus getOnlineStatus(DataOfferDbRow dataOfferDbRow) {
        return switch (dataOfferDbRow.getConnectorOnlineStatus()) {
            case ONLINE -> ConnectorOnlineStatus.ONLINE;
            case OFFLINE -> ConnectorOnlineStatus.OFFLINE;
            default -> throw new IllegalStateException("Unknown ConnectorOnlineStatus from DAO for API: " + dataOfferDbRow.getConnectorOnlineStatus());
        };
    }

    private static List<CatalogPageSortingItem> buildAvailableSortings() {
        return Arrays.stream(CatalogPageSortingType.values()).map(it -> new CatalogPageSortingItem(it, it.getTitle())).toList();
    }
}
