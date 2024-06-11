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

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@RequiredArgsConstructor
@Schema(description = "Policy Creation Request Supporting Multiplicity Constraints.")
public class MultiplicityPolicyCreateRequest {

    @Schema(description = "Policy Definition ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String policyDefinitionId;

    @Schema(description = "List of required PolicyElements for the policy to evaluate to TRUE.")
    private List<PolicyElement> policyElements;

}
