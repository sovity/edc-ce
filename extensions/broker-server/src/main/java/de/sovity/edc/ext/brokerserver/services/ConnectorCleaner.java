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

import de.sovity.edc.ext.brokerserver.dao.utils.PostgresqlUtils;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import org.jooq.DSLContext;

import java.util.Collection;

public class ConnectorCleaner {
    public void removeDataForDeadConnectors(DSLContext dsl, Collection<String> endpoints) {
        var doco = Tables.DATA_OFFER_CONTRACT_OFFER;
        var dof = Tables.DATA_OFFER;
        dsl.deleteFrom(doco).where(PostgresqlUtils.in(doco.CONNECTOR_ENDPOINT, endpoints)).execute();
        dsl.deleteFrom(dof).where(PostgresqlUtils.in(dof.CONNECTOR_ENDPOINT, endpoints)).execute();
    }
}
