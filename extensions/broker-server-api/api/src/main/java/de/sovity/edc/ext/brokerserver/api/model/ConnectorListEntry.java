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
@Schema(description = "A Contract Offer's Connector Status")
public class ConnectorListEntry {
    @Schema(description = "Connector Participant ID", example = "my-test-connector", requiredMode = Schema.RequiredMode.REQUIRED)
    private String participantId;

    @Schema(description = "Connector Endpoint", example = "https://my-test.connector/api/dsp", requiredMode = Schema.RequiredMode.REQUIRED)
    private String endpoint;

    @Schema(description = "Name of the responsible organization", requiredMode = Schema.RequiredMode.REQUIRED)
    private String organizationName;

    @Schema(description = "Creation date in Broker", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime createdAt;

    @Schema(description = "Last time the connector was successfully refreshed.")
    private OffsetDateTime lastSuccessfulRefreshAt;

    @Schema(description = "Last time the connector was tried to be refreshed.")
    private OffsetDateTime lastRefreshAttemptAt;

    @Schema(description = "Connector Online Status", requiredMode = Schema.RequiredMode.REQUIRED)
    private ConnectorOnlineStatus onlineStatus;

    @Schema(description = "Number of known data offerings")
    private Integer numDataOffers;
}

