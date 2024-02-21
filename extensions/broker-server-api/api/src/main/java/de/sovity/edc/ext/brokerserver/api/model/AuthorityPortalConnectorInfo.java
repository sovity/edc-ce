/*
 *  Copyright (c) 2024 sovity GmbH
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
package de.sovity.edc.ext.brokerserver.api.model;

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
@Schema(description = "Information for one connector, as required for the Authority Portal.", requiredMode = Schema.RequiredMode.REQUIRED)
public class AuthorityPortalConnectorInfo {
    @Schema(description = "Connector Endpoint", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorEndpoint;
    @Schema(description = "Connector Participant ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String participantId;
    @Schema(description = "Number of public Data Offers in this connector, as tracked by the broker", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer dataOfferCount;
    @Schema(description = "Connector Online Status", requiredMode = Schema.RequiredMode.REQUIRED)
    private ConnectorOnlineStatus onlineStatus;
    @Schema(description = "Last successful refresh time stamp of the online status", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime offlineSinceOrLastUpdatedAt;
}
