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
public class UiDataSourceHttpData {
    @Schema(
        description = "HTTP Request Method",
        defaultValue = "GET",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private UiDataSourceHttpDataMethod method;

    @Schema(
        description = "HTTP Request URL. If parameterized, additional pathParams will be joined onto existing one.",
        example = "https://my-app.my-org.com/api/edc-data-offer/v1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String baseUrl;

    @Schema(
        description = "HTTP Request Query Params / Query String.",
        example = "search=example&limit=10",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String queryString;

    @Schema(
        description = "HTTP Request Headers. HTTP Header Parameterization is not available.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Map<String, String> headers;

    @Schema(
        description = "Enable Method Parameterization. This forces consumers to provide" +
            " a method, otherwise the transfer will fail.",
        defaultValue = "false",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Boolean enableMethodParameterization;

    @Schema(
        description = "Enable Path Parameterization.",
        defaultValue = "false",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Boolean enablePathParameterization;

    @Schema(
        description = "Enable Query Parameterization. Any additionally provided queryString" +
            " will be merged with the existing one.",
        defaultValue = "false",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Boolean enableQueryParameterization;

    @Schema(
        description = "Enable Body Parameterization. Forces the provider to provide both a" +
            " request body and a content type. Only Methods POST, PUT and PATCH allow request bodies.",
        defaultValue = "false",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Boolean enableBodyParameterization;
}
