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
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Offer, meaning an offered asset.")
public class CatalogDataOffer {
    @Schema(description = "ID of asset", requiredMode = Schema.RequiredMode.REQUIRED)
    private String assetId;

    @Schema(description = "Connector Endpoint", example = "https://my-test.connector/control/ids/data", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorEndpoint;

    @Schema(description = "Connector Online Status", requiredMode = Schema.RequiredMode.REQUIRED)
    private ConnectorOnlineStatus connectorOnlineStatus;

    @Schema(description = "Date to be displayed as last update date, for online connectors it's the last refresh date, for offline connectors it's the creation date or last successful fetch.")
    private OffsetDateTime connectorOfflineSinceOrLastUpdatedAt;

    @Schema(description = "Creation date in Broker", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime createdAt;

    @Schema(description = "Update date in Broker", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime updatedAt;

    @Schema(description = "Asset properties", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, String> properties;

    @Schema(description = "Available Contract Offers", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CatalogContractOffer> contractOffers;
}

