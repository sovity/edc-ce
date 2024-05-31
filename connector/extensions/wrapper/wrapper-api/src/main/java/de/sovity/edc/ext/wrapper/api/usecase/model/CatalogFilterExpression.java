/*
 *  Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.usecase.model;

import de.sovity.edc.utils.jsonld.vocab.Prop;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Generic expression for filtering the data offers in the catalog", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
public class CatalogFilterExpression {
    @Schema(description = "Asset property name", requiredMode = Schema.RequiredMode.REQUIRED, example = Prop.Edc.ASSET_ID)
    private String operandLeft;

    @Schema(description = "Operator", requiredMode = Schema.RequiredMode.REQUIRED)
    private CatalogFilterExpressionOperator operator;

    @Schema(description = "Right Operand", requiredMode = Schema.RequiredMode.REQUIRED)
    private CatalogFilterExpressionLiteral operandRight;
}
