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

import de.sovity.edc.ext.brokerserver.api.model.ConnectorOnlineStatus;
import de.sovity.edc.ext.brokerserver.api.model.DataOfferDetailContractOffer;
import de.sovity.edc.ext.brokerserver.api.model.DataOfferDetailPageQuery;
import de.sovity.edc.ext.brokerserver.api.model.DataOfferDetailPageResult;
import de.sovity.edc.ext.brokerserver.dao.pages.dataoffer.DataOfferDetailPageQueryService;
import de.sovity.edc.ext.brokerserver.dao.pages.dataoffer.ViewCountLogger;
import de.sovity.edc.ext.brokerserver.dao.pages.dataoffer.model.ContractOfferRs;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class DataOfferDetailApiService {
    private final DataOfferDetailPageQueryService dataOfferDetailPageQueryService;
    private final ViewCountLogger viewCountLogger;
    private final DataOfferMappingUtils dataOfferMappingUtils;

    public DataOfferDetailPageResult dataOfferDetailPage(DSLContext dsl, DataOfferDetailPageQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        var dataOffer = dataOfferDetailPageQueryService.queryDataOfferDetailsPage(dsl, query.getAssetId(), query.getConnectorEndpoint());
        var asset = dataOfferMappingUtils.buildUiAsset(
            dataOffer.getAssetJsonLd(),
            dataOffer.getConnectorEndpoint(),
            dataOffer.getConnectorParticipantId()
        );
        viewCountLogger.increaseDataOfferViewCount(dsl, query.getAssetId(), query.getConnectorEndpoint());

        var result = new DataOfferDetailPageResult();
        result.setAssetId(dataOffer.getAssetId());
        result.setConnectorEndpoint(dataOffer.getConnectorEndpoint());
        result.setConnectorOnlineStatus(mapConnectorOnlineStatus(dataOffer.getConnectorOnlineStatus()));
        result.setConnectorOfflineSinceOrLastUpdatedAt(dataOffer.getConnectorOfflineSinceOrLastUpdatedAt());
        result.setAsset(asset);
        result.setCreatedAt(dataOffer.getCreatedAt());
        result.setUpdatedAt(dataOffer.getUpdatedAt());
        result.setContractOffers(buildDataOfferDetailContractOffers(dataOffer.getContractOffers()));
        result.setViewCount(dataOffer.getViewCount());
        return result;
    }

    private ConnectorOnlineStatus mapConnectorOnlineStatus(
            de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus connectorOnlineStatus
    ) {
        if (connectorOnlineStatus == null) {
            return ConnectorOnlineStatus.OFFLINE;
        }

        return switch (connectorOnlineStatus) {
            case ONLINE -> ConnectorOnlineStatus.ONLINE;
            case OFFLINE -> ConnectorOnlineStatus.OFFLINE;
            case DEAD -> ConnectorOnlineStatus.DEAD;
        };
    }

    private List<DataOfferDetailContractOffer> buildDataOfferDetailContractOffers(List<ContractOfferRs> contractOffers) {
        return contractOffers.stream().map(this::buildDataOfferDetailContractOffer).toList();
    }

    @NotNull
    private DataOfferDetailContractOffer buildDataOfferDetailContractOffer(ContractOfferRs offer) {
        var newOffer = new DataOfferDetailContractOffer();
        newOffer.setCreatedAt(offer.getCreatedAt());
        newOffer.setUpdatedAt(offer.getUpdatedAt());
        newOffer.setContractOfferId(offer.getContractOfferId());
        newOffer.setContractPolicy(dataOfferMappingUtils.buildUiPolicy(offer.getPolicyJson()));
        return newOffer;
    }
}
