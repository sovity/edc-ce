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
@Schema(description = "Filterable Connector Page Query")
public class ConnectorPageQuery {
    @Schema(description = "Search query")
    private String searchQuery;

    @Schema(description = "Sorting")
    private ConnectorPageSortingType sorting;

    @Schema(description = "Page number, one based, meaning the first page is page 1.", example = "1", defaultValue = "1", type = "n")
    private Integer pageOneBased;
}

