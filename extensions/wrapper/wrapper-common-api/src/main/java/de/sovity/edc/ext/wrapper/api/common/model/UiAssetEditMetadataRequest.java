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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data for editing an asset.")
public class UiAssetEditMetadataRequest {
    @Schema(description = "Asset Title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String title;

    @Schema(description = "Asset Language", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String language;

    @Schema(description = "Asset Description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Asset Homepage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String publisherHomepage;

    @Schema(description = "License URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String licenseUrl;

    @Schema(description = "Version", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String version;

    @Schema(description = "Asset Keywords", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> keywords;

    @Schema(description = "Asset MediaType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String mediaType;

    @Schema(description = "Landing Page URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String landingPageUrl;

    @Schema(description = "Data Category", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dataCategory;

    @Schema(description = "Data Subcategory", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dataSubcategory;

    @Schema(description = "Data Model", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dataModel;

    @Schema(description = "Geo-Reference Method", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String geoReferenceMethod;

    @Schema(description = "Transport Mode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String transportMode;

    @Schema(description = "Custom Asset Properties (that are strings)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, String> additionalProperties;

    @Schema(description = "Custom Asset Properties (that are not strings but other JSON values)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, String> additionalJsonProperties;

    @Schema(description = "Private Asset Properties (that are strings)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, String> privateProperties;

    @Schema(description = "Private Asset Properties (that are not strings but other JSON values)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, String> privateJsonProperties;
}
