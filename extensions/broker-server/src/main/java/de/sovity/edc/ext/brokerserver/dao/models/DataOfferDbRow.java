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

package de.sovity.edc.ext.brokerserver.dao.models;

import de.sovity.edc.ext.brokerserver.db.jooq.enums.ConnectorOnlineStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataOfferDbRow {
    String assetId;
    String connectorEndpoint;
    String connectorTitle;
    String connectorDescription;
    ConnectorOnlineStatus connectorOnlineStatus;
    String assetPropertiesJson;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
    OffsetDateTime offlineSinceOrLastUpdatedAt;
    List<DataOfferContractOfferDbRow> contractOffers;
}
