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
@Schema(description = "Type-Safe Asset Metadata as needed by our UI")
public class UiAsset {

    @Schema(description = "Asset Id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String assetId;

    @Schema(description = "Providing Connector's Connector Endpoint", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorEndpoint;

    @Schema(description = "Providing Connector's Participant ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String participantId;

    @Schema(description = "Asset Title", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "Asset Organization Name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String creatorOrganizationName;

    @Schema(description = "Asset Language", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String language;

    @Schema(description = "Asset Description. Supports markdown.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Asset Description Short Text generated from description. Contains no markdown.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String descriptionShortText;

    @Schema(description = "Flag that indicates whether the Connector is owned by you.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isOwnConnector;

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

    @Schema(description = "Homepage URL associated with the Asset", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String landingPageUrl;

    @Schema(description = "HTTP Datasource Hints Proxy Method", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean httpDatasourceHintsProxyMethod;

    @Schema(description = "HTTP Datasource Hints Proxy Path", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean httpDatasourceHintsProxyPath;

    @Schema(description = "HTTP Datasource Hints Proxy Query Params", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean httpDatasourceHintsProxyQueryParams;

    @Schema(description = "HTTP Datasource Hints Proxy Body", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean httpDatasourceHintsProxyBody;

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

    @Schema(description = "Unhandled Asset Properties (that were strings)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, String> additionalProperties;

    @Schema(description = "Unhandled Asset Properties (that were not strings but other JSON values)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, String> additionalJsonProperties;

    @Schema(description = "Private Asset Properties (that were strings)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, String> privateProperties;

    @Schema(description = "Private Asset Properties (that were not strings but other JSON values)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, String> privateJsonProperties;

    @Schema(description = "Contains the entire asset in the JSON-LD format", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String assetJsonLd;
}
