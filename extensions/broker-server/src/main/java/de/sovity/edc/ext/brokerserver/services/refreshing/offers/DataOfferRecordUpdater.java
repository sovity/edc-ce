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

import de.sovity.edc.ext.brokerserver.dao.queries.utils.JsonbUtils;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.DataOfferRecord;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedDataOffer;
import lombok.RequiredArgsConstructor;
import org.jooq.JSONB;

import java.time.OffsetDateTime;
import java.util.Objects;

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
    public boolean updateDataOffer(DataOfferRecord dataOffer, FetchedDataOffer fetchedDataOffer, boolean changed) {
        String existingAssetProps = JsonbUtils.getDataOrNull(dataOffer.getAssetProperties());
        var fetchedAssetProps = fetchedDataOffer.getAssetPropertiesJson();
        if (!Objects.equals(fetchedAssetProps, existingAssetProps)) {
            dataOffer.setAssetProperties(JSONB.jsonb(fetchedAssetProps));
            changed = true;
        }

        if (changed) {
            dataOffer.setUpdatedAt(OffsetDateTime.now());
        }

        return changed;
    }
}
