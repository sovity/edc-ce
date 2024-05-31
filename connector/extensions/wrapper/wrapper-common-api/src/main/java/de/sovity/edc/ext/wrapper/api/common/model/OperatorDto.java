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
 * Equivalent of ODRL Policy Operator for our API Wrapper API.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@Schema(description = "Operator for policies", enumAsRef = true)
public enum OperatorDto {
    EQ,
    NEQ,
    GT,
    GEQ,
    LT,
    LEQ,
    IN,
    HAS_PART,
    IS_A,
    IS_ALL_OF,
    IS_ANY_OF,
    IS_NONE_OF
}
