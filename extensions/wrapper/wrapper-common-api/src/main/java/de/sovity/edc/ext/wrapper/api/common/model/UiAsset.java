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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@RequiredArgsConstructor
@Schema(description = "Asset Details")
public class UiAsset {

    @Schema(description = "Asset Name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Asset Title", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "Asset Language", requiredMode = Schema.RequiredMode.REQUIRED)
    private String language;

    @Schema(description = "Asset Description", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "Asset Creator", requiredMode = Schema.RequiredMode.REQUIRED)
    private String creator;

    @Schema(description = "Asset Publisher", requiredMode = Schema.RequiredMode.REQUIRED)
    private String publisher;

    @Schema(description = "License URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String license;

    @Schema(description = "Version", requiredMode = Schema.RequiredMode.REQUIRED)
    private String version;

    @Schema(description = "Asset Keywords", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> keywords;

    @Schema(description = "Distribution", requiredMode = Schema.RequiredMode.REQUIRED)
    private String distribution;

    @Schema(description = "Landing Page URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String landingPage;

    @Schema(description = "HTTP Datasource Hints Proxy Method", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean httpDatasourceHintsProxyMethod;

    @Schema(description = "HTTP Datasource Hints Proxy Path", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean httpDatasourceHintsProxyPath;

    @Schema(description = "HTTP Datasource Hints Proxy Query Params", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean httpDatasourceHintsProxyQueryParams;

    @Schema(description = "HTTP Datasource Hints Proxy Body", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean httpDatasourceHintsProxyBody;

    @Schema(description = "Data Category", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dataCategory;

    @Schema(description = "Data Subcategory", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dataSubcategory;

    @Schema(description = "Data Model", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dataModel;

    @Schema(description = "Geo-Reference Method", requiredMode = Schema.RequiredMode.REQUIRED)
    private String geoReferenceMethod;

    @Schema(description = "Transport Mode", requiredMode = Schema.RequiredMode.REQUIRED)
    private String transportMode;

    @Schema(description = "Asset JsonLd", requiredMode = Schema.RequiredMode.REQUIRED)
    private String assetJsonLd;

    @Schema(description = "Asset additional Properties", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, String> additionalProperties;

    @Schema(description = "Asset Private Properties", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, String> privateProperties;

    @Schema(description = "Asset Json Properties", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, String> additionalJsonProperties;
}
