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

import de.sovity.edc.ext.wrapper.api.common.model.AssetDto;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Contract Offer")
public class ContractOfferDto {
    @Schema(description = "Connector Information", requiredMode = Schema.RequiredMode.REQUIRED)
    private ContractOfferConnectorInfo connectorInfo;
    @Schema(description = "Asset", requiredMode = Schema.RequiredMode.REQUIRED)
    private AssetDto asset;

    @Schema(description = "Policy", requiredMode = Schema.RequiredMode.REQUIRED)
    private PolicyDto policy;
}

