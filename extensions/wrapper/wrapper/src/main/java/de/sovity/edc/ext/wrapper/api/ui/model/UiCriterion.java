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
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Contract Definition Criterion as supported by the UI")
public class UiCriterion {
    @Schema(description = "Left Operand", requiredMode = Schema.RequiredMode.REQUIRED)
    private String operandLeft;

    @Schema(description = "Operator", requiredMode = Schema.RequiredMode.REQUIRED)
    private UiCriterionOperator operator;

    @Schema(description = "Right Operand", requiredMode = Schema.RequiredMode.REQUIRED)
    private UiCriterionLiteral operandRight;
}
