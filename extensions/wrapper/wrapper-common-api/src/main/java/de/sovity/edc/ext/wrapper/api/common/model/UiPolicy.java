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
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
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
@Schema(description = "Type-Safe OpenAPI generator friendly Policy DTO as needed by our UI")
public class UiPolicy {
    @Schema(description = "EDC Policy JSON-LD. This is required because the EDC requires the " +
            "full policy when initiating contract negotiations.", requiredMode = RequiredMode.REQUIRED)
    private String policyJsonLd;

    @Schema(description = "Conjunction of required expressions for the policy to evaluate to TRUE.")
    private List<UiPolicyConstraint> constraints;

    @Schema(description = "When trying to reduce the policy JSON-LD to our opinionated subset of functionalities, " +
            "many fields and functionalities are unsupported. Should any discrepancies occur during " +
            "the mapping process, we'll collect them here.", requiredMode = RequiredMode.REQUIRED)
    private List<String> errors;
}
