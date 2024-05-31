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

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Pagination Metadata")
public class PaginationMetadata {
    @Schema(description = "Total number of results", example = "368", type = "n", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer numTotal;

    @Schema(description = "Visible number of results", example = "20", type = "n", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer numVisible;

    @Schema(description = "Page number, one based, meaning the first page is page 1.", example = "1", defaultValue = "1", type = "n", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageOneBased;

    @Schema(description = "Items per page", example = "20", type = "n", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageSize;
}

