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

package de.sovity.edc.ext.wrapper.api.broker.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Attribute, e.g. Language")
public class CnfFilterAttribute {
    @Schema(description = "Attribute ID", example = "asset:prop:language", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;
    @Schema(description = "Attribute Title", example = "Language", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;
    @Schema(description = "Available values.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CnfFilterItem> values;
}

