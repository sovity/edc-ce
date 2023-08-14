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

package de.sovity.edc.ext.wrapper.api.ui.model;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Available operators for assetSelector in the Core EDC")
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
