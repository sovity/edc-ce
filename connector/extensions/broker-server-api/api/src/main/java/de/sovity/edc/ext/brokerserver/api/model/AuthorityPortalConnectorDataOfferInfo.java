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
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Details of a Connector and its data offers.")
public class AuthorityPortalConnectorDataOfferInfo {

    @Schema(description = "Connector Endpoint", example = "https://my-test.connector/api/dsp", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorEndpoint;

    @Schema(description = "ID of participant", requiredMode = Schema.RequiredMode.REQUIRED)
    private String participantId;

    @Schema(description = "Connector Online Status", requiredMode = Schema.RequiredMode.REQUIRED)
    private ConnectorOnlineStatus onlineStatus;

    @Schema(description = "Date to be displayed as last update date, for online connectors it's the last refresh date, for offline connectors it's the creation date or last successful fetch.")
    private OffsetDateTime offlineSinceOrLastUpdatedAt;

    @Schema(description = "Available Data Offers", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<AuthorityPortalConnectorDataOfferDetails> dataOffers;

}
