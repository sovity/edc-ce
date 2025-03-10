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
package de.sovity.edc.ce.api.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "ODRL constraint as supported by the sovity product landscape")
public class UiPolicyExpression {

    @Schema(description = "Expression type", requiredMode = Schema.RequiredMode.REQUIRED)
    private UiPolicyExpressionType type;

    @Schema(description = "Only for types AND, OR, XONE. List of sub-expressions " +
        "to be evaluated according to the expressionType.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<UiPolicyExpression> expressions;

    @Schema(description = "Only for type CONSTRAINT. A single constraint.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UiPolicyConstraint constraint;

    public static UiPolicyExpression empty() {
        return UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.EMPTY)
            .build();
    }

    public static UiPolicyExpression constraint(UiPolicyConstraint constraint) {
        return UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.CONSTRAINT)
            .constraint(constraint)
            .build();
    }

    public static UiPolicyExpression and(List<UiPolicyExpression> expressions) {
        return UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.AND)
            .expressions(expressions)
            .build();
    }

    public static UiPolicyExpression or(List<UiPolicyExpression> expressions) {
        return UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.OR)
            .expressions(expressions)
            .build();
    }

    public static UiPolicyExpression xone(List<UiPolicyExpression> expressions) {
        return UiPolicyExpression.builder()
            .type(UiPolicyExpressionType.XONE)
            .expressions(expressions)
            .build();
    }
}
