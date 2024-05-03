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

import de.sovity.edc.ext.brokerserver.api.model.CatalogContractOffer;
import de.sovity.edc.ext.brokerserver.api.model.CatalogDataOffer;
import de.sovity.edc.ext.brokerserver.api.model.CatalogPageQuery;
import de.sovity.edc.ext.brokerserver.api.model.CatalogPageResult;
import de.sovity.edc.ext.brokerserver.api.model.CatalogPageSortingItem;
import de.sovity.edc.ext.brokerserver.api.model.CatalogPageSortingType;
import de.sovity.edc.ext.brokerserver.api.model.ConnectorOnlineStatus;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.CatalogQueryService;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.DataOfferListEntryRs;
import de.sovity.edc.ext.brokerserver.dao.pages.dataoffer.model.ContractOfferRs;
import de.sovity.edc.ext.brokerserver.services.api.filtering.CatalogFilterService;
import de.sovity.edc.ext.brokerserver.services.config.BrokerServerSettings;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class CatalogApiService {
    private final PaginationMetadataUtils paginationMetadataUtils;
    private final CatalogQueryService catalogQueryService;
    private final DataOfferMappingUtils dataOfferMappingUtils;
    private final CatalogFilterService catalogFilterService;
    private final BrokerServerSettings brokerServerSettings;

    public CatalogPageResult catalogPage(DSLContext dsl, CatalogPageQuery query) {
        Objects.requireNonNull(query, "query must not be null");


        var filters = catalogFilterService.getCatalogQueryFilters(query.getFilter());

        var pageQuery = paginationMetadataUtils.getPageQuery(
                query.getPageOneBased(),
                brokerServerSettings.getCatalogPagePageSize()
        );

        var availableSortings = buildAvailableSortings();
        var sorting = query.getSorting();
        if (sorting == null) {
            sorting = availableSortings.get(0).getSorting();
        }

        // execute db query
        var catalogPageRs = catalogQueryService.queryCatalogPage(
                dsl,
                query.getSearchQuery(),
                filters,
                sorting,
                pageQuery
        );

        var paginationMetadata = paginationMetadataUtils.buildPaginationMetadata(
                query.getPageOneBased(),
                brokerServerSettings.getCatalogPagePageSize(),
                catalogPageRs.getDataOffers().size(),
                catalogPageRs.getNumTotalDataOffers()
        );

        var result = new CatalogPageResult();
        result.setAvailableSortings(availableSortings);
        result.setPaginationMetadata(paginationMetadata);
        result.setAvailableFilters(catalogFilterService.buildAvailableFilters(catalogPageRs.getAvailableFilterValues()));
        result.setDataOffers(buildCatalogDataOffers(catalogPageRs.getDataOffers()));
        return result;
    }

    private List<CatalogDataOffer> buildCatalogDataOffers(List<DataOfferListEntryRs> dataOfferRs) {
        return dataOfferRs.stream()
                .map(this::buildCatalogDataOffer)
                .toList();
    }

    private CatalogDataOffer buildCatalogDataOffer(DataOfferListEntryRs dataOfferRs) {
        var asset = dataOfferMappingUtils.buildUiAsset(
            dataOfferRs.getAssetJsonLd(),
            dataOfferRs.getConnectorEndpoint(),
            dataOfferRs.getConnectorParticipantId(),
            dataOfferRs.getOrganizationName()
        );

        var dataOffer = new CatalogDataOffer();
        dataOffer.setAssetId(dataOfferRs.getAssetId());
        dataOffer.setCreatedAt(dataOfferRs.getCreatedAt());
        dataOffer.setUpdatedAt(dataOfferRs.getUpdatedAt());
        dataOffer.setAsset(asset);
        dataOffer.setContractOffers(buildCatalogContractOffers(dataOfferRs));
        dataOffer.setConnectorEndpoint(dataOfferRs.getConnectorEndpoint());
        dataOffer.setConnectorOfflineSinceOrLastUpdatedAt(dataOfferRs.getConnectorOfflineSinceOrLastUpdatedAt());
        dataOffer.setConnectorOnlineStatus(getOnlineStatus(dataOfferRs));
        return dataOffer;
    }

    private List<CatalogContractOffer> buildCatalogContractOffers(DataOfferListEntryRs dataOfferRs) {
        return dataOfferRs.getContractOffers().stream()
                .map(this::buildCatalogContractOffer)
                .toList();
    }

    private CatalogContractOffer buildCatalogContractOffer(ContractOfferRs contractOfferDbRow) {
        var contractOffer = new CatalogContractOffer();
        contractOffer.setContractOfferId(contractOfferDbRow.getContractOfferId());
        contractOffer.setContractPolicy(dataOfferMappingUtils.buildUiPolicy(contractOfferDbRow.getPolicyJson()));
        contractOffer.setCreatedAt(contractOfferDbRow.getCreatedAt());
        contractOffer.setUpdatedAt(contractOfferDbRow.getUpdatedAt());
        return contractOffer;
    }

    private ConnectorOnlineStatus getOnlineStatus(DataOfferListEntryRs dataOfferRs) {
        return switch (dataOfferRs.getConnectorOnlineStatus()) {
            case ONLINE -> ConnectorOnlineStatus.ONLINE;
            case OFFLINE -> ConnectorOnlineStatus.OFFLINE;
            case DEAD -> ConnectorOnlineStatus.DEAD;
            default -> throw new IllegalStateException("Unknown ConnectorOnlineStatus from DAO for API: " + dataOfferRs.getConnectorOnlineStatus());
        };
    }

    private static List<CatalogPageSortingItem> buildAvailableSortings() {
        return Stream.of(
                CatalogPageSortingType.MOST_RECENT,
                CatalogPageSortingType.TITLE,
                CatalogPageSortingType.ORIGINATOR,
                CatalogPageSortingType.VIEW_COUNT
        ).map(it -> new CatalogPageSortingItem(it, it.getTitle())).toList();
    }
}
