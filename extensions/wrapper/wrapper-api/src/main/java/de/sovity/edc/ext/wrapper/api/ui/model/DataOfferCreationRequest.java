/*
 * Copyright (c) 2024 sovity GmbH
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

import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyExpression;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Request to create a data offer")
public class DataOfferCreationRequest {

    @Schema(description = "The asset to create", requiredMode = REQUIRED)
    private UiAssetCreateRequest uiAssetCreateRequest;

    @Schema(description = "Which policy to apply to this asset creation.", requiredMode = REQUIRED)
    private PolicyDefinitionChoiceEnum policy;

    @Schema(description = "Policy Expression.", requiredMode = NOT_REQUIRED)
    private UiPolicyExpression uiPolicyExpression;
}
