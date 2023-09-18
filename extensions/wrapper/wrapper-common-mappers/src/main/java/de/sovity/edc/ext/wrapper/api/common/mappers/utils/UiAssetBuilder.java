package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
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

@RequiredArgsConstructor
public class UiAssetBuilder {
    private final ObjectMapper mapper = new ObjectMapper();

    public UiAsset buildUiAsset(JsonObject assetJsonLd) {
        var assetJsonLdObj = parseAssetJsonLd(JsonUtils.toJson(assetJsonLd));
        return buildUiAsset(assetJsonLdObj);
    }

    private UiAsset buildUiAsset(AssetJsonLd assetJsonLd) {
        var assetPropertyJsonLd = assetJsonLd.getProperties();

        return UiAsset.builder()
                .assetId(assetPropertyJsonLd.getAssetId())
                .keywords(assetPropertyJsonLd.getKeywords())
                .version(assetPropertyJsonLd.getVersion())
                .licenseUrl(assetPropertyJsonLd.getLicense())
                .creatorOrganizationName(assetPropertyJsonLd.getCreator() != null ? assetPropertyJsonLd.getCreator().getName() : null)
                .publisherHomepage(assetPropertyJsonLd.getPublisher() != null ? assetPropertyJsonLd.getPublisher().getName() : null)
                .description(assetPropertyJsonLd.getDescription())
                .language(assetPropertyJsonLd.getLanguage())
                .title(assetPropertyJsonLd.getTitle())
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
        var assetPropertiesJsonLd = mapper.readTree(assetJsonLd).get("properties");
        var assetProperties = mapper.readValue(assetPropertiesJsonLd.toString(), AssetPropertyJsonLd.class);

        return AssetJsonLd.builder()
                .assetId(String.valueOf(mapper.readTree(assetJsonLd).get("id")))
                .properties(assetProperties)
                .build();
    }

    @SneakyThrows
    @Nullable
    public JsonObject buildAssetJsonLd(UiAssetCreateRequest uiAssetCreateRequest) {

        var propertiesBuilder = Json.createObjectBuilder();

        JsonBuilderUtils.addNonNull(propertiesBuilder, Prop.Edc.ID, uiAssetCreateRequest.getId());
        JsonBuilderUtils.addNonNull(propertiesBuilder, Prop.Dcterms.LICENSE, uiAssetCreateRequest.getLicenseUrl());
        JsonBuilderUtils.addNonNull(propertiesBuilder, Prop.Dcterms.TITLE, uiAssetCreateRequest.getTitle());
        JsonBuilderUtils.addNonNull(propertiesBuilder, Prop.Dcterms.DESCRIPTION, uiAssetCreateRequest.getDescription());
        JsonBuilderUtils.addNonNull(propertiesBuilder, Prop.Dcterms.LANGUAGE, uiAssetCreateRequest.getLanguage());
        JsonBuilderUtils.addNonNull(propertiesBuilder, Prop.Dcat.VERSION, uiAssetCreateRequest.getVersion());
        JsonBuilderUtils.addNonNull(propertiesBuilder, Prop.Dcat.MEDIATYPE, uiAssetCreateRequest.getDistribution());
        JsonBuilderUtils.addNonNull(propertiesBuilder, Prop.Dcat.LANDING_PAGE, uiAssetCreateRequest.getLandingPageUrl());
        JsonBuilderUtils.addNonNull(propertiesBuilder, Prop.Mds.DATA_CATEGORY, uiAssetCreateRequest.getDataCategory());
        JsonBuilderUtils.addNonNull(propertiesBuilder, Prop.Mds.DATA_SUBCATEGORY, uiAssetCreateRequest.getDataSubcategory());
        JsonBuilderUtils.addNonNull(propertiesBuilder, Prop.Mds.DATA_MODEL, uiAssetCreateRequest.getDataModel());
        JsonBuilderUtils.addNonNull(propertiesBuilder, Prop.Mds.GEO_REFERENCE_METHOD, uiAssetCreateRequest.getGeoReferenceMethod());
        JsonBuilderUtils.addNonNull(propertiesBuilder, Prop.Mds.TRANSPORT_MODE, uiAssetCreateRequest.getTransportMode());
        JsonBuilderUtils.addNonNullArray(propertiesBuilder, Prop.Dcat.KEYWORDS, uiAssetCreateRequest.getKeywords());

        if (uiAssetCreateRequest.getPublisherHomepage() != null) {
            propertiesBuilder.add(Prop.Dcterms.PUBLISHER, Json.createObjectBuilder()
                    .add(Prop.TYPE, Prop.Foaf.ORGANIZATION)
                    .add(Prop.Foaf.HOMEPAGE, uiAssetCreateRequest.getPublisherHomepage()));
        }
        if (uiAssetCreateRequest.getCreatorOrganizationName() != null) {
            propertiesBuilder.add(Prop.Dcterms.CREATOR, Json.createObjectBuilder()
                    .add(Prop.TYPE, Prop.Foaf.ORGANIZATION)
                    .add(Prop.Foaf.NAME, uiAssetCreateRequest.getCreatorOrganizationName()));

        }

        var assetJsonLd = Json.createObjectBuilder()
                .add(Prop.ID, uiAssetCreateRequest.getId())
                .add(Prop.TYPE, Prop.Edc.TYPE_ASSET)
                .add(Prop.Edc.PROPERTIES, propertiesBuilder.build())
                .build();

        var assetJsonLdString = assetJsonLd.toString();


        return assetJsonLd;
    }
}
