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
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.DataOfferRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.List;

@RequiredArgsConstructor
public class DataOfferQueries {

    public List<DataOfferRecord> findByConnectorEndpoint(DSLContext dsl, String connectorEndpoint) {
        var d = Tables.DATA_OFFER;
        return dsl.selectFrom(d).where(d.CONNECTOR_ENDPOINT.eq(connectorEndpoint)).stream().toList();
    }

}
