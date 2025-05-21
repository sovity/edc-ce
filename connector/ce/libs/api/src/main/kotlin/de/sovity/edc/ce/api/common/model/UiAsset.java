/*
 * Copyright 2025 sovity GmbH
 * Copyright 2023 Fraunhofer-Institut für Software- und Systemtechnik ISST
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
 *     Fraunhofer ISST - initial implementation of an unified workflow for creating data offerings
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
@Schema(description = "Type-safe data offer metadata as supported by the sovity product landscape. Contains extension points.")
public class UiAsset {
    @Schema(description = "'Live' vs 'On Request'", requiredMode = Schema.RequiredMode.REQUIRED)
    private DataSourceAvailability dataSourceAvailability;

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

    @Schema(
        description = "Contact E-Mail address. Only for dataSourceAvailability ON_REQUEST.",
        example = "contact@my-org.com",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String onRequestContactEmail;

    @Schema(
        description = "Contact Preferred E-Mail Subject. Only for dataSourceAvailability ON_REQUEST.",
        example = "Department XYZ Data Offer Request - My Product, My API",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String onRequestContactEmailSubject;

    @Schema(description = "Asset Language", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String language;

    @Schema(description = "Asset Description. Supports markdown.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Asset Description Short Text generated from description. Contains no markdown.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String descriptionShortText;

    @Schema(description = "Flag that indicates whether this asset is created by this connector.", requiredMode = Schema.RequiredMode.REQUIRED)
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

    @Schema(
        description = "HTTP Datasource Hint: Proxy Method. " +
            "Only for dataSourceAvailability LIVE with an underlying HTTP_DATA Data Source.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Boolean httpDatasourceHintsProxyMethod;

    @Schema(
        description = "HTTP Datasource Hint: Proxy Path. " +
            "Only for dataSourceAvailability LIVE with an underlying HTTP_DATA Data Source.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Boolean httpDatasourceHintsProxyPath;

    @Schema(
        description = "HTTP Datasource Hint: Proxy Query Params. " +
            "Only for dataSourceAvailability LIVE with an underlying HTTP_DATA Data Source.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Boolean httpDatasourceHintsProxyQueryParams;

    @Schema(
        description = "HTTP Datasource Hint: Proxy Body. " +
            "Only for dataSourceAvailability LIVE with an underlying HTTP_DATA Data Source.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Boolean httpDatasourceHintsProxyBody;

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

    @Schema(description = "Contains the entire asset in the JSON-LD format", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String assetJsonLd;

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
