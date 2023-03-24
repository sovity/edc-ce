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

package de.sovity.edc.ext.wrapper.api.example.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Example enum to test enum documentation.")
public enum ExampleEnum {
    @Schema(description = "This is the first enum value.")
    FIRST,
    @Schema(description = "This is the second enum value.")
    SECOND
}
