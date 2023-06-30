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
 *       sovity GmbH - initial implementation
 *
 */

package de.sovity.edc.ext.brokerserver.services.refreshing.offers;

import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.DataOfferContractOfferRecord;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.DataOfferRecord;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

class DataOfferWriterTestResultHelper {
    private final @NotNull Map<String, DataOfferRecord> dataOffers;
    private final @NotNull Map<String, Map<String, DataOfferContractOfferRecord>> contractOffers;

    DataOfferWriterTestResultHelper(DSLContext dsl) {
        this.dataOffers = dsl.selectFrom(Tables.DATA_OFFER).fetchMap(Tables.DATA_OFFER.ASSET_ID);
        this.contractOffers = dsl.selectFrom(Tables.DATA_OFFER_CONTRACT_OFFER).stream().collect(groupingBy(
            DataOfferContractOfferRecord::getAssetId,
            Collectors.toMap(DataOfferContractOfferRecord::getContractOfferId, Function.identity())
        ));
    }

    public DataOfferRecord getDataOffer(String assetId) {
        return dataOffers.get(assetId);
    }

    public int numDataOffers() {
        return dataOffers.size();
    }

    public int numContractOffers(String assetId) {
        return contractOffers.getOrDefault(assetId, Map.of()).size();
    }

    public DataOfferContractOfferRecord getContractOffer(String assetId, String contractOfferId) {
        return contractOffers.getOrDefault(assetId, Map.of()).get(contractOfferId);
    }
}
