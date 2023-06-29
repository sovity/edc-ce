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

import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
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
@Schema(description = "A contract offer a data offer is available under (as required by the data offer detail page).")
public class DataOfferDetailContractOffer {
    @Schema(description = "Contract Offer ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractOfferId;

    @Schema(description = "Creation date in Broker", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime createdAt;

    @Schema(description = "Update date in Broker", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime updatedAt;

    @Schema(description = "Contract Policy", requiredMode = Schema.RequiredMode.REQUIRED)
    private PolicyDto contractPolicy;
}

