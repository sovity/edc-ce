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

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@Schema(description = "FilterExpression Criterion Literal")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogFilterExpressionLiteral {

    private CatalogFilterExpressionLiteralType type;

    @Schema(description = "Only for type VALUE. The single value representation.")
    private String value;

    @Schema(description = "Only for type VALUE_LIST. List of values, e.g. for the IN-Operator.")
    private List<String> valueList;

    public static CatalogFilterExpressionLiteral ofValue(@NonNull String value) {
        return new CatalogFilterExpressionLiteral(CatalogFilterExpressionLiteralType.VALUE, value, null);
    }

    public static CatalogFilterExpressionLiteral ofValueList(@NonNull List<String> valueList) {
        return new CatalogFilterExpressionLiteral(CatalogFilterExpressionLiteralType.VALUE_LIST, null, valueList);
    }
}
