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

package de.sovity.edc.ext.brokerserver.dao;

import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.DataOfferContractOfferRecord;
import org.jooq.DSLContext;

import java.util.List;

public class DataOfferContractOfferQueries {

    public List<DataOfferContractOfferRecord> findByConnectorEndpoint(DSLContext dsl, String connectorEndpoint) {
        var co = Tables.DATA_OFFER_CONTRACT_OFFER;
        return dsl.selectFrom(co).where(co.CONNECTOR_ENDPOINT.eq(connectorEndpoint)).stream().toList();
    }
}
