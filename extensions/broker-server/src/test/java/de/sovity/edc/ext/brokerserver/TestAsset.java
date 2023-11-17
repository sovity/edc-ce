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

package de.sovity.edc.ext.brokerserver;

import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.DataOfferRecord;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedDataOffer;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestAsset {
    public static JsonObject getAssetJsonLd(String assetId) {
        return getAssetJsonLd(assetId, Map.of());
    }

    public static JsonObject getAssetJsonLd(String assetId, Map<String, Object> properties) {
        return Json.createObjectBuilder()
            .add(Prop.ID, assetId)
            .add(Prop.Edc.PROPERTIES, Json.createObjectBuilder()
                .add(Prop.Edc.ASSET_ID, assetId)
                .addAll(Json.createObjectBuilder(properties)))
            .build();
    }

    /**
     * Sets assetJsonLd and other extracted fields.
     * <p>
     * This method keeps our tests consistent if we change the extracted fields.
     *
     * @param dataOfferRecord data offer record to be updated
     * @param assetJsonLd     asset json ld
     * @param participantId   required because the organization name will default to the participant id if unset
     */
    public static void setDataOfferAssetMetadata(DataOfferRecord dataOfferRecord, JsonObject assetJsonLd, String participantId) {
        // We trickily use the real code to update all the extracted values from the asset JSON-LD
        var fetchedCatalogBuilder = BrokerServerExtensionContext.instance.fetchedCatalogBuilder();
        var dataOfferRecordUpdater = BrokerServerExtensionContext.instance.dataOfferRecordUpdater();

        var fetchedDataOffer = new FetchedDataOffer();
        fetchedCatalogBuilder.setAssetMetadata(fetchedDataOffer, assetJsonLd, participantId);

        dataOfferRecord.setAssetId(fetchedDataOffer.getAssetId());
        dataOfferRecordUpdater.updateDataOffer(dataOfferRecord, fetchedDataOffer, false);
    }
}
