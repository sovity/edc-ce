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

package de.sovity.edc.ext.catalog.crawler.dao.contract_offers;

import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedContractOffer;
import de.sovity.edc.ext.catalog.crawler.crawling.writing.utils.ChangeTracker;
import de.sovity.edc.ext.catalog.crawler.dao.utils.JsonbUtils;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.records.ContractOfferRecord;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.records.DataOfferRecord;
import de.sovity.edc.ext.catalog.crawler.utils.JsonUtils2;
import lombok.RequiredArgsConstructor;
import org.jooq.JSONB;

import java.time.OffsetDateTime;

/**
 * Creates or updates {@link ContractOfferRecord} DB Rows.
 * <p>
 * (Or at least prepares them for batch inserts / updates)
 */
@RequiredArgsConstructor
public class ContractOfferRecordUpdater {

    /**
     * Create new {@link ContractOfferRecord} from {@link FetchedContractOffer}.
     *
     * @param dataOffer parent data offer db row
     * @param fetchedContractOffer fetched contract offer
     * @return new db row
     */
    public ContractOfferRecord newContractOffer(
            DataOfferRecord dataOffer,
            FetchedContractOffer fetchedContractOffer
    ) {
        var contractOffer = new ContractOfferRecord();

        contractOffer.setConnectorId(dataOffer.getConnectorId());
        contractOffer.setContractOfferId(fetchedContractOffer.getContractOfferId());
        contractOffer.setAssetId(dataOffer.getAssetId());
        contractOffer.setCreatedAt(OffsetDateTime.now());
        updateContractOffer(contractOffer, fetchedContractOffer);
        return contractOffer;
    }

    /**
     * Update existing {@link ContractOfferRecord} with changes from {@link FetchedContractOffer}.
     *
     * @param contractOffer existing row
     * @param fetchedContractOffer changes to be integrated
     * @return if anything was changed
     */
    public boolean updateContractOffer(
            ContractOfferRecord contractOffer,
            FetchedContractOffer fetchedContractOffer
    ) {
        var changes = new ChangeTracker();

        changes.setIfChanged(
                JsonbUtils.getDataOrNull(contractOffer.getUiPolicyJson()),
                fetchedContractOffer.getUiPolicyJson(),
                it -> contractOffer.setUiPolicyJson(JSONB.jsonb(it)),
                JsonUtils2::isEqualJson
        );

        if (changes.isChanged()) {
            contractOffer.setUpdatedAt(OffsetDateTime.now());
        }

        return changes.isChanged();
    }
}
