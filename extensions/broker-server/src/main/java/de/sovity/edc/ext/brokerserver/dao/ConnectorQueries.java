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
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.ConnectorRecord;
import org.jooq.DSLContext;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConnectorQueries {

    public ConnectorRecord findByEndpoint(DSLContext dsl, String endpoint) {
        var c = Tables.CONNECTOR;
        return dsl.selectFrom(c).where(c.ENDPOINT_URL.eq(endpoint)).fetchOne();
    }

    public Set<String> findConnectorsForScheduledRefresh(DSLContext dsl, ConnectorOnlineStatus onlineStatus) {
        var c = Tables.CONNECTOR;
        return dsl.select(c.ENDPOINT_URL).from(c).where(c.ONLINE_STATUS.eq(onlineStatus)).fetchSet(c.ENDPOINT_URL);
    }

    public Set<String> findExistingConnectors(DSLContext dsl, Collection<String> connectorEndpoints) {
        var c = Tables.CONNECTOR;
        return dsl.select(c.ENDPOINT_URL).from(c)
                .where(PostgresqlUtils.in(c.ENDPOINT_URL, connectorEndpoints))
                .fetchSet(c.ENDPOINT_URL);
    }

    public List<String> findAllConnectorsForKilling(DSLContext dsl, Duration deleteOfflineConnectorsAfter) {
        var c = Tables.CONNECTOR;
        return dsl.select(c.ENDPOINT_URL).from(c)
                .where(c.LAST_SUCCESSFUL_REFRESH_AT.lt(OffsetDateTime.now().minus(deleteOfflineConnectorsAfter)))
                .fetch(c.ENDPOINT_URL);
    }

    public Map<String, String> getConnectorIdsByEndpointUrl(DSLContext dsl, Collection<String> endpoints) {
        return dsl.selectFrom(Tables.CONNECTOR)
            .where(PostgresqlUtils.in(Tables.CONNECTOR.ENDPOINT_URL, endpoints))
            .fetchMap(Tables.CONNECTOR.ENDPOINT_URL, Tables.CONNECTOR.CONNECTOR_ID);
    }
}
