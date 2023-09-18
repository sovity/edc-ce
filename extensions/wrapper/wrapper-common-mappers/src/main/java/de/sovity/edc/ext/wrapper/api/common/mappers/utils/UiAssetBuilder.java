package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

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
    public JsonObject buildAssetJsonLd(UiAssetCreateRequest uiAssetCreateRequest) {

        var properties = Json.createObjectBuilder()
                .add(Prop.Dcat.KEYWORDS, Json.createArrayBuilder(uiAssetCreateRequest.getKeywords()))
                .add(Prop.Dcterms.PUBLISHER, Json.createObjectBuilder()
                        .add(Prop.TYPE, Prop.Foaf.ORGANIZATION)
                        .add(Prop.Foaf.HOMEPAGE, uiAssetCreateRequest.getPublisherHomepage()))
                .add(Prop.Dcterms.CREATOR, Json.createObjectBuilder()
                        .add(Prop.TYPE, Prop.Foaf.ORGANIZATION)
                        .add(Prop.Foaf.NAME, uiAssetCreateRequest.getCreatorOrganizationName()))
                .add(Prop.Dcterms.LICENSE, uiAssetCreateRequest.getLicenseUrl())
                .add(Prop.Dcterms.TITLE, uiAssetCreateRequest.getTitle())
                .add(Prop.Dcterms.DESCRIPTION, uiAssetCreateRequest.getDescription())
                .add(Prop.Dcterms.LANGUAGE, uiAssetCreateRequest.getLanguage())
                .add(Prop.Dcat.VERSION, uiAssetCreateRequest.getVersion())
                .add(Prop.Dcat.MEDIATYPE, uiAssetCreateRequest.getDistribution())
                .add(Prop.Dcat.LANDING_PAGE, uiAssetCreateRequest.getLandingPageUrl())
                .add(Prop.Mds.DATA_CATEGORY, uiAssetCreateRequest.getDataCategory())
                .add(Prop.Mds.DATA_SUBCATEGORY, uiAssetCreateRequest.getDataSubcategory())
                .add(Prop.Mds.DATA_MODEL, uiAssetCreateRequest.getDataModel())
                .add(Prop.Mds.GEO_REFERENCE_METHOD, uiAssetCreateRequest.getGeoReferenceMethod())
                .add(Prop.Mds.TRANSPORT_MODE, uiAssetCreateRequest.getTransportMode())
                .build();

        var dataAddressProperties = Json.createObjectBuilder()
                .add(Prop.Edc.TYPE, uiAssetCreateRequest.getDataAddressProperties().get(Prop.Edc.TYPE))
                .add(Prop.Edc.BASE_URL, uiAssetCreateRequest.getDataAddressProperties().get(Prop.Edc.BASE_URL))
                .build();

        return Json.createObjectBuilder()
                .add(Prop.ID, uiAssetCreateRequest.getId())
                .add("properties", properties)
                .add("dataAddress", dataAddressProperties)
                .build();
    }
}
