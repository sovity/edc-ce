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

package de.sovity.edc.ext.wrapper.api.ui.model;

import de.sovity.edc.ext.wrapper.api.common.model.PolicyDefinitionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "All data for the policy definition page as required by the UI", requiredMode = Schema.RequiredMode.REQUIRED)
public class PolicyDefinitionPage {
    @Schema(description = "Policy Definition Entries", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PolicyDefinitionDto> policies;
}
