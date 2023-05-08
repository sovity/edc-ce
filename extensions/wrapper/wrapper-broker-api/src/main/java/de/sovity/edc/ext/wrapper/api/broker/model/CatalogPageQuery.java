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

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filterable Catalog Page Query")
public class CatalogPageQuery {
    @Schema(description = "Selected filters")
    private CnfFilterValue filter;

    @Schema(description = "Search query")
    private String searchQuery;

    @Schema(description = "Sorting")
    private CatalogPageSortingType sorting;

    @Schema(description = "Page number, one based, meaning the first page is page 1.", example = "1", defaultValue = "1", type = "n")
    private Integer pageOneBased;
}

