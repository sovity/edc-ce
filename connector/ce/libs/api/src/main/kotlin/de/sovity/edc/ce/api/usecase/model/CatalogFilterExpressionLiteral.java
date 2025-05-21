/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.api.usecase.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "FilterExpression Criterion Literal", deprecated = true)
@Deprecated
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
