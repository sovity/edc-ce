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

package de.sovity.edc.ext.brokerserver.services.refreshing.offers.model;

import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.DataOfferContractOfferRecord;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.DataOfferRecord;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains planned DB Row changes to be applied as batch.
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataOfferPatch {
    List<DataOfferRecord> dataOffersToInsert = new ArrayList<>();
    List<DataOfferRecord> dataOffersToUpdate = new ArrayList<>();
    List<DataOfferRecord> dataOffersToDelete = new ArrayList<>();

    List<DataOfferContractOfferRecord> contractOffersToInsert = new ArrayList<>();
    List<DataOfferContractOfferRecord> contractOffersToUpdate = new ArrayList<>();
    List<DataOfferContractOfferRecord> contractOffersToDelete = new ArrayList<>();

    public void insertDataOffer(DataOfferRecord offer) {
        dataOffersToInsert.add(offer);
    }

    public void updateDataOffer(DataOfferRecord offer) {
        dataOffersToUpdate.add(offer);
    }

    public void deleteDataOffer(DataOfferRecord offer) {
        dataOffersToDelete.add(offer);
    }

    public void insertContractOffer(DataOfferContractOfferRecord offer) {
        contractOffersToInsert.add(offer);
    }

    public void updateContractOffer(DataOfferContractOfferRecord offer) {
        contractOffersToUpdate.add(offer);
    }

    public void deleteContractOffer(DataOfferContractOfferRecord offer) {
        contractOffersToDelete.add(offer);
    }
}
