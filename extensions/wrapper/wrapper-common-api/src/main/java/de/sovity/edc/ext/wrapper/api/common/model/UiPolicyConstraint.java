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

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "ODRL AtomicConstraint as supported by the sovity product landscape. For example 'a EQ b', 'c IN [d, e, f]'")
public class UiPolicyConstraint {
    @Schema(description = "Left side of the expression.", requiredMode = RequiredMode.REQUIRED)
    private String left;

    @Schema(description = "Operator, e.g. EQ", requiredMode = RequiredMode.REQUIRED)
    private OperatorDto operator;

    @Schema(description = "Right side of the expression", requiredMode = RequiredMode.REQUIRED)
    private UiPolicyLiteral right;
}
