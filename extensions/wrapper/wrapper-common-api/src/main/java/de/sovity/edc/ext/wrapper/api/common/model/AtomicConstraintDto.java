/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.ext.wrapper.api.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description =
        "Type-Safe OpenAPI generator friendly Constraint DTO that supports an opinionated"
                + " subset of the original EDC Constraint Entity.")
public class AtomicConstraintDto {

    @Schema(description = "Left part of the constraint.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String leftExpression;
    @Schema(description = "Operator to connect both parts of the constraint.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private OperatorDto operator;
    @Schema(description = "Right part of the constraint.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String rightExpression;
}
