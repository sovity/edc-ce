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
        uiAsset.setDataCategory(JsonLdUtils.string(properties, Prop.Mobility.DATA_CATEGORY));
        uiAsset.setDataSubcategory(JsonLdUtils.string(properties, Prop.Mobility.DATA_SUBCATEGORY));
        uiAsset.setDataModel(JsonLdUtils.string(properties, Prop.Mobility.DATA_MODEL));
        uiAsset.setGeoReferenceMethod(JsonLdUtils.string(properties, Prop.Mobility.GEO_REFERENCE_METHOD));
        uiAsset.setTransportMode(JsonLdUtils.string(properties, Prop.Mobility.TRANSPORT_MODE));
        uiAsset.setKeywords(JsonLdUtils.stringList(properties, Prop.Dcat.KEYWORDS));

        uiAsset.setHttpDatasourceHintsProxyMethod(JsonLdUtils.bool(properties, HttpDatasourceHints.METHOD));
        uiAsset.setHttpDatasourceHintsProxyPath(JsonLdUtils.bool(properties, HttpDatasourceHints.PATH));
        uiAsset.setHttpDatasourceHintsProxyQueryParams(JsonLdUtils.bool(properties, HttpDatasourceHints.QUERY_PARAMS));
        uiAsset.setHttpDatasourceHintsProxyBody(JsonLdUtils.bool(properties, HttpDatasourceHints.BODY));

        var publisher = JsonLdUtils.object(properties, Prop.Dcterms.PUBLISHER);
        uiAsset.setPublisherHomepage(JsonLdUtils.string(publisher, Prop.Foaf.HOMEPAGE));

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
                Prop.Mobility.DATA_CATEGORY,
                Prop.Mobility.DATA_MODEL,
                Prop.Mobility.DATA_SUBCATEGORY,
                Prop.Mobility.GEO_REFERENCE_METHOD,
                Prop.Mobility.TRANSPORT_MODE,
                HttpDatasourceHints.BODY,
                HttpDatasourceHints.METHOD,
                HttpDatasourceHints.PATH,
                HttpDatasourceHints.QUERY_PARAMS
        ));
        uiAsset.setAdditionalProperties(getStringProperties(remaining));
        uiAsset.setAdditionalJsonProperties(getJsonProperties(remaining));

        // Private Properties
        var privateProperties = JsonLdUtils.tryCompact(getPrivateProperties(assetJsonLd));
        uiAsset.setPrivateProperties(getStringProperties(privateProperties));
        uiAsset.setPrivateJsonProperties(getJsonProperties(privateProperties));

        return uiAsset;
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
        addNonNull(properties, Prop.Mobility.DATA_CATEGORY, uiAssetCreateRequest.getDataCategory());
        addNonNull(properties, Prop.Mobility.DATA_SUBCATEGORY, uiAssetCreateRequest.getDataSubcategory());
        addNonNull(properties, Prop.Mobility.DATA_MODEL, uiAssetCreateRequest.getDataModel());
        addNonNull(properties, Prop.Mobility.GEO_REFERENCE_METHOD, uiAssetCreateRequest.getGeoReferenceMethod());
        addNonNull(properties, Prop.Mobility.TRANSPORT_MODE, uiAssetCreateRequest.getTransportMode());
        addNonNullArray(properties, Prop.Dcat.KEYWORDS, uiAssetCreateRequest.getKeywords());

        if (uiAssetCreateRequest.getPublisherHomepage() != null) {
            properties.add(Prop.Dcterms.PUBLISHER, Json.createObjectBuilder()
                    .add(Prop.Foaf.HOMEPAGE, uiAssetCreateRequest.getPublisherHomepage()));
        }

        properties.add(Prop.Dcterms.CREATOR, Json.createObjectBuilder()
                .add(Prop.Foaf.NAME, organizationName));

        var dataAddress = uiAssetCreateRequest.getDataAddressProperties();
        if (dataAddress.get(Prop.Edc.TYPE).equals("HttpData")) {
            addNonNull(properties, HttpDatasourceHints.BODY, trueIfTrue(dataAddress, Prop.Edc.PROXY_BODY));
            addNonNull(properties, HttpDatasourceHints.PATH, trueIfTrue(dataAddress, Prop.Edc.PROXY_PATH));
            addNonNull(properties, HttpDatasourceHints.QUERY_PARAMS, trueIfTrue(dataAddress, Prop.Edc.PROXY_QUERY_PARAMS));
            addNonNull(properties, HttpDatasourceHints.METHOD, trueIfTrue(dataAddress, Prop.Edc.PROXY_METHOD));
        }

        var additionalProperties = uiAssetCreateRequest.getAdditionalProperties();
        if (additionalProperties != null) {
            additionalProperties.forEach((k, v) -> addNonNull(properties, k, v));
        }

        var additionalJsonProperties = uiAssetCreateRequest.getAdditionalJsonProperties();
        if (additionalJsonProperties != null) {
            additionalJsonProperties.forEach((k, v) -> addNonNullJsonValue(properties, k, v));
        }

        return properties;
    }

    private JsonObjectBuilder getAssetPrivateProperties(UiAssetCreateRequest uiAssetCreateRequest) {
        var privateProperties = Json.createObjectBuilder();

        var stringProperties = uiAssetCreateRequest.getPrivateProperties();
        if (stringProperties != null) {
            stringProperties.forEach((k, v) -> addNonNull(privateProperties, k, v));
        }

        var jsonProperties = uiAssetCreateRequest.getPrivateJsonProperties();
        if (jsonProperties != null) {
            jsonProperties.forEach((k, v) -> addNonNullJsonValue(privateProperties, k, v));
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

    private Map<String, String> getStringProperties(JsonObject jsonObject) {
        return getPropertyMap(
                jsonObject,
                it -> it.getValueType() == JsonValue.ValueType.STRING,
                it -> ((JsonString) it).getString()
        );
    }

    private Map<String, String> getJsonProperties(JsonObject jsonObject) {
        return getPropertyMap(
                jsonObject,
                it -> it.getValueType() != JsonValue.ValueType.STRING,
                JsonUtils::toJson
        );
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
