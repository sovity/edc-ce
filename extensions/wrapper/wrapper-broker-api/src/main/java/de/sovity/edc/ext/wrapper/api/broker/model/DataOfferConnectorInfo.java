/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.broker.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A Contract Offer's Connector Status")
public class DataOfferConnectorInfo {
    @Schema(description = "Connector Endpoint", example = "https://my-test.connector/control/ids/data", requiredMode = Schema.RequiredMode.REQUIRED)
    private String endpoint;

    @Schema(description = "Connector IDS Title", example = "My Test Connector", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "Connector IDS Description", example = "Some EDC Connector", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "Connector Online Status", requiredMode = Schema.RequiredMode.REQUIRED)
    private ConnectorOnlineStatus onlineStatus;

    @Schema(description = "Date to be displayed as last update date, for online connectors it's the last refresh date, for offline connectors it's the creation date or last successful fetch.")
    private OffsetDateTime offlineSinceOrLastUpdatedAt;
}

