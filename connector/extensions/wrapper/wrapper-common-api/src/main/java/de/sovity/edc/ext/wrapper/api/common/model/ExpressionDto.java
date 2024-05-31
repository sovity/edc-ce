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
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Expression constraints for policies.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExpressionDto {

    @Schema(description = """
            Expression types:
            * `EMPTY` - No constraints for the policy
            * `ATOMIC_CONSTRAINT` - A single constraint for the policy
            * `AND` - Several constraints, all of which must be respected
            * `OR` - Several constraints, of which at least one must be respected
            * `XOR` - Several constraints, of which exactly one must be respected
            """
    )
    private ExpressionType type;
    private AtomicConstraintDto atomicConstraint;
    private List<ExpressionDto> and;
    private List<ExpressionDto> or;
    private List<ExpressionDto> xor;
}
