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

package de.sovity.edc.ext.brokerserver.services;

import de.sovity.edc.ext.brokerserver.dao.ConnectorQueries;
import de.sovity.edc.ext.brokerserver.dao.utils.PostgresqlUtils;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class ConnectorCleaner {

    private final ConnectorQueries connectorQueries;

    public void removeDataForDeadConnectors(DSLContext dsl, Collection<String> endpoints) {
        var doco = Tables.CONTRACT_OFFER;
        var dof = Tables.DATA_OFFER;

        var connectorIdsByEndpointUrl = connectorQueries.getConnectorIdsByEndpointUrl(dsl, endpoints);

        dsl.deleteFrom(doco).where(PostgresqlUtils.in(doco.CONNECTOR_ID, connectorIdsByEndpointUrl.values())).execute();
        dsl.deleteFrom(dof).where(PostgresqlUtils.in(dof.CONNECTOR_ID, connectorIdsByEndpointUrl.values())).execute();
    }
}
