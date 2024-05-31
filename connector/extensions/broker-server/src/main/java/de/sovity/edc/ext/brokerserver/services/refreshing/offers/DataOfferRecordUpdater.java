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

package de.sovity.edc.ext.brokerserver.services.refreshing.offers;

import de.sovity.edc.ext.brokerserver.dao.utils.JsonbUtils;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.DataOfferRecord;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedDataOffer;
import de.sovity.edc.ext.brokerserver.utils.JsonUtils2;
import lombok.RequiredArgsConstructor;
import org.jooq.JSONB;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Creates or updates {@link DataOfferRecord} DB Rows.
 * <p>
 * (Or at least prepares them for batch inserts / updates)
 */
@RequiredArgsConstructor
public class DataOfferRecordUpdater {
    /**
     * Create a new {@link DataOfferRecord}.
     *
     * @param connectorEndpoint connector endpoint
     * @param fetchedDataOffer  new db row data
     * @return new db row
     */
    public DataOfferRecord newDataOffer(String connectorEndpoint, FetchedDataOffer fetchedDataOffer) {
        var dataOffer = new DataOfferRecord();
        dataOffer.setConnectorEndpoint(connectorEndpoint);
        dataOffer.setAssetId(fetchedDataOffer.getAssetId());
        dataOffer.setCreatedAt(OffsetDateTime.now());
        updateDataOffer(dataOffer, fetchedDataOffer, true);
        return dataOffer;
    }


    /**
     * Update existing {@link DataOfferRecord}.
     *
     * @param dataOffer        existing row
     * @param fetchedDataOffer changes to be incorporated
     * @param changed          whether the data offer should be marked as updated simply because the contract offers changed
     * @return whether any fields were updated
     */
    public boolean updateDataOffer(
            DataOfferRecord dataOffer,
            FetchedDataOffer fetchedDataOffer,
            boolean changed
    ) {
        changed |= updateField(
            dataOffer,
            fetchedDataOffer,
            FetchedDataOffer::getAssetTitle,
            DataOfferRecord::getAssetTitle,
            dataOffer::setAssetTitle
        );

        changed |= updateField(
            dataOffer,
            fetchedDataOffer,
            FetchedDataOffer::getDescription,
            DataOfferRecord::getDescription,
            dataOffer::setDescription
        );

        changed |= updateField(
            dataOffer,
            fetchedDataOffer,
            FetchedDataOffer::getCuratorOrganizationName,
            DataOfferRecord::getCuratorOrganizationName,
            dataOffer::setCuratorOrganizationName
        );

        changed |= updateField(
            dataOffer,
            fetchedDataOffer,
            FetchedDataOffer::getDataCategory,
            DataOfferRecord::getDataCategory,
            dataOffer::setDataCategory
        );

        changed |= updateField(
            dataOffer,
            fetchedDataOffer,
            FetchedDataOffer::getDataSubcategory,
            DataOfferRecord::getDataSubcategory,
            dataOffer::setDataSubcategory
        );

        changed |= updateField(
            dataOffer,
            fetchedDataOffer,
            FetchedDataOffer::getDataModel,
            DataOfferRecord::getDataModel,
            dataOffer::setDataModel
        );

        changed |= updateField(
            dataOffer,
            fetchedDataOffer,
            FetchedDataOffer::getTransportMode,
            DataOfferRecord::getTransportMode,
            dataOffer::setTransportMode
        );

        changed |= updateField(
            dataOffer,
            fetchedDataOffer,
            FetchedDataOffer::getGeoReferenceMethod,
            DataOfferRecord::getGeoReferenceMethod,
            dataOffer::setGeoReferenceMethod
        );

        changed |= updateKeywords(dataOffer, fetchedDataOffer);

        changed |= updateAssetJsonLd(dataOffer, fetchedDataOffer);

        if (changed) {
            dataOffer.setUpdatedAt(OffsetDateTime.now());
        }

        return changed;
    }

    private boolean updateField(
            DataOfferRecord dataOffer,
            FetchedDataOffer fetchedDataOffer,
            Function<FetchedDataOffer, String> fetchedField,
            Function<DataOfferRecord, String> existingField,
            Consumer<String> setter
    ) {
        var fetched = fetchedField.apply(fetchedDataOffer);
        if (fetched == null) {
            fetched = "";
        }

        var existing = existingField.apply(dataOffer);
        if (existing == null) {
            existing = "";
        }


        if (Objects.equals(fetched, existing)) {
            return false;
        }

        setter.accept(fetched);
        return true;
    }

    private boolean updateKeywords(
            DataOfferRecord dataOffer,
            FetchedDataOffer fetchedDataOffer
    ) {
        List<String> fetched = fetchedDataOffer.getKeywords();
        if (fetched == null) {
            fetched = List.of();
        }

        String[] existing = dataOffer.getKeywords();
        if (existing == null) {
            existing = new String[0];
        }

        if (Objects.equals(new HashSet<>(fetched), new HashSet<>(Arrays.asList(existing)))) {
            return false;
        }

        dataOffer.setKeywords(fetched.toArray(new String[0]));
        dataOffer.setKeywordsCommaJoined(String.join(",", fetched));
        return true;
    }

    private boolean updateAssetJsonLd(
            DataOfferRecord dataOffer,
            FetchedDataOffer fetchedDataOffer
    ) {
        String existing = JsonbUtils.getDataOrNull(dataOffer.getAssetJsonLd());
        var fetched = fetchedDataOffer.getAssetJsonLd();
        if (JsonUtils2.isEqualJson(fetched, existing)) {
            return false;
        }

        dataOffer.setAssetJsonLd(JSONB.jsonb(fetched));
        return true;
    }
}
