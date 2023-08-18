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

package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Opinionated subset of the EDC policy for our EDC UI.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@RequiredArgsConstructor
@Schema(description = "Data for creating a Policy Definition")
public class PolicyDefinitionCreateRequest {
    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String policyDefinitionId;

    @Schema(description = "UIPolicy Create Dto", requiredMode = Schema.RequiredMode.REQUIRED)
    private UiPolicyCreateRequest uiPolicyDto;
}

