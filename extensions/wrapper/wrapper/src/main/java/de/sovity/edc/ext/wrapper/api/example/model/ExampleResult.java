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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "This is the result of the example endpoint.")
public class ExampleResult {
    @Schema(description = "Some name field", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Some nested object. This documentation should disappear in favor of the " +
            "documentation of TestItem due to constraints of the OpenAPI file format.", requiredMode = Schema.RequiredMode.REQUIRED)
    private ExampleItem myNestedItem;

    @Schema(description = "Some array of nested objects", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ExampleItem> myNestedList;

    @Schema(description = "Configured IDS Endpoint", requiredMode = Schema.RequiredMode.REQUIRED)
    private String idsEndpoint;

    private ExampleEnum myEnum;
}

