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

import de.sovity.edc.ext.brokerserver.services.logging.ConnectorChangeTracker;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedDataOffer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jooq.DSLContext;

import java.util.Collection;

@RequiredArgsConstructor
public class DataOfferWriter {
    private final DataOfferPatchBuilder dataOfferPatchBuilder;
    private final DataOfferPatchApplier dataOfferPatchApplier;

    /**
     * Updates a connector's data offers with given {@link FetchedDataOffer}s.
     *
     * @param dsl               dsl
     * @param connectorEndpoint connector endpoint
     * @param fetchedDataOffers fetched data offers
     * @param changes           change tracker for log message
     */
    @SneakyThrows
    public void updateDataOffers(DSLContext dsl, String connectorEndpoint, Collection<FetchedDataOffer> fetchedDataOffers, ConnectorChangeTracker changes) {
        var patch = dataOfferPatchBuilder.buildDataOfferPatch(dsl, connectorEndpoint, fetchedDataOffers);
        changes.setNumOffersAdded(patch.getDataOffersToInsert().size());
        changes.setNumOffersUpdated(patch.getDataOffersToUpdate().size());
        changes.setNumOffersDeleted(patch.getDataOffersToDelete().size());
        dataOfferPatchApplier.writeDataOfferPatch(dsl, patch);
    }
}
