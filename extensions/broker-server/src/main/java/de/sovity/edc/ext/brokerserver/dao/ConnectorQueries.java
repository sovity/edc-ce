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

import de.sovity.edc.ext.brokerserver.dao.utils.PostgresqlUtils;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.ConnectorRecord;
import org.jooq.DSLContext;

import java.util.Collection;
import java.util.Set;

public class ConnectorQueries {

    public ConnectorRecord findByEndpoint(DSLContext dsl, String endpoint) {
        var c = Tables.CONNECTOR;
        return dsl.selectFrom(c).where(c.ENDPOINT.eq(endpoint)).fetchOne();
    }

    public Set<String> findConnectorsForScheduledRefresh(DSLContext dsl) {
        var c = Tables.CONNECTOR;
        return dsl.select(c.ENDPOINT).from(c).fetchSet(c.ENDPOINT);
    }

    public Set<String> findExistingConnectors(DSLContext dsl, Collection<String> connectorEndpoints) {
        var c = Tables.CONNECTOR;
        return dsl.select(c.ENDPOINT).from(c)
                .where(PostgresqlUtils.in(c.ENDPOINT, connectorEndpoints))
                .fetchSet(c.ENDPOINT);
    }
}
