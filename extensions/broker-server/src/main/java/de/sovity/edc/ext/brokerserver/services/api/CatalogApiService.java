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

import de.sovity.edc.ext.brokerserver.dao.pages.catalog.CatalogQueryService;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.CatalogQueryFilter;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.ContractOfferRs;
import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.DataOfferRs;
import de.sovity.edc.ext.brokerserver.services.config.BrokerServerSettings;
import de.sovity.edc.ext.brokerserver.services.api.filtering.CatalogFilterService;
import de.sovity.edc.ext.wrapper.api.broker.model.CatalogPageQuery;
import de.sovity.edc.ext.wrapper.api.broker.model.CatalogPageResult;
import de.sovity.edc.ext.wrapper.api.broker.model.CatalogPageSortingItem;
import de.sovity.edc.ext.wrapper.api.broker.model.CatalogPageSortingType;
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
    private final CatalogQueryService catalogQueryService;
    private final PolicyDtoBuilder policyDtoBuilder;
    private final AssetPropertyParser assetPropertyParser;
    private final CatalogFilterService catalogFilterService;
    private final BrokerServerSettings brokerServerSettings;

    public CatalogPageResult catalogPage(DSLContext dsl, CatalogPageQuery query) {
        Objects.requireNonNull(query, "query must not be null");


        var filter = new CatalogQueryFilter(
                query.getSearchQuery(),
                catalogFilterService.getSelectedFiltersQuery(query.getFilter())
        );

        var pageQuery = paginationMetadataUtils.getPageQuery(
                query.getPageOneBased(),
                brokerServerSettings.getCatalogPagePageSize()
        );

        // execute db query
        var catalogPageRs = catalogQueryService.queryCatalogPage(
                dsl,
                filter,
                query.getSorting(),
                pageQuery,
                catalogFilterService.getAvailableFiltersQuery(),
                brokerServerSettings.getDataSpaceConfig()
        );

        var paginationMetadata = paginationMetadataUtils.buildPaginationMetadata(
                query.getPageOneBased(),
                brokerServerSettings.getCatalogPagePageSize(),
                catalogPageRs.getDataOffers().size(),
                catalogPageRs.getNumTotalDataOffers()
        );

        var result = new CatalogPageResult();
        result.setAvailableSortings(buildAvailableSortings());
        result.setPaginationMetadata(paginationMetadata);
        result.setAvailableFilters(catalogFilterService.buildAvailableFilters(catalogPageRs.getAvailableFilterValues()));
        result.setDataOffers(buildDataOfferListEntries(catalogPageRs.getDataOffers()));
        return result;
    }

    private List<DataOfferListEntry> buildDataOfferListEntries(List<DataOfferRs> dataOfferRs) {
        return dataOfferRs.stream()
                .map(this::buildDataOfferListEntry)
                .toList();
    }

    private DataOfferListEntry buildDataOfferListEntry(DataOfferRs dataOfferRs) {
        var dataOffer = new DataOfferListEntry();
        dataOffer.setAssetId(dataOfferRs.getAssetId());
        dataOffer.setCreatedAt(dataOfferRs.getCreatedAt());
        dataOffer.setUpdatedAt(dataOfferRs.getUpdatedAt());
        dataOffer.setProperties(assetPropertyParser.parsePropertiesFromJsonString(dataOfferRs.getAssetPropertiesJson()));
        dataOffer.setContractOffers(buildDataOfferListEntryContractOffers(dataOfferRs));
        dataOffer.setConnectorEndpoint(dataOfferRs.getConnectorEndpoint());
        dataOffer.setConnectorOfflineSinceOrLastUpdatedAt(dataOfferRs.getConnectorOfflineSinceOrLastUpdatedAt());
        dataOffer.setConnectorOnlineStatus(getOnlineStatus(dataOfferRs));
        return dataOffer;
    }

    private List<DataOfferListEntryContractOffer> buildDataOfferListEntryContractOffers(DataOfferRs dataOfferRs) {
        return dataOfferRs.getContractOffers().stream()
                .map(this::buildDataOfferListEntryContractOffer)
                .toList();
    }

    private DataOfferListEntryContractOffer buildDataOfferListEntryContractOffer(ContractOfferRs contractOfferDbRow) {
        var contractOffer = new DataOfferListEntryContractOffer();
        contractOffer.setContractOfferId(contractOfferDbRow.getContractOfferId());
        contractOffer.setContractPolicy(policyDtoBuilder.buildPolicyFromJson(contractOfferDbRow.getPolicyJson()));
        contractOffer.setCreatedAt(contractOfferDbRow.getCreatedAt());
        contractOffer.setUpdatedAt(contractOfferDbRow.getUpdatedAt());
        return contractOffer;
    }

    private ConnectorOnlineStatus getOnlineStatus(DataOfferRs dataOfferRs) {
        return switch (dataOfferRs.getConnectorOnlineStatus()) {
            case ONLINE -> ConnectorOnlineStatus.ONLINE;
            case OFFLINE -> ConnectorOnlineStatus.OFFLINE;
            default -> throw new IllegalStateException("Unknown ConnectorOnlineStatus from DAO for API: " + dataOfferRs.getConnectorOnlineStatus());
        };
    }

    private static List<CatalogPageSortingItem> buildAvailableSortings() {
        return Arrays.stream(CatalogPageSortingType.values()).map(it -> new CatalogPageSortingItem(it, it.getTitle())).toList();
    }
}
