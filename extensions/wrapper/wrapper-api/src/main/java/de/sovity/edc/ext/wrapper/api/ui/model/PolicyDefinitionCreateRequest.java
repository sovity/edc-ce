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

import com.fasterxml.jackson.annotation.JsonInclude;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyCreateRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Deprecated
@Schema(description = "[Deprecated] Create a Policy Definition. Use PolicyDefinitionCreateDto", deprecated = true)
public class PolicyDefinitionCreateRequest {
    @Schema(description = "Policy Definition ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String policyDefinitionId;

    @Schema(description = "[Deprecated] Conjunction of constraints (simplified UiPolicyExpression)",
        requiredMode = Schema.RequiredMode.REQUIRED, deprecated = true)
    private UiPolicyCreateRequest policy;
}

