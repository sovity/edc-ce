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

import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.services.ConnectorCreator;
import de.sovity.edc.ext.brokerserver.services.queue.ConnectorQueue;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableField;

import java.util.Collection;
import java.util.Set;

@RequiredArgsConstructor
public class ConnectorService {
    private final ConnectorCreator connectorCreator;
    private final ConnectorQueue connectorQueue;

    public void addConnectors(DSLContext dsl, Collection<String> connectorEndpoints, int priority) {
        connectorCreator.addConnectors(dsl, connectorEndpoints);
        connectorQueue.addAll(connectorEndpoints, priority);
    }

    public void deleteConnectors(DSLContext dsl, Collection<String> endpoints) {
        removeConnectorRows(dsl, Tables.BROKER_EXECUTION_TIME_MEASUREMENT.CONNECTOR_ENDPOINT, endpoints);
        removeConnectorRows(dsl, Tables.DATA_OFFER_CONTRACT_OFFER.CONNECTOR_ENDPOINT, endpoints);
        removeConnectorRows(dsl, Tables.DATA_OFFER.CONNECTOR_ENDPOINT, endpoints);
        removeConnectorRows(dsl, Tables.DATA_OFFER_VIEW_COUNT.CONNECTOR_ENDPOINT, endpoints);
        removeConnectorRows(dsl, Tables.CONNECTOR.ENDPOINT, endpoints);
    }

    public Set<String> getConnectorEndpoints(DSLContext dsl) {
        return dsl.select(Tables.CONNECTOR.ENDPOINT).from(Tables.CONNECTOR).fetchSet(Tables.CONNECTOR.ENDPOINT);
    }

    private <T extends Record> void removeConnectorRows(
            DSLContext dsl,
            TableField<T, String> endpointField,
            Collection<String> endpoints
    ) {
        dsl.deleteFrom(endpointField.getTable()).where(endpointField.in(endpoints)).execute();
    }
}
