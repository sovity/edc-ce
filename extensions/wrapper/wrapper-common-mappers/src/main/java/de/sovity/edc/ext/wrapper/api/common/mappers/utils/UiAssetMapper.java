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
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static de.sovity.edc.ext.wrapper.api.common.mappers.utils.JsonBuilderUtils.addNonNull;
import static de.sovity.edc.ext.wrapper.api.common.mappers.utils.JsonBuilderUtils.addNonNullArray;
import static de.sovity.edc.ext.wrapper.api.common.mappers.utils.JsonBuilderUtils.addNonNullJsonValue;
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
        uiAsset.setMediaType(JsonLdUtils.string(properties, Prop.Dcat.MEDIATYPE));
        uiAsset.setLandingPageUrl(JsonLdUtils.string(properties, Prop.Dcat.LANDING_PAGE));
        uiAsset.setDataCategory(JsonLdUtils.string(properties, Prop.Mds.DATA_CATEGORY));
        uiAsset.setDataSubcategory(JsonLdUtils.string(properties, Prop.Mds.DATA_SUBCATEGORY));
        uiAsset.setDataModel(JsonLdUtils.string(properties, Prop.Mds.DATA_MODEL));
        uiAsset.setGeoReferenceMethod(JsonLdUtils.string(properties, Prop.Mds.GEO_REFERENCE_METHOD));
        uiAsset.setTransportMode(JsonLdUtils.string(properties, Prop.Mds.TRANSPORT_MODE));
        uiAsset.setSovereignLegalName(JsonLdUtils.string(properties, Prop.MdsDcatExt.SOVEREIGN));
        uiAsset.setGeoLocation(JsonLdUtils.string(properties, Prop.MdsDcatExt.GEO_LOCATION));
        uiAsset.setNutsLocation(JsonLdUtils.stringList(properties, Prop.MdsDcatExt.NUTS_LOCATION));
        uiAsset.setDataSampleUrls(JsonLdUtils.stringList(properties, Prop.MdsDcatExt.DATA_SAMPLE_URLS));
        uiAsset.setReferenceFileUrls(JsonLdUtils.stringList(properties, Prop.MdsDcatExt.REFERENCE_FILES));
        uiAsset.setReferenceFilesDescription(JsonLdUtils.string(properties, Prop.MdsDcatExt.ADDITIONAL_DESCRIPTION));
        uiAsset.setConditionsForUse(JsonLdUtils.string(properties, Prop.MdsDcatExt.CONDITIONS_FOR_USE));
        uiAsset.setDataUpdateFrequency(JsonLdUtils.string(properties, Prop.MdsDcatExt.DATA_UPDATE_FREQUENCY));
        uiAsset.setTemporalCoverageFrom(JsonLdUtils.localDate(properties, Prop.MdsDcatExt.TEMPORAL_COVERAGE_FROM));
        uiAsset.setTemporalCoverageToInclusive(JsonLdUtils.localDate(properties, Prop.MdsDcatExt.TEMPORAL_COVERAGE_TO));
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
        JsonObject remaining = removeHandledProperties(properties, List.of(
                // Implicitly handled / should be skipped if found
                Prop.ID,
                Prop.TYPE,
                Prop.CONTEXT,
                Prop.Edc.ID,
                Prop.Dcterms.IDENTIFIER,

                // Explicitly handled
                Prop.Dcat.KEYWORDS,
                Prop.Dcat.LANDING_PAGE,
                Prop.Dcat.MEDIATYPE,
                Prop.Dcat.VERSION,
                Prop.Dcterms.CREATOR,
                Prop.Dcterms.DESCRIPTION,
                Prop.Dcterms.LANGUAGE,
                Prop.Dcterms.LICENSE,
                Prop.Dcterms.PUBLISHER,
                Prop.Dcterms.TITLE,
                Prop.Mds.DATA_CATEGORY,
                Prop.Mds.DATA_MODEL,
                Prop.Mds.DATA_SUBCATEGORY,
                Prop.Mds.GEO_REFERENCE_METHOD,
                Prop.Mds.TRANSPORT_MODE,
                Prop.MdsDcatExt.SOVEREIGN,
                Prop.MdsDcatExt.GEO_LOCATION,
                Prop.MdsDcatExt.NUTS_LOCATION,
                Prop.MdsDcatExt.DATA_SAMPLE_URLS,
                Prop.MdsDcatExt.REFERENCE_FILES,
                Prop.MdsDcatExt.ADDITIONAL_DESCRIPTION,
                Prop.MdsDcatExt.CONDITIONS_FOR_USE,
                Prop.MdsDcatExt.DATA_UPDATE_FREQUENCY,
                Prop.MdsDcatExt.TEMPORAL_COVERAGE_FROM,
                Prop.MdsDcatExt.TEMPORAL_COVERAGE_TO,

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
        for (val e : remaining.entrySet()) {
            customJsonLd.add(e.getKey(), e.getValue());
        }
        JsonObject compacted = JsonLdUtils.tryCompact(customJsonLd.build());
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
        addNonNull(properties, Prop.Dcat.MEDIATYPE, uiAssetCreateRequest.getMediaType());
        addNonNull(properties, Prop.Dcat.LANDING_PAGE, uiAssetCreateRequest.getLandingPageUrl());
        addNonNull(properties, Prop.Mds.DATA_CATEGORY, uiAssetCreateRequest.getDataCategory());
        addNonNull(properties, Prop.Mds.DATA_SUBCATEGORY, uiAssetCreateRequest.getDataSubcategory());
        addNonNull(properties, Prop.Mds.DATA_MODEL, uiAssetCreateRequest.getDataModel());
        addNonNull(properties, Prop.Mds.GEO_REFERENCE_METHOD, uiAssetCreateRequest.getGeoReferenceMethod());
        addNonNull(properties, Prop.Mds.TRANSPORT_MODE, uiAssetCreateRequest.getTransportMode());
        addNonNull(properties, Prop.MdsDcatExt.SOVEREIGN, uiAssetCreateRequest.getSovereignLegalName());
        addNonNull(properties, Prop.MdsDcatExt.GEO_LOCATION, uiAssetCreateRequest.getGeoLocation());
        addNonNullArray(properties, Prop.MdsDcatExt.NUTS_LOCATION, uiAssetCreateRequest.getNutsLocation());
        addNonNullArray(properties, Prop.MdsDcatExt.DATA_SAMPLE_URLS, uiAssetCreateRequest.getDataSampleUrls());
        addNonNullArray(properties, Prop.MdsDcatExt.REFERENCE_FILES, uiAssetCreateRequest.getReferenceFileUrls());
        addNonNull(properties, Prop.MdsDcatExt.ADDITIONAL_DESCRIPTION, uiAssetCreateRequest.getReferenceFilesDescription());
        addNonNull(properties, Prop.MdsDcatExt.CONDITIONS_FOR_USE, uiAssetCreateRequest.getConditionsForUse());
        addNonNull(properties, Prop.MdsDcatExt.DATA_UPDATE_FREQUENCY, uiAssetCreateRequest.getDataUpdateFrequency());
        addNonNull(properties, Prop.MdsDcatExt.TEMPORAL_COVERAGE_FROM, uiAssetCreateRequest.getTemporalCoverageFrom());
        addNonNull(properties, Prop.MdsDcatExt.TEMPORAL_COVERAGE_TO, uiAssetCreateRequest.getTemporalCoverageToInclusive());

        addNonNullArray(properties, Prop.Dcat.KEYWORDS, uiAssetCreateRequest.getKeywords());

        if (uiAssetCreateRequest.getPublisherHomepage() != null) {
            properties.add(Prop.Dcterms.PUBLISHER, Json.createObjectBuilder()
                    .add(Prop.Foaf.HOMEPAGE, uiAssetCreateRequest.getPublisherHomepage()));
        }

        properties.add(Prop.Dcterms.CREATOR, Json.createObjectBuilder()
                .add(Prop.Foaf.NAME, organizationName));

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

    private void add(JsonObject overrides, JsonObjectBuilder properties, String key, String value) {
        val override = JsonLdUtils.string(overrides, key);
        if (override != null) {
            properties.add(key, override);
        }
        properties.add(key, value);
    }

    private JsonObjectBuilder getAssetPrivateProperties(UiAssetCreateRequest uiAssetCreateRequest) {
        var privateProperties = Json.createObjectBuilder();

        val privateJsonStr = uiAssetCreateRequest.getPrivateCustomJsonAsString();
        if (privateJsonStr != null) {
            addNonNull(
                    privateProperties,
                    Prop.SovityDcatExt.PRIVATE_CUSTOM_JSON,
                    privateJsonStr);
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

    private Map<String, String> getPropertyMap(
            JsonObject jsonObject,
            Predicate<JsonValue> filter,
            Function<JsonValue, String> mapper) {
        return jsonObject.entrySet().stream()
                .filter(entry -> filter.test(entry.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> mapper.apply(entry.getValue())
                ));
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
}
