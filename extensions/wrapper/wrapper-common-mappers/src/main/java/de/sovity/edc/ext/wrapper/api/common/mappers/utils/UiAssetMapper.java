/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import de.sovity.edc.utils.jsonld.vocab.Prop.SovityDcatExt.HttpDatasourceHints;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static de.sovity.edc.ext.wrapper.api.common.mappers.utils.JsonBuilderUtils.addNonNull;
import static de.sovity.edc.ext.wrapper.api.common.mappers.utils.JsonBuilderUtils.addNonNullArray;
import static de.sovity.edc.ext.wrapper.api.common.mappers.utils.JsonBuilderUtils.addNonNullJsonValue;
import static de.sovity.edc.ext.wrapper.api.common.mappers.utils.JsonBuilderUtils.addNotBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;

@RequiredArgsConstructor
public class UiAssetMapper {
    private final EdcPropertyUtils edcPropertyUtils;
    private final AssetJsonLdUtils assetJsonLdUtils;
    private final MarkdownToTextConverter markdownToTextConverter;
    private final TextUtils textUtils;
    private final OwnConnectorEndpointService ownConnectorEndpointService;

    public UiAsset buildUiAsset(JsonObject assetJsonLd, String connectorEndpoint, String participantId) {
        var properties = JsonLdUtils.object(assetJsonLd, Prop.Edc.PROPERTIES);

        var uiAsset = new UiAsset();
        uiAsset.setAssetJsonLd(JsonUtils.toJson(JsonLdUtils.tryCompact(assetJsonLd)));

        var id = assetJsonLdUtils.getId(assetJsonLd);
        var title = assetJsonLdUtils.getTitle(assetJsonLd);

        var distribution = JsonLdUtils.object(properties, Prop.Dcat.DISTRIBUTION);
        uiAsset.setMediaType(JsonLdUtils.string(distribution, Prop.Dcat.MEDIATYPE));
        uiAsset.setDataSampleUrls(JsonLdUtils.stringList(distribution, Prop.Adms.SAMPLE));
        var rights = JsonLdUtils.object(distribution, Prop.Dcterms.RIGHTS);
        uiAsset.setConditionsForUse(JsonLdUtils.string(rights, Prop.Rdfs.LABEL));
        var mobilityDataStandard = JsonLdUtils.object(distribution, Prop.MobilityDcatAp.MOBILITY_DATA_STANDARD);
        uiAsset.setDataModel(JsonLdUtils.string(mobilityDataStandard, Prop.ID));
        var referenceFiles = JsonLdUtils.object(mobilityDataStandard, Prop.MobilityDcatAp.SCHEMA);
        uiAsset.setReferenceFileUrls(JsonLdUtils.stringList(referenceFiles, Prop.Dcat.DOWNLOAD_URL));
        uiAsset.setReferenceFilesDescription(JsonLdUtils.string(referenceFiles, Prop.Rdfs.LITERAL));


        var temporalCoverage = JsonLdUtils.object(properties, Prop.Dcterms.TEMPORAL);
        uiAsset.setTemporalCoverageFrom(JsonLdUtils.localDate(temporalCoverage, Prop.Dcat.START_DATE));
        uiAsset.setTemporalCoverageToInclusive(JsonLdUtils.localDate(temporalCoverage, Prop.Dcat.END_DATE));

        var spatial = JsonLdUtils.object(properties, Prop.Dcterms.SPATIAL);
        uiAsset.setGeoLocation(JsonLdUtils.string(spatial, Prop.Skos.PREF_LABEL));
        uiAsset.setNutsLocations(JsonLdUtils.stringList(spatial, Prop.Dcterms.IDENTIFIER));

        var mobilityTheme = JsonLdUtils.object(properties, Prop.MobilityDcatAp.MOBILITY_THEME);
        uiAsset.setDataCategory(JsonLdUtils.string(mobilityTheme, Prop.MobilityDcatAp.DataCategoryProps.DATA_CATEGORY));
        uiAsset.setDataSubcategory(JsonLdUtils.string(mobilityTheme, Prop.MobilityDcatAp.DataCategoryProps.DATA_SUBCATEGORY));

        var creator = JsonLdUtils.object(properties, Prop.Dcterms.CREATOR);
        var creatorOrganizationName = JsonLdUtils.string(creator, Prop.Foaf.NAME);
        creatorOrganizationName = isBlank(creatorOrganizationName) ? participantId : creatorOrganizationName;


        var description = JsonLdUtils.string(properties, Prop.Dcterms.DESCRIPTION);
        uiAsset.setAssetId(id);
        uiAsset.setConnectorEndpoint(connectorEndpoint);
        uiAsset.setParticipantId(participantId);
        uiAsset.setTitle(title);
        uiAsset.setLicenseUrl(JsonLdUtils.string(properties, Prop.Dcterms.LICENSE));
        uiAsset.setDescription(description);
        uiAsset.setDescriptionShortText(buildShortDescription(description));
        uiAsset.setIsOwnConnector(ownConnectorEndpointService.isOwnConnectorEndpoint(connectorEndpoint));
        uiAsset.setLanguage(JsonLdUtils.string(properties, Prop.Dcterms.LANGUAGE));
        uiAsset.setVersion(JsonLdUtils.string(properties, Prop.Dcat.VERSION));
        uiAsset.setLandingPageUrl(JsonLdUtils.string(properties, Prop.Dcat.LANDING_PAGE));
        uiAsset.setGeoReferenceMethod(JsonLdUtils.string(properties, Prop.MobilityDcatAp.GEO_REFERENCE_METHOD));
        uiAsset.setTransportMode(JsonLdUtils.string(properties, Prop.MobilityDcatAp.TRANSPORT_MODE));
        uiAsset.setSovereignLegalName(JsonLdUtils.string(properties, Prop.Dcterms.RIGHTS_HOLDER));
        uiAsset.setDataUpdateFrequency(JsonLdUtils.string(properties, Prop.Dcterms.ACCRUAL_PERIODICITY));
        uiAsset.setKeywords(JsonLdUtils.stringList(properties, Prop.Dcat.KEYWORDS));

        uiAsset.setHttpDatasourceHintsProxyMethod(JsonLdUtils.bool(properties, HttpDatasourceHints.METHOD));
        uiAsset.setHttpDatasourceHintsProxyPath(JsonLdUtils.bool(properties, HttpDatasourceHints.PATH));
        uiAsset.setHttpDatasourceHintsProxyQueryParams(JsonLdUtils.bool(properties, HttpDatasourceHints.QUERY_PARAMS));
        uiAsset.setHttpDatasourceHintsProxyBody(JsonLdUtils.bool(properties, HttpDatasourceHints.BODY));

        var publisher = JsonLdUtils.object(properties, Prop.Dcterms.PUBLISHER);
        uiAsset.setPublisherHomepage(JsonLdUtils.string(publisher, Prop.Foaf.HOMEPAGE));

        uiAsset.setCustomJsonAsString(JsonLdUtils.string(properties, Prop.SovityDcatExt.CUSTOM_JSON));

        uiAsset.setCreatorOrganizationName(creatorOrganizationName);

        // Additional / Remaining Properties
        // TODO: diff nested objects
        val remaining = removeHandledProperties(properties, List.of(
                // Implicitly handled / should be skipped if found
                Prop.ID,
                Prop.TYPE,
                Prop.CONTEXT,
                Prop.Edc.ID,
                Prop.Dcterms.IDENTIFIER,

                // Explicitly handled
                Prop.Dcat.DISTRIBUTION,
                Prop.Dcat.KEYWORDS,
                Prop.Dcat.LANDING_PAGE,
                Prop.Dcat.VERSION,
                Prop.Dcterms.CREATOR,
                Prop.Dcterms.DESCRIPTION,
                Prop.Dcterms.LANGUAGE,
                Prop.Dcterms.LICENSE,
                Prop.Dcterms.PUBLISHER,
                Prop.Dcterms.TITLE,
                Prop.MobilityDcatAp.GEO_REFERENCE_METHOD,
                Prop.MobilityDcatAp.TRANSPORT_MODE,
                Prop.Dcterms.TEMPORAL,
                Prop.Dcterms.SPATIAL,
                Prop.MobilityDcatAp.MOBILITY_THEME,
                Prop.Dcterms.RIGHTS_HOLDER,
                Prop.Dcterms.ACCRUAL_PERIODICITY,

                HttpDatasourceHints.BODY,
                HttpDatasourceHints.METHOD,
                HttpDatasourceHints.PATH,
                HttpDatasourceHints.QUERY_PARAMS,

                Prop.SovityDcatExt.CUSTOM_JSON
        ));

        // custom properties
        val serializedJsonLd = packAsJsonLdProperties(remaining);
        uiAsset.setCustomJsonLdAsString(serializedJsonLd);

        // Private Properties
        val privateProperties = getPrivateProperties(assetJsonLd);
        if (privateProperties != null) {
            val privateCustomJson = JsonLdUtils.string(privateProperties, Prop.SovityDcatExt.PRIVATE_CUSTOM_JSON);
            uiAsset.setPrivateCustomJsonAsString(privateCustomJson);

            val privateRemaining = removeHandledProperties(
                    privateProperties,
                    List.of(Prop.SovityDcatExt.PRIVATE_CUSTOM_JSON));
            val privateSerializedJsonLd = packAsJsonLdProperties(privateRemaining);
            uiAsset.setPrivateCustomJsonLdAsString(privateSerializedJsonLd);
        }

        return uiAsset;
    }

