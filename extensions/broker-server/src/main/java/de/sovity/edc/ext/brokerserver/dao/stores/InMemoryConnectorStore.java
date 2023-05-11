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

package de.sovity.edc.ext.brokerserver.dao.stores;

import de.sovity.edc.ext.brokerserver.dao.models.ConnectorRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class InMemoryConnectorStore {
    private final Map<String, ConnectorRecord> connectorsById = new HashMap<>();

    public Stream<ConnectorRecord> findAll() {
        return connectorsById.values().stream();
    }

    public ConnectorRecord findById(String connectorId) {
        return connectorsById.get(connectorId);
    }

    public ConnectorRecord save(ConnectorRecord connector) {
        return connectorsById.put(connector.getId(), connector);
    }
}
