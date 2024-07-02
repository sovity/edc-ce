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
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.ContractOfferRecord;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.DataOfferRecord;
import de.sovity.edc.ext.brokerserver.services.api.DataOfferMappingUtils;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedContractOffer;
import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.JSONB;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Creates or updates {@link ContractOfferRecord} DB Rows.
 * <p>
 * (Or at least prepares them for batch inserts / updates)
 */
@RequiredArgsConstructor
public class ContractOfferRecordUpdater {

    private final DataOfferMappingUtils dataOfferMappingUtils;

    /**
     * Create new {@link ContractOfferRecord} from {@link FetchedContractOffer}.
     *
     * @param dataOffer            parent data offer db row
     * @param fetchedContractOffer fetched contract offer
     * @return new db row
     */
    public ContractOfferRecord newContractOffer(DataOfferRecord dataOffer, FetchedContractOffer fetchedContractOffer) {
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
     * @param contractOffer        existing row
     * @param fetchedContractOffer changes to be integrated
     * @return if anything was changed
     */
    public boolean updateContractOffer(ContractOfferRecord contractOffer, FetchedContractOffer fetchedContractOffer) {
        var existingPolicy = JsonbUtils.getDataOrNull(contractOffer.getPolicy());
        var fetchedPolicy = fetchedContractOffer.getPolicyJson();
        var changed = false;

        if (!Objects.equals(existingPolicy, fetchedPolicy)) {
            contractOffer.setPolicy(JSONB.jsonb(fetchedPolicy));
            changed = true;
        }

        if (changed) {
            contractOffer.setUpdatedAt(OffsetDateTime.now());
        }

        return changed;
    }
}
