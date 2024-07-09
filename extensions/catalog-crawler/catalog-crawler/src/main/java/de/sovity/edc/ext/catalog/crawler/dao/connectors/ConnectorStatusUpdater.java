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
import org.jooq.DSLContext;

import java.util.Collection;
import java.util.stream.Collectors;

public class ConnectorStatusUpdater {
    public void markAsDead(DSLContext dsl, Collection<ConnectorRef> connectorRefs) {
        var connectorIds = connectorRefs.stream()
                .map(ConnectorRef::getConnectorId)
                .collect(Collectors.toSet());
        var c = Tables.CONNECTOR;
        dsl.update(c).set(c.ONLINE_STATUS, ConnectorOnlineStatus.DEAD)
                .where(PostgresqlUtils.in(c.CONNECTOR_ID, connectorIds)).execute();
    }
}
