
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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Data for creating a Contract Definition")
public class ContractDefinitionRequest {

    @Schema(description = "Contract Definition ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractDefinitionId;

    @Schema(description = "Contract Policy ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractPolicyId;

    @Schema(description = "Access Policy ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String accessPolicyId;

    @Schema(description = "List of Criteria for the contract", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<UiCriterion> assetSelector;
}
