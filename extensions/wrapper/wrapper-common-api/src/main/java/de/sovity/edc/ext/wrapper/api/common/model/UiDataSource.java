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

package de.sovity.edc.ext.wrapper.api.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Type-safe data source as supported by the sovity product landscape. Contains extension points for using custom data address properties.")
public class UiDataSource {
    @Schema(
        description = "Data Address Type.",
        defaultValue = "CUSTOM",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private DataSourceType type;

    @Schema(
        description = "Only for type HTTP_DATA",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private UiDataSourceHttpData httpData;

    @Schema(
        description = "Only for type ON_REQUEST",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private UiDataSourceOnRequest onRequest;

    @Schema(
        description = "For all types. Custom Data Address Properties.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Map<String, String> customProperties;
}
