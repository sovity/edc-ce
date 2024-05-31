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
import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import org.jooq.DSLContext;

import java.util.Collection;

public class ConnectorKiller {
    public void killConnectors(DSLContext dsl, Collection<String> endpoints) {
        var c = Tables.CONNECTOR;
        dsl.update(c).set(c.ONLINE_STATUS, ConnectorOnlineStatus.DEAD).where(PostgresqlUtils.in(c.ENDPOINT, endpoints)).execute();
    }
}
