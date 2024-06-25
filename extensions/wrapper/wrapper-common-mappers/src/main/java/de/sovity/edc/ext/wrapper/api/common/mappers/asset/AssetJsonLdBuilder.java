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

package de.sovity.edc.ext.wrapper.api.common.mappers.asset;

import de.sovity.edc.ext.wrapper.api.common.mappers.dataaddress.DataSourceMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetEditRequest;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.apicatalog.jsonld.StringUtils.isBlank;
import static com.apicatalog.jsonld.StringUtils.isNotBlank;
import static de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.JsonBuilderUtils.addNonNull;
import static de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.JsonBuilderUtils.addNonNullJsonValue;
import static de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.JsonBuilderUtils.addNotBlank;
import static de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.JsonBuilderUtils.addNotBlankStringArray;

@RequiredArgsConstructor
public class AssetJsonLdBuilder {
    private final DataSourceMapper dataSourceMapper;
    private final AssetJsonLdParser assetJsonLdParser;
    private final AssetEditRequestMapper assetEditRequestMapper;


    @SneakyThrows
    @Nullable
    public JsonObject createAssetJsonLd(
        UiAssetCreateRequest createRequest,
        String organizationName
    ) {
        var dataSourceJsonLd = dataSourceMapper.buildDataSourceJsonLd(createRequest.getDataSource());
        var properties = getAssetProperties(createRequest, dataSourceJsonLd, organizationName);
        var privateProperties = getAssetPrivateProperties(createRequest);

        return buildAssetJsonLd(
            createRequest.getId(),
            properties,
            privateProperties,
            dataSourceJsonLd
        );
    }


    @SneakyThrows
    @Nullable
    public JsonObject editAssetJsonLd(
        JsonObject assetJsonLd,
        UiAssetEditRequest editRequest
    ) {
        var dataAddress = getDataAddressJsonLd(assetJsonLd);
        if (editRequest.getDataSourceOverrideOrNull() != null) {
            dataAddress = dataSourceMapper.buildDataSourceJsonLd(editRequest.getDataSourceOverrideOrNull());
        }

        var assetId = Objects.requireNonNull(JsonLdUtils.string(assetJsonLd, Prop.ID), "Asset JSON-LD had no @id");
        var organizationName = assetJsonLdParser.getCreatorOrganizationName(assetJsonLd);
        var createRequest = assetEditRequestMapper.buildCreateRequest(editRequest, assetId);
        var properties = getAssetProperties(createRequest, dataAddress, organizationName);
        var privateProperties = getAssetPrivateProperties(createRequest);

        return buildAssetJsonLd(
            assetId,
            properties,
            privateProperties,
            dataAddress
        );
    }

    private JsonObject buildAssetJsonLd(
        String assetId,
        JsonObject properties,
        JsonObject privateProperties,
        JsonObject dataSource
    ) {
        return Json.createObjectBuilder()
            .add(Prop.ID, assetId)
            .add(Prop.TYPE, Prop.Edc.TYPE_ASSET)
            .add(Prop.Edc.PROPERTIES, properties)
            .add(Prop.Edc.PRIVATE_PROPERTIES, privateProperties)
            .add(Prop.Edc.DATA_ADDRESS, dataSource)
            .build();
    }

    private JsonObject getAssetProperties(
        UiAssetCreateRequest request,
        JsonObject dataAddressJsonLd,
        String organizationName
    ) {
        var properties = Json.createObjectBuilder();

        addNotBlank(properties, Prop.Edc.ID, request.getId());
        addNotBlank(properties, Prop.Dcterms.LICENSE, request.getLicenseUrl());
        addNotBlank(properties, Prop.Dcterms.TITLE, request.getTitle());
        addNotBlank(properties, Prop.Dcterms.DESCRIPTION, request.getDescription());
        addNotBlank(properties, Prop.Dcterms.LANGUAGE, request.getLanguage());
        addNotBlank(properties, Prop.Dcat.VERSION, request.getVersion());
        addNotBlank(properties, Prop.Dcat.LANDING_PAGE, request.getLandingPageUrl());
        addNotBlank(properties, Prop.MobilityDcatAp.GEO_REFERENCE_METHOD, request.getGeoReferenceMethod());
        addNotBlank(properties, Prop.MobilityDcatAp.TRANSPORT_MODE, request.getTransportMode());
        addNotBlank(properties, Prop.Dcterms.RIGHTS_HOLDER, request.getSovereignLegalName());
        addNotBlank(properties, Prop.Dcterms.ACCRUAL_PERIODICITY, request.getDataUpdateFrequency());
        addNotBlankStringArray(properties, Prop.Dcat.KEYWORDS, request.getKeywords());
        addNonNull(properties, Prop.SovityDcatExt.CUSTOM_JSON, request.getCustomJsonAsString());

        addPublisher(properties, request);
        addCreator(properties, organizationName);
        addDistribution(properties, request);
        addTemporal(properties, request);
        addSpatial(properties, request);
        addMobilityTheme(properties, request);

        addCustomJsonLd(properties, request);
        addDataSourceHints(properties, dataAddressJsonLd);
        return properties.build();
    }

    private void addCreator(JsonObjectBuilder properties, String organizationName) {
        properties.add(Prop.Dcterms.CREATOR, Json.createObjectBuilder()
            .add(Prop.Foaf.NAME, organizationName));
    }

    private void addCustomJsonLd(JsonObjectBuilder properties, UiAssetCreateRequest request) {
        var jsonLdStr = request.getCustomJsonLdAsString();
        if (jsonLdStr == null) {
            return;
        }

        var jsonLd = JsonUtils.parseJsonObj(jsonLdStr);
        jsonLd.forEach((key, value) -> addNonNullJsonValue(properties, key, value));
    }