    private static String packAsJsonLdProperties(JsonObject remaining) {
        val customJsonLd = Json.createObjectBuilder();
        remaining.entrySet().stream()
                .filter(it -> !JsonLdUtils.isEmptyArray(it.getValue()) || !JsonLdUtils.isEmptyObject(it.getValue()))
                .forEach(it -> customJsonLd.add(it.getKey(), it.getValue()));
        val compacted = JsonLdUtils.tryCompact(customJsonLd.build());
        return JsonUtils.toJson(compacted);
    }

    @SneakyThrows
    @Nullable
    public JsonObject buildAssetJsonLd(
            UiAssetCreateRequest uiAssetCreateRequest,
            String organizationName
    ) {
        var properties = getAssetProperties(uiAssetCreateRequest, organizationName);
        var privateProperties = getAssetPrivateProperties(uiAssetCreateRequest);
        var dataAddress = getDataAddress(uiAssetCreateRequest);

        return Json.createObjectBuilder()
                .add(Prop.ID, uiAssetCreateRequest.getId())
                .add(Prop.TYPE, Prop.Edc.TYPE_ASSET)
                .add(Prop.Edc.PROPERTIES, properties)
                .add(Prop.Edc.PRIVATE_PROPERTIES, privateProperties)
                .add(Prop.Edc.DATA_ADDRESS, dataAddress)
                .build();
    }

