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

package de.sovity.edc.ext.catalog.crawler.dao;

import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.catalog.crawler.dao.utils.PostgresqlUtils;
import de.sovity.edc.ext.catalog.crawler.db.jooq.Tables;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.Collection;

import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class CatalogCleaner {

    public void removeCatalogByConnectors(DSLContext dsl, Collection<ConnectorRef> connectorRefs) {
        var co = Tables.CONTRACT_OFFER;
        var d = Tables.DATA_OFFER;

        var connectorIds = connectorRefs.stream().map(ConnectorRef::getConnectorId).collect(toSet());

        dsl.deleteFrom(co).where(PostgresqlUtils.in(co.CONNECTOR_ID, connectorIds)).execute();
        dsl.deleteFrom(d).where(PostgresqlUtils.in(d.CONNECTOR_ID, connectorIds)).execute();
    }
}
