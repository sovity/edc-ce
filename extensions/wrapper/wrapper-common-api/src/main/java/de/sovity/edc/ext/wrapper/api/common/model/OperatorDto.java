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

/**
 * The set of supported expression operators. Not all operators may be supported for particular
 * expression types.
 * Copied from EDC policy-model.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@Schema(description = "Operator for constraints")
public enum OperatorDto {
    /**
     * Operator expressing equality of two operands.
     */
    EQ,
    /**
     * Operator expressing inequality of two operands.
     */
    NEQ,
    /**
     * Operator expressing left operand is greater than right operand.
     */
    GT,
    /**
     * Operator expressing left operand is greater or equal than to the right operand.
     */
    GEQ,
    /**
     * Operator expressing left operand is lesser than to the right operand.
     */
    LT,
    /**
     * Operator expressing left operand is lesser or equal than to the right operand.
     */
    LEQ,
    /**
     * Operator expressing left operand is contained in the right operand.
     */
    IN
}
