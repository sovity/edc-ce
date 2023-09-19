package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static de.sovity.edc.ext.wrapper.api.common.mappers.utils.JsonBuilderUtils.addNonNull;
import static de.sovity.edc.ext.wrapper.api.common.mappers.utils.JsonBuilderUtils.addNonNullArray;

@RequiredArgsConstructor
public class UiAssetBuilder {
    private final ObjectMapper mapper = new ObjectMapper();
    private final EdcPropertyMapperUtils edcPropertyMapperUtils;

    public UiAsset buildUiAsset(JsonObject assetJsonLd) {
        var assetJsonLdObj = parseAssetJsonLd(JsonUtils.toJson(assetJsonLd));
        return buildUiAsset(assetJsonLdObj);
    }

    private UiAsset buildUiAsset(AssetJsonLd assetJsonLd) {
        var assetPropertyJsonLd = assetJsonLd.getProperties();

        return UiAsset.builder()
                .assetId(assetPropertyJsonLd.getAssetId())
                .keywords(assetPropertyJsonLd.getKeywords() == null ? List.of() : assetPropertyJsonLd.getKeywords())
                .version(assetPropertyJsonLd.getVersion())
                .licenseUrl(assetPropertyJsonLd.getLicense())
                .creatorOrganizationName(assetPropertyJsonLd.getCreator() != null ? assetPropertyJsonLd.getCreator().getName() : null)
                .publisherHomepage(assetPropertyJsonLd.getPublisher() != null ? assetPropertyJsonLd.getPublisher().getName() : null)
                .description(assetPropertyJsonLd.getDescription())
                .language(assetPropertyJsonLd.getLanguage())
                .name(assetPropertyJsonLd.getName())
                .httpDatasourceHintsProxyMethod(assetPropertyJsonLd.getHttpDatasourceHintsProxyMethod())
                .httpDatasourceHintsProxyPath(assetPropertyJsonLd.getHttpDatasourceHintsProxyPath())
                .httpDatasourceHintsProxyQueryParams(assetPropertyJsonLd.getHttpDatasourceHintsProxyQueryParams())
                .httpDatasourceHintsProxyBody(assetPropertyJsonLd.getHttpDatasourceHintsProxyBody())
                .dataCategory(assetPropertyJsonLd.getDataCategory())
                .dataSubcategory(assetPropertyJsonLd.getDataSubcategory())
                .dataModel(assetPropertyJsonLd.getDataModel())
                .geoReferenceMethod(assetPropertyJsonLd.getGeoReferenceMethod())
                .transportMode(assetPropertyJsonLd.getTransportMode())
                .landingPageUrl(assetPropertyJsonLd.getLandingPage())
                .mediaType(assetPropertyJsonLd.getMediaType())
                .build();
    }

    @SneakyThrows
    private AssetJsonLd parseAssetJsonLd(String assetJsonLd) {
        var assetPropertiesJsonLd = mapper.readTree(assetJsonLd).get(Prop.Edc.PROPERTIES);
        var assetProperties = mapper.readValue(assetPropertiesJsonLd.toString(), AssetPropertyJsonLd.class);

        return AssetJsonLd.builder()
                .assetId(String.valueOf(mapper.readTree(assetJsonLd).get(Prop.ID)))
                .properties(assetProperties)
                .build();
    }

    @SneakyThrows
    @Nullable
    public JsonObject buildAssetJsonLd(UiAssetCreateRequest uiAssetCreateRequest) {
        var properties = getAssetProperties(uiAssetCreateRequest);
        var dataAddress = getDataAddress(uiAssetCreateRequest);

        return Json.createObjectBuilder()
                .add(Prop.ID, uiAssetCreateRequest.getId())
                .add(Prop.TYPE, Prop.Edc.TYPE_ASSET)
                .add(Prop.Edc.PROPERTIES, Json.createArrayBuilder().add(properties))
                .add(Prop.Edc.DATA_ADDRESS, Json.createArrayBuilder().add(dataAddress))
                .build();
    }

    private JsonObjectBuilder getAssetProperties(UiAssetCreateRequest uiAssetCreateRequest) {
        var properties = Json.createObjectBuilder();

        addNonNull(properties, Prop.Edc.ID, uiAssetCreateRequest.getId());
        addNonNull(properties, Prop.Dcterms.LICENSE, uiAssetCreateRequest.getLicenseUrl());
        addNonNull(properties, Prop.Dcterms.NAME, uiAssetCreateRequest.getName());
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
        addNonNullArray(properties, Prop.Dcat.KEYWORDS, uiAssetCreateRequest.getKeywords());

        return properties;
    }

    private JsonObjectBuilder getDataAddress(UiAssetCreateRequest uiAssetCreateRequest) {
        var props = edcPropertyMapperUtils.toMapOfObject(uiAssetCreateRequest.getDataAddressProperties());
        return Json.createObjectBuilder()
                .add(Prop.TYPE, Prop.Edc.TYPE_DATA_ADDRESS)
                .add(Prop.Edc.PROPERTIES, Json.createArrayBuilder().add(Json.createObjectBuilder(props)));
    }
}