    private JsonObjectBuilder getAssetProperties(
            UiAssetCreateRequest uiAssetCreateRequest,
            String organizationName
    ) {
        var properties = Json.createObjectBuilder();

        addNonNull(properties, Prop.Edc.ID, uiAssetCreateRequest.getId());
        addNonNull(properties, Prop.Dcterms.LICENSE, uiAssetCreateRequest.getLicenseUrl());
        addNonNull(properties, Prop.Dcterms.TITLE, uiAssetCreateRequest.getTitle());
        addNonNull(properties, Prop.Dcterms.DESCRIPTION, uiAssetCreateRequest.getDescription());
        addNonNull(properties, Prop.Dcterms.LANGUAGE, uiAssetCreateRequest.getLanguage());
        addNonNull(properties, Prop.Dcat.VERSION, uiAssetCreateRequest.getVersion());
        addNonNull(properties, Prop.Dcat.LANDING_PAGE, uiAssetCreateRequest.getLandingPageUrl());
        addNonNull(properties, Prop.MobilityDcatAp.GEO_REFERENCE_METHOD, uiAssetCreateRequest.getGeoReferenceMethod());
        addNonNull(properties, Prop.MobilityDcatAp.TRANSPORT_MODE, uiAssetCreateRequest.getTransportMode());
        addNonNull(properties, Prop.Dcterms.RIGHTS_HOLDER, uiAssetCreateRequest.getSovereignLegalName());
        addNonNull(properties, Prop.Dcterms.ACCRUAL_PERIODICITY, uiAssetCreateRequest.getDataUpdateFrequency());

        addNonNullArray(properties, Prop.Dcat.KEYWORDS, uiAssetCreateRequest.getKeywords());

        if (uiAssetCreateRequest.getPublisherHomepage() != null) {
            properties.add(Prop.Dcterms.PUBLISHER, Json.createObjectBuilder()
                    .add(Prop.Foaf.HOMEPAGE, uiAssetCreateRequest.getPublisherHomepage()));
        }

        properties.add(Prop.Dcterms.CREATOR, Json.createObjectBuilder()
                .add(Prop.Foaf.NAME, organizationName));

        var distribution = buildDistribution(uiAssetCreateRequest);
        if (distribution != null) {
            properties.add(Prop.Dcat.DISTRIBUTION, distribution);
        }

        if (uiAssetCreateRequest.getTemporalCoverageFrom() != null || uiAssetCreateRequest.getTemporalCoverageToInclusive() != null) {
            var temporal = Json.createObjectBuilder();
            addNonNull(temporal, Prop.Dcat.START_DATE, uiAssetCreateRequest.getTemporalCoverageFrom());
            addNonNull(temporal, Prop.Dcat.END_DATE, uiAssetCreateRequest.getTemporalCoverageToInclusive());
            properties.add(Prop.Dcterms.TEMPORAL, temporal);
        }

        var nutsLocations = uiAssetCreateRequest.getNutsLocations();
        if (uiAssetCreateRequest.getGeoLocation() != null || (nutsLocations != null && !nutsLocations.isEmpty())) {
            var spatial = Json.createObjectBuilder();
            addNonNull(spatial, Prop.Skos.PREF_LABEL, uiAssetCreateRequest.getGeoLocation());
            addNonNullArray(spatial, Prop.Dcterms.IDENTIFIER, uiAssetCreateRequest.getNutsLocations());
            properties.add(Prop.Dcterms.SPATIAL, spatial);
        }

        if (uiAssetCreateRequest.getDataCategory() != null || uiAssetCreateRequest.getDataSubcategory() != null) {
            var mobilityTheme = Json.createObjectBuilder();
            addNonNull(mobilityTheme, Prop.MobilityDcatAp.DataCategoryProps.DATA_CATEGORY, uiAssetCreateRequest.getDataCategory());
            addNonNull(mobilityTheme, Prop.MobilityDcatAp.DataCategoryProps.DATA_SUBCATEGORY, uiAssetCreateRequest.getDataSubcategory());
            properties.add(Prop.MobilityDcatAp.MOBILITY_THEME, mobilityTheme);
        }

        var dataAddress = uiAssetCreateRequest.getDataAddressProperties();
        if (dataAddress != null && dataAddress.get(Prop.Edc.TYPE).equals("HttpData")) {
            addNonNull(properties, HttpDatasourceHints.BODY, trueIfTrue(dataAddress, Prop.Edc.PROXY_BODY));
            addNonNull(properties, HttpDatasourceHints.PATH, trueIfTrue(dataAddress, Prop.Edc.PROXY_PATH));
            addNonNull(properties, HttpDatasourceHints.QUERY_PARAMS, trueIfTrue(dataAddress, Prop.Edc.PROXY_QUERY_PARAMS));
            addNonNull(properties, HttpDatasourceHints.METHOD, trueIfTrue(dataAddress, Prop.Edc.PROXY_METHOD));
        }

        addNonNull(properties, Prop.SovityDcatExt.CUSTOM_JSON, uiAssetCreateRequest.getCustomJsonAsString());
        val jsonLdStr = uiAssetCreateRequest.getCustomJsonLdAsString();
        if (jsonLdStr != null) {
            val jsonLd = JsonUtils.parseJsonObj(jsonLdStr);
            for (val e : jsonLd.entrySet()) {
                addNonNullJsonValue(properties, e.getKey(), e.getValue());
            }
        }

        return properties;
    }

