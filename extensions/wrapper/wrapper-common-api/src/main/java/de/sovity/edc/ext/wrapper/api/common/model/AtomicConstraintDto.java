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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Opinionated DTO of an EDC Constraint for permissions.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@Getter
@Setter
@Builder(toBuilder = true)
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
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
