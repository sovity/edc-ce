/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.api.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Type-safe data offer metadata for editing an asset as supported by the sovity product landscape. Contains extension points.")
public class UiAssetEditRequest {
    @Schema(description = "Data Source", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UiDataSource dataSourceOverrideOrNull;

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

    @Schema(description = "Data Model", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dataModel;

    @Schema(description = "The sovereign is distinct from the publisher by being the legal owner of the data.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String sovereignLegalName;

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

    @Schema(description = "sphin-x dataspace specific asset metadata fields", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UiAssetExtForSphinx sphinxFields;

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
