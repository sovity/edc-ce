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

import de.sovity.edc.ext.catalog.crawler.db.jooq.Tables;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.records.ContractOfferRecord;
import org.jooq.DSLContext;

import java.util.List;

public class ContractOfferQueries {

    public List<ContractOfferRecord> findByConnectorId(DSLContext dsl, String connectorId) {
        var co = Tables.CONTRACT_OFFER;
        return dsl.selectFrom(co).where(co.CONNECTOR_ID.eq(connectorId)).stream().toList();
    }
}
