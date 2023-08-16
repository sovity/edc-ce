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

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@RequiredArgsConstructor
@Schema(description = "ODRL AtomicConstraint as supported by our UI")
public class UiPolicyConstraint {
    @Schema(description = "Left side of the expression.", requiredMode = RequiredMode.REQUIRED)
    private String left;

    @Schema(description = "Operator, e.g. EQ", requiredMode = RequiredMode.REQUIRED)
    private OperatorDto operator;

    @Schema(description = "Right side of the expression", requiredMode = RequiredMode.REQUIRED)
    private UiPolicyLiteral right;
}
