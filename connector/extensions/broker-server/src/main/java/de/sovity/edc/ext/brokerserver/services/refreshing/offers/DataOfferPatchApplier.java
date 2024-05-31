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

import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.DataOfferPatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jooq.DSLContext;

@RequiredArgsConstructor
public class DataOfferPatchApplier {

    @SneakyThrows
    public void writeDataOfferPatch(DSLContext dsl, DataOfferPatch dataOfferPatch) {
        if (!dataOfferPatch.getDataOffersToUpdate().isEmpty()) {
            dsl.batchUpdate(dataOfferPatch.getDataOffersToUpdate()).execute();
        }
        if (!dataOfferPatch.getContractOffersToUpdate().isEmpty()) {
            dsl.batchUpdate(dataOfferPatch.getContractOffersToUpdate()).execute();
        }

        // insert: parent entity first
        if (!dataOfferPatch.getDataOffersToInsert().isEmpty()) {
            dsl.batchInsert(dataOfferPatch.getDataOffersToInsert()).execute();
        }
        if (!dataOfferPatch.getContractOffersToInsert().isEmpty()) {
            dsl.batchInsert(dataOfferPatch.getContractOffersToInsert()).execute();
        }

        // delete: child entity first
        if (!dataOfferPatch.getContractOffersToDelete().isEmpty()) {
            dsl.batchDelete(dataOfferPatch.getContractOffersToDelete()).execute();
        }
        if (!dataOfferPatch.getDataOffersToDelete().isEmpty()) {
            dsl.batchDelete(dataOfferPatch.getDataOffersToDelete()).execute();
        }
    }
}
