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

import de.sovity.edc.ext.brokerserver.dao.ContractOfferQueries;
import de.sovity.edc.ext.brokerserver.dao.DataOfferQueries;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.ContractOfferRecord;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.DataOfferRecord;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.DataOfferPatch;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedContractOffer;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedDataOffer;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
public class DataOfferPatchBuilder {
    private final ContractOfferQueries contractOfferQueries;
    private final DataOfferQueries dataOfferQueries;
    private final DataOfferRecordUpdater dataOfferRecordUpdater;
    private final ContractOfferRecordUpdater contractOfferRecordUpdater;

    /**
     * Fetches existing data offers of given connector endpoint and compares them with fetched data offers.
     *
     * @param dsl               dsl
     * @param connectorEndpoint connector endpoint
     * @param fetchedDataOffers fetched data offers
     * @return change list / patch
     */
    public DataOfferPatch buildDataOfferPatch(
            DSLContext dsl,
            String connectorEndpoint,
            Collection<FetchedDataOffer> fetchedDataOffers
    ) {
        var patch = new DataOfferPatch();
        var dataOffers = dataOfferQueries.findByConnectorEndpoint(dsl, connectorEndpoint);
        var contractOffersByAssetId = contractOfferQueries.findByConnectorEndpoint(dsl, connectorEndpoint)
                .stream()
                .collect(groupingBy(ContractOfferRecord::getAssetId));

        var diff = DiffUtils.compareLists(
                dataOffers,
                DataOfferRecord::getAssetId,
                fetchedDataOffers,
                FetchedDataOffer::getAssetId
        );

        diff.added().forEach(fetched -> {
            var newRecord = dataOfferRecordUpdater.newDataOffer(connectorEndpoint, fetched);
            patch.insertDataOffer(newRecord);
            patchContractOffers(patch, newRecord, List.of(), fetched.getContractOffers());
        });

        diff.updated().forEach(match -> {
            var existing = match.existing();
            var fetched = match.fetched();

            // Update Contract Offers
            var contractOffers = contractOffersByAssetId.getOrDefault(existing.getAssetId(), List.of());
            var changed = patchContractOffers(patch, existing, contractOffers, fetched.getContractOffers());

            // Update Data Offer (and update updatedAt if contractOffers changed)
            changed = dataOfferRecordUpdater.updateDataOffer(existing, fetched, changed);

            if (changed) {
                patch.updateDataOffer(existing);
            }
        });

        diff.removed().forEach(dataOffer -> {
            patch.deleteDataOffer(dataOffer);
            var contractOffers = contractOffersByAssetId.getOrDefault(dataOffer.getAssetId(), List.of());
            contractOffers.forEach(patch::deleteContractOffer);
        });

        return patch;
    }

    private boolean patchContractOffers(
            DataOfferPatch patch,
            DataOfferRecord dataOffer,
            Collection<ContractOfferRecord> contractOffers,
            Collection<FetchedContractOffer> fetchedContractOffers
    ) {
        var hasUpdates = new AtomicBoolean(false);

        var diff = DiffUtils.compareLists(
                contractOffers,
                ContractOfferRecord::getContractOfferId,
                fetchedContractOffers,
                FetchedContractOffer::getContractOfferId
        );

        diff.added().forEach(fetched -> {
            var newRecord = contractOfferRecordUpdater.newContractOffer(dataOffer, fetched);
            patch.insertContractOffer(newRecord);
            hasUpdates.set(true);
        });

        diff.updated().forEach(match -> {
            var existing = match.existing();
            var fetched = match.fetched();

            if (contractOfferRecordUpdater.updateContractOffer(existing, fetched)) {
                patch.updateContractOffer(existing);
                hasUpdates.set(true);
            }
        });

        diff.removed().forEach(existing -> {
            patch.deleteContractOffer(existing);
            hasUpdates.set(true);
        });

        return hasUpdates.get();
    }
}
