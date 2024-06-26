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
@Schema(description =
        "Represents a single atomic constraint or a multiplicity constraint. The atomicConstraint" +
                " will be evaluated if the constraintType is ATOMIC_CONSTRAINT.")
public class Expression {

    @Schema(description = "Either ATOMIC_CONSTRAINT or one of the multiplicity constraint types.")
    private ExpressionType expressionType;

    @Schema(description =
            "List of policy elements that are evaluated according the expressionType.")
    private List<Expression> expressions;

    @Schema(description =
            "A single atomic constraint. Will be evaluated if the expressionType is set to " +
                    "ATOMIC_CONSTRAINT.")
    private AtomicConstraintDto atomicConstraint;
}
