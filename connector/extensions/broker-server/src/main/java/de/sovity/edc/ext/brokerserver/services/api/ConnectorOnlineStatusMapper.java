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

import de.sovity.edc.ext.brokerserver.api.model.ConnectorOnlineStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConnectorOnlineStatusMapper {

    public ConnectorOnlineStatus getOnlineStatus(de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus onlineStatus) {
        return switch (onlineStatus) {
            case ONLINE -> ConnectorOnlineStatus.ONLINE;
            case OFFLINE -> ConnectorOnlineStatus.OFFLINE;
            case DEAD -> ConnectorOnlineStatus.DEAD;
            default -> throw new IllegalStateException("Unknown ConnectorOnlineStatus from DAO for API: " + onlineStatus);
        };
    }
}
