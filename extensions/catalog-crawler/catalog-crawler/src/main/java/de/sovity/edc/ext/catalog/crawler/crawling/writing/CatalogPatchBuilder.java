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

package de.sovity.edc.ext.catalog.crawler.crawling.writing;

import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedContractOffer;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedDataOffer;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.utils.DiffUtils;
import de.sovity.edc.ext.catalog.crawler.dao.CatalogPatch;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.dao.contract_offers.ContractOfferQueries;
import de.sovity.edc.ext.catalog.crawler.dao.contract_offers.ContractOfferRecordUpdater;
import de.sovity.edc.ext.catalog.crawler.dao.data_offers.DataOfferQueries;
import de.sovity.edc.ext.catalog.crawler.dao.data_offers.DataOfferRecordUpdater;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.records.ContractOfferRecord;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.records.DataOfferRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
public class CatalogPatchBuilder {
    private final ContractOfferQueries contractOfferQueries;
    private final DataOfferQueries dataOfferQueries;
    private final DataOfferRecordUpdater dataOfferRecordUpdater;
    private final ContractOfferRecordUpdater contractOfferRecordUpdater;

    /**
     * Fetches existing data offers of given connector endpoint and compares them with fetched data offers.
     *
     * @param dsl dsl
     * @param connectorRef connector
     * @param fetchedDataOffers fetched data offers
     * @return change list / patch
     */
    public CatalogPatch buildDataOfferPatch(
            DSLContext dsl,
            ConnectorRef connectorRef,
            Collection<FetchedDataOffer> fetchedDataOffers
    ) {
        var patch = new CatalogPatch();
        var dataOffers = dataOfferQueries.findByConnectorId(dsl, connectorRef.getConnectorId());
        var contractOffersByAssetId = contractOfferQueries.findByConnectorId(dsl, connectorRef.getConnectorId())
                .stream()
                .collect(groupingBy(ContractOfferRecord::getAssetId));

        var diff = DiffUtils.compareLists(
                dataOffers,
                DataOfferRecord::getAssetId,
                fetchedDataOffers,
                FetchedDataOffer::getAssetId
        );

        diff.added().forEach(fetched -> {
            var newRecord = dataOfferRecordUpdater.newDataOffer(connectorRef, fetched);
            patch.dataOffers().insert(newRecord);
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
                patch.dataOffers().update(existing);
            }
        });

        diff.removed().forEach(dataOffer -> {
            patch.dataOffers().delete(dataOffer);
            var contractOffers = contractOffersByAssetId.getOrDefault(dataOffer.getAssetId(), List.of());
            contractOffers.forEach(it -> patch.contractOffers().delete(it));
        });

        return patch;
    }

    private boolean patchContractOffers(
            CatalogPatch patch,
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
            patch.contractOffers().insert(newRecord);
            hasUpdates.set(true);
        });

        diff.updated().forEach(match -> {
            var existing = match.existing();
            var fetched = match.fetched();

            if (contractOfferRecordUpdater.updateContractOffer(existing, fetched)) {
                patch.contractOffers().update(existing);
                hasUpdates.set(true);
            }
        });

        diff.removed().forEach(existing -> {
            patch.contractOffers().delete(existing);
            hasUpdates.set(true);
        });

        return hasUpdates.get();
    }
}