    private JsonObjectBuilder getAssetPrivateProperties(UiAssetCreateRequest uiAssetCreateRequest) {
        var privateProperties = Json.createObjectBuilder();

        val privateJsonStr = uiAssetCreateRequest.getPrivateCustomJsonAsString();
        if (privateJsonStr != null) {
            addNonNull(
                    privateProperties,
                    Prop.SovityDcatExt.PRIVATE_CUSTOM_JSON,
                    privateJsonStr
            );
        }

        val privateJsonLdStr = uiAssetCreateRequest.getPrivateCustomJsonLdAsString();
        if (privateJsonLdStr != null) {
            val privateJsonLd = JsonUtils.parseJsonObj(privateJsonLdStr);
            privateJsonLd.forEach((k, v) -> addNonNullJsonValue(privateProperties, k, v));
        }

        return privateProperties;
    }

    private String trueIfTrue(Map<String, String> dataAddressProperties, String key) {
        return "true".equals(dataAddressProperties.get(key)) ? "true" : "false";
    }

    private JsonObjectBuilder getDataAddress(UiAssetCreateRequest uiAssetCreateRequest) {
        var props = edcPropertyUtils.toMapOfObject(uiAssetCreateRequest.getDataAddressProperties());
        return Json.createObjectBuilder()
                .add(Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS)
                .add(Prop.Edc.PROPERTIES, Json.createObjectBuilder(props));
    }

