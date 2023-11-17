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

package de.sovity.edc.ext.brokerserver.dao.pages.connector.model;

import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConnectorDetailsRs {
    String endpoint;
    String participantId;
    OffsetDateTime createdAt;
    OffsetDateTime lastSuccessfulRefreshAt;
    OffsetDateTime lastRefreshAttemptAt;
    ConnectorOnlineStatus onlineStatus;
    Integer numDataOffers;
    Long connectorCrawlingTimeAvg;
}
