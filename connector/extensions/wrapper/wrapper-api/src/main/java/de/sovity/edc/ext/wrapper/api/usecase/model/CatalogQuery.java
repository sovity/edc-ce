/*
 *  Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.usecase.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Catalog query parameters")
public class CatalogQuery {
    @Schema(description = "Target EDC DSP endpoint URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorEndpoint;

    @Schema(description = "Limit the number of results", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer limit;

    @Schema(description = "Offset for returned results, e.g. start at result 2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer offset;

    @Schema(description = "Filter expressions for catalog filtering", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<CatalogFilterExpression> filterExpressions;
}
