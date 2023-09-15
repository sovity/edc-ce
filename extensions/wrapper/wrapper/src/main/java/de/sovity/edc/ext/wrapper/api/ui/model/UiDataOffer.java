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

package de.sovity.edc.ext.wrapper.api.ui.model;

import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Catalog Data Offer as required by the UI")
public class UiDataOffer {
    @Schema(description = "Connector Endpoint", requiredMode = Schema.RequiredMode.REQUIRED)
    private String endpoint;

    @Schema(description = "Participant ID. Required for initiating transfers.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String participantId;

    @Schema(description = "Asset Information", requiredMode = Schema.RequiredMode.REQUIRED)
    private UiAsset asset;

    @Schema(description = "Available Contract Offers", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<UiContractOffer> contractOffers;
}
