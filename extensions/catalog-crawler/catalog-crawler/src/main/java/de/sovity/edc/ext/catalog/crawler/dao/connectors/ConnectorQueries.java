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

package de.sovity.edc.ext.catalog.crawler.dao.connectors;

import de.sovity.edc.ext.catalog.crawler.dao.utils.PostgresqlUtils;
import de.sovity.edc.ext.catalog.crawler.db.jooq.Tables;
import de.sovity.edc.ext.catalog.crawler.db.jooq.enums.ConnectorOnlineStatus;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.Connector;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.Organization;
import de.sovity.edc.ext.catalog.crawler.db.jooq.tables.records.ConnectorRecord;
import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.DSLContext;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class ConnectorQueries {

    public ConnectorRecord findByConnectorId(DSLContext dsl, String connectorId) {
        var c = Tables.CONNECTOR;
        return dsl.fetchOne(c, c.CONNECTOR_ID.eq(connectorId));
    }

    public Set<ConnectorRef> findConnectorsForScheduledRefresh(DSLContext dsl, ConnectorOnlineStatus onlineStatus) {
        return queryConnectorRefs(dsl, (c, o) -> c.ONLINE_STATUS.eq(onlineStatus));
    }

    public Set<ConnectorRef> findAllConnectorsForKilling(DSLContext dsl, Duration deleteOfflineConnectorsAfter) {
        var minLastRefresh = OffsetDateTime.now().minus(deleteOfflineConnectorsAfter);
        return queryConnectorRefs(dsl, (c, o) -> c.LAST_SUCCESSFUL_REFRESH_AT.lt(minLastRefresh));
    }

    @NotNull
    private HashSet<ConnectorRef> queryConnectorRefs(
            DSLContext dsl,
            BiFunction<Connector, Organization, Condition> condition
    ) {
        var c = Tables.CONNECTOR;
        var o = Tables.ORGANIZATION;
        var query = dsl.select(
                        c.ENVIRONMENT.as("environmentId"),
                        o.NAME.as("organizationLegalName"),
                        o.MDS_ID.as("organizationId"),
                        c.CONNECTOR_ID.as("connectorId"),
                        c.ENDPOINT_URL.as("endpoint")
                )
                .from(c)
                .leftJoin(o).on(c.MDS_ID.eq(o.MDS_ID))
                .where(condition.apply(c, o))
                .fetchInto(ConnectorRef.class);

        return new HashSet<>(query);
    }
}
