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

import de.sovity.edc.ext.brokerserver.dao.models.LogEventRecord;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LogEventStore {
    private final Map<String, LogEventRecord> logEntries = new HashMap<>();

    public LogEventRecord save(@NonNull LogEventRecord logEntry) {
        Validate.isTrue(logEntry.getId() == null, "ID already set!");
        var updated = logEntry.toBuilder().id(UUID.randomUUID().toString()).build();
        logEntries.put(updated.getId(), updated);
        return updated;
    }

    public List<LogEventRecord> findAll() {
        return new ArrayList<>(logEntries.values());
    }
}