    private JsonObject removeHandledProperties(JsonObject properties, List<String> handledProperties) {
        var remaining = Json.createObjectBuilder(JsonLdUtils.tryCompact(properties));
        handledProperties.forEach(remaining::remove);
        return remaining.build();
    }

    private JsonObject getPrivateProperties(JsonObject assetJsonLd) {
        if (assetJsonLd.containsKey(Prop.Edc.PRIVATE_PROPERTIES)) {
            return JsonLdUtils.object(assetJsonLd, Prop.Edc.PRIVATE_PROPERTIES);
        } else if (assetJsonLd.containsKey("privateProperties")) {
            // Tests claim this path exists
            return JsonLdUtils.object(assetJsonLd, "privateProperties");
        } else {
            return JsonValue.EMPTY_JSON_OBJECT;
        }
    }

    private String buildShortDescription(String description) {
        if (description == null) {
            return null;
        }

        var text = markdownToTextConverter.extractText(description);
        return textUtils.abbreviate(text, 300);
    }

    private JsonObjectBuilder buildDistribution(UiAssetCreateRequest uiAssetCreateRequest) {
        var dataSampleUrls = uiAssetCreateRequest.getDataSampleUrls();
        var referenceFileUrls = uiAssetCreateRequest.getReferenceFileUrls();
        var hasRootLevelFields = uiAssetCreateRequest.getMediaType() != null
                || (dataSampleUrls != null && !dataSampleUrls.isEmpty());
        var hasRightsFields = uiAssetCreateRequest.getConditionsForUse() != null;
        var hasDataModelFields = uiAssetCreateRequest.getDataModel() != null
                && !uiAssetCreateRequest.getDataModel().isBlank();
        var hasReferenceFilesFields = (referenceFileUrls != null && !referenceFileUrls.isEmpty())
                || uiAssetCreateRequest.getReferenceFilesDescription() != null;

        if (!hasRootLevelFields && !hasRightsFields && !hasDataModelFields && !hasReferenceFilesFields) {
            return null;
        }

        var distribution = Json.createObjectBuilder();
        addNonNull(distribution, Prop.Dcat.MEDIATYPE, uiAssetCreateRequest.getMediaType());
        addNonNullArray(distribution, Prop.Adms.SAMPLE, uiAssetCreateRequest.getDataSampleUrls());

        if (hasRightsFields) {
            var rights = Json.createObjectBuilder();
            addNonNull(rights, Prop.Rdfs.LABEL, uiAssetCreateRequest.getConditionsForUse());
            distribution.add(Prop.Dcterms.RIGHTS, rights);
        }

        if (!hasDataModelFields && !hasReferenceFilesFields) {
            return distribution;
        }
        var mobilityDataStandard = Json.createObjectBuilder();
        addNotBlank(mobilityDataStandard, Prop.ID, uiAssetCreateRequest.getDataModel());

        if (hasReferenceFilesFields) {
            var referenceFiles = Json.createObjectBuilder();
            addNonNullArray(referenceFiles, Prop.Dcat.DOWNLOAD_URL, uiAssetCreateRequest.getReferenceFileUrls());
            addNonNull(referenceFiles, Prop.Rdfs.LITERAL, uiAssetCreateRequest.getReferenceFilesDescription());
            mobilityDataStandard.add(Prop.MobilityDcatAp.SCHEMA, referenceFiles);
        }
        distribution.add(Prop.MobilityDcatAp.MOBILITY_DATA_STANDARD, mobilityDataStandard);
        return distribution;
    }
}
