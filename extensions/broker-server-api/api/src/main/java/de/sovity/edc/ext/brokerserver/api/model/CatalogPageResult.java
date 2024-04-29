/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.ext.brokerserver.api.model;

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
@Schema(description = "Catalog Page and visible filters")
public class CatalogPageResult {
    @Schema(description = "Available filter options", requiredMode = Schema.RequiredMode.REQUIRED)
    private CnfFilter availableFilters;

    @Schema(description = "Available sorting options", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CatalogPageSortingItem> availableSortings;

    @Schema(description = "Pagination Metadata", requiredMode = Schema.RequiredMode.REQUIRED)
    private PaginationMetadata paginationMetadata;

    @Schema(description = "Current page of data offers", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CatalogDataOffer> dataOffers;
}

