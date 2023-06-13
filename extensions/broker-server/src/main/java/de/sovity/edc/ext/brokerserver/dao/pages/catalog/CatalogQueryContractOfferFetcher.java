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

package de.sovity.edc.ext.brokerserver.dao.pages.catalog;

import de.sovity.edc.ext.brokerserver.dao.pages.catalog.models.ContractOfferRs;
import de.sovity.edc.ext.brokerserver.dao.utils.MultisetUtils;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import lombok.RequiredArgsConstructor;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.List;

@RequiredArgsConstructor
public class CatalogQueryContractOfferFetcher {

    /**
     * Query a data offer's contract offers.
     *
     * @param fields query fields
     * @return {@link Field} of {@link ContractOfferRs}s
     */
    public Field<List<ContractOfferRs>> getContractOffers(CatalogQueryFields fields) {
        var c = fields.getConnectorTable();
        var d = fields.getDataOfferTable();
        var co = Tables.DATA_OFFER_CONTRACT_OFFER;

        var query = DSL.select(
                co.CONTRACT_OFFER_ID,
                co.POLICY.cast(String.class).as("policyJson"),
                co.CREATED_AT,
                co.UPDATED_AT
        ).from(co).where(
                co.CONNECTOR_ENDPOINT.eq(c.ENDPOINT),
                co.ASSET_ID.eq(d.ASSET_ID)).orderBy(co.CREATED_AT.desc()
        );

        return MultisetUtils.multiset(query, ContractOfferRs.class);
    }
}