    private void addDataSourceHints(JsonObjectBuilder properties, JsonObject dataAddressJsonLd) {
        var dataSourceHints = dataSourceMapper.buildAssetPropsFromDataAddress(dataAddressJsonLd);
        properties.addAll(Json.createObjectBuilder(dataSourceHints));
    }

    private void addDistribution(JsonObjectBuilder properties, UiAssetCreateRequest request) {
        var distribution = buildDistribution(request);
        addNonNullJsonValue(properties, Prop.Dcat.DISTRIBUTION, distribution);
    }

    private void addMobilityTheme(JsonObjectBuilder properties, UiAssetCreateRequest request) {
        var dataCategory = request.getDataCategory();
        var dataSubcategory = request.getDataSubcategory();

        if (isBlank(dataCategory) && isBlank(dataSubcategory)) {
            return;
        }

        var mobilityTheme = Json.createObjectBuilder();
        addNotBlank(mobilityTheme, Prop.MobilityDcatAp.DataCategoryProps.DATA_CATEGORY, dataCategory);
        addNotBlank(mobilityTheme, Prop.MobilityDcatAp.DataCategoryProps.DATA_SUBCATEGORY, dataSubcategory);
        properties.add(Prop.MobilityDcatAp.MOBILITY_THEME, mobilityTheme);
    }

    private void addPublisher(JsonObjectBuilder properties, UiAssetCreateRequest request) {
        var publisherHomepage = request.getPublisherHomepage();

        if (isBlank(publisherHomepage)) {
            return;
        }

        var publisher = Json.createObjectBuilder().add(Prop.Foaf.HOMEPAGE, publisherHomepage);
        properties.add(Prop.Dcterms.PUBLISHER, publisher);
    }

    private void addSpatial(JsonObjectBuilder properties, UiAssetCreateRequest uiAssetCreateRequest) {
        var nutsLocations = uiAssetCreateRequest.getNutsLocations();

        if (isBlank(uiAssetCreateRequest.getGeoLocation()) && CollectionUtils.isEmpty(nutsLocations)) {
            return;
        }

        var spatial = Json.createObjectBuilder();
        addNotBlank(spatial, Prop.Skos.PREF_LABEL, uiAssetCreateRequest.getGeoLocation());
        addNotBlankStringArray(spatial, Prop.Dcterms.IDENTIFIER, uiAssetCreateRequest.getNutsLocations());
        properties.add(Prop.Dcterms.SPATIAL, spatial);
    }

    private void addTemporal(JsonObjectBuilder properties, UiAssetCreateRequest request) {
        var from = request.getTemporalCoverageFrom();
        var toInclusive = request.getTemporalCoverageToInclusive();

        if (from == null && toInclusive == null) {
            return;
        }

        var temporal = Json.createObjectBuilder();
        addNonNull(temporal, Prop.Dcat.START_DATE, from);
        addNonNull(temporal, Prop.Dcat.END_DATE, toInclusive);
        properties.add(Prop.Dcterms.TEMPORAL, temporal);
    }

    @Nullable
    private JsonObject buildDistribution(UiAssetCreateRequest request) {
        var dataSampleUrls = request.getDataSampleUrls();
        var referenceFileUrls = request.getReferenceFileUrls();

        var hasRootLevel = request.getMediaType() != null || CollectionUtils.isNotEmpty(dataSampleUrls);
        var hasRights = isNotBlank(request.getConditionsForUse());
        var hasDataModel = isNotBlank(request.getDataModel());
        var hasReferenceFiles = CollectionUtils.isNotEmpty(referenceFileUrls)
            || isNotBlank(request.getReferenceFilesDescription());

        if (!hasRootLevel && !hasRights && !hasDataModel && !hasReferenceFiles) {
            return null;
        }

        var distribution = Json.createObjectBuilder();
        addNotBlank(distribution, Prop.Dcat.MEDIATYPE, request.getMediaType());
        addNotBlankStringArray(distribution, Prop.Adms.SAMPLE, request.getDataSampleUrls());

        if (hasRights) {
            var rights = Json.createObjectBuilder();
            addNotBlank(rights, Prop.Rdfs.LABEL, request.getConditionsForUse());
            distribution.add(Prop.Dcterms.RIGHTS, rights);
        }

        if (hasDataModel || hasReferenceFiles) {
            var mobilityDataStandard = Json.createObjectBuilder();
            addNotBlank(mobilityDataStandard, Prop.ID, request.getDataModel());

            if (hasReferenceFiles) {
                var referenceFiles = Json.createObjectBuilder();
                addNotBlankStringArray(referenceFiles, Prop.Dcat.DOWNLOAD_URL, request.getReferenceFileUrls());
                addNotBlank(referenceFiles, Prop.Rdfs.LITERAL, request.getReferenceFilesDescription());
                mobilityDataStandard.add(Prop.MobilityDcatAp.SCHEMA, referenceFiles);
            }

            distribution.add(Prop.MobilityDcatAp.MOBILITY_DATA_STANDARD, mobilityDataStandard);
        }

        return distribution.build();
    }

    private JsonObject getAssetPrivateProperties(UiAssetCreateRequest uiAssetCreateRequest) {
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

        return privateProperties.build();
    }

    private JsonObject getDataAddressJsonLd(JsonObject assetJsonLd) {
        var dataAddress = JsonLdUtils.object(assetJsonLd, Prop.Edc.DATA_ADDRESS);

        if (!dataAddress.containsKey(Prop.Edc.PROPERTIES)) {
            return dataAddress;
        }

        return Json.createObjectBuilder(dataAddress)
            .remove(Prop.Edc.PROPERTIES)
            .addAll(Json.createObjectBuilder(JsonLdUtils.object(dataAddress, Prop.Edc.PROPERTIES)))
            .build();
    }
}
