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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Type-Safe OpenAPI generator friendly Asset Create DTO that supports an opinionated subset of the original EDC Asset Entity.")
public class UiAssetCreateRequest {
    @Schema(description = "Asset Id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

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

    @Schema(description = "The sovereign is distinct from the publisher by being the legal owner of the data.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String sovereignLegalName;

    @Schema(description = "Geo location", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String geoLocation;

    @Schema(description = "Locations by NUTS standard which divides countries into administrative divisions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> nutsLocation;

    @Schema(description = "Data sample URLs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> dataSampleUrls;

    @Schema(description = "Reference file/schema URLs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> referenceFileUrls;

    @Schema(description = "Additional information on reference files/schemas", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String referenceFilesDescription;

    @Schema(description = "Instructions for use that are not legally relevant e.g. information on how to cite the dataset in papers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String conditionsForUse;

    @Schema(description = "Data update frequency", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dataUpdateFrequency;

    @Schema(description = "Temporal coverage start date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate temporalCoverageFrom;

    @Schema(description = "Temporal coverage end date (inclusive)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate temporalCoverageToInclusive;

    @Schema(description = "Data Address", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, String> dataAddressProperties;

    @Schema(description = "Contains serialized custom properties in the JSON format.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String customJsonAsString;

    @Schema(description = "Contains serialized custom properties in the JSON LD format. " +
            "Contrary to the customJsonAsString field, this string must represent a JSON LD object " +
            "and will be affected by JSON LD compaction and expansion. " +
            "Due to a technical limitation, the properties can't be booleans.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String customJsonLdAsString;

    @Schema(description = "Same as customJsonAsString but the data will be stored in the private properties.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String privateCustomJsonAsString;

    @Schema(description = "Same as customJsonLdAsString but the data will be stored in the private properties. " +
            "The same limitations apply.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String privateCustomJsonLdAsString;
}
