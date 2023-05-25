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
import de.sovity.edc.ext.wrapper.api.broker.model.DataOffer;
import de.sovity.edc.ext.wrapper.api.broker.model.DataOfferConnectorInfo;
import de.sovity.edc.ext.wrapper.api.common.model.AssetDto;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
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
        result.setDataOffers(buildDataOffers(dataOfferDbRows));
        return result;
    }

    private List<DataOffer> buildDataOffers(List<DataOfferDbRow> dataOfferDbRows) {
        return dataOfferDbRows.stream().map(this::buildDataOffer).toList();
    }

    private DataOffer buildDataOffer(DataOfferDbRow dataOfferDbRow) {
        AssetDto assetDto = new AssetDto();
        assetDto.setAssetId(dataOfferDbRow.getAssetId());
        assetDto.setCreatedAt(dataOfferDbRow.getCreatedAt());
        assetDto.setProperties(assetPropertyParser.parsePropertiesFromJsonString(dataOfferDbRow.getAssetPropertiesJson()));

        DataOfferConnectorInfo connectorInfo = new DataOfferConnectorInfo();
        connectorInfo.setEndpoint(dataOfferDbRow.getConnectorEndpoint());
        connectorInfo.setTitle(dataOfferDbRow.getConnectorTitle());
        connectorInfo.setDescription(dataOfferDbRow.getConnectorDescription());
        connectorInfo.setOnlineStatus(getOnlineStatus(dataOfferDbRow));
        connectorInfo.setOfflineSinceOrLastUpdatedAt(dataOfferDbRow.getOfflineSinceOrLastUpdatedAt());

        DataOffer dataOffer = new DataOffer();
        dataOffer.setAsset(assetDto);
        dataOffer.setConnectorInfo(connectorInfo);
        dataOffer.setPolicy(buildPolicies(dataOfferDbRow));
        return dataOffer;
    }

    private List<PolicyDto> buildPolicies(DataOfferDbRow dataOfferDbRow) {
        return dataOfferDbRow.getContractOffers().stream()
                .map(DataOfferContractOfferDbRow::getPolicyJson)
                .map(policyDtoBuilder::buildPolicyFromJson)
                .toList();
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
