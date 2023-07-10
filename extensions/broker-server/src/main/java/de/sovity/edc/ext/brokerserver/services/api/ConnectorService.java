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

package de.sovity.edc.ext.brokerserver.services.api;

import de.sovity.edc.ext.brokerserver.services.ConnectorCreator;
import de.sovity.edc.ext.brokerserver.services.queue.ConnectorQueue;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.Collection;
import java.util.Set;

import static de.sovity.edc.ext.brokerserver.db.jooq.Tables.CONNECTOR;

@RequiredArgsConstructor
public class ConnectorService {
    private final ConnectorCreator connectorCreator;
    private final ConnectorQueue connectorQueue;

    public void addConnectors(DSLContext dsl, Collection<String> connectorEndpoints, int priority) {
        connectorCreator.addConnectors(dsl, connectorEndpoints);
        connectorQueue.addAll(connectorEndpoints, priority);
    }

    public Set<String> getConnectorEndpoints(DSLContext dsl) {
        return dsl.select(CONNECTOR.ENDPOINT).from(CONNECTOR).fetchSet(CONNECTOR.ENDPOINT);
    }
}
