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
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static de.sovity.edc.ext.wrapper.api.common.mappers.utils.JsonBuilderUtils.addNonNull;
import static de.sovity.edc.ext.wrapper.api.common.mappers.utils.JsonBuilderUtils.addNonNullArray;

@RequiredArgsConstructor
public class UiAssetMapper {
    private final EdcPropertyUtils edcPropertyUtils;
    private final JsonLd jsonLd;

    public UiAsset buildUiAsset(JsonObject assetJsonLd) {
        var properties = JsonLdUtils.object(assetJsonLd, Prop.Edc.PROPERTIES);

        var uiAsset = new UiAsset();
        uiAsset.setAssetJsonLd(buildCompactAssetJsonLd(assetJsonLd));

        uiAsset.setAssetId(JsonLdUtils.string(assetJsonLd, Prop.ID));
        uiAsset.setLicenseUrl(JsonLdUtils.string(properties, Prop.Dcterms.LICENSE));
        uiAsset.setName(JsonLdUtils.string(properties, Prop.Dcterms.TITLE));
        uiAsset.setDescription(JsonLdUtils.string(properties, Prop.Dcterms.DESCRIPTION));
        uiAsset.setLanguage(JsonLdUtils.string(properties, Prop.Dcterms.LANGUAGE));
        uiAsset.setVersion(JsonLdUtils.string(properties, Prop.Dcat.VERSION));
        uiAsset.setMediaType(JsonLdUtils.string(properties, Prop.Dcat.MEDIATYPE));
        uiAsset.setLandingPageUrl(JsonLdUtils.string(properties, Prop.Dcat.LANDING_PAGE));
        uiAsset.setDataCategory(JsonLdUtils.string(properties, Prop.Mds.DATA_CATEGORY));
        uiAsset.setDataSubcategory(JsonLdUtils.string(properties, Prop.Mds.DATA_SUBCATEGORY));
        uiAsset.setDataModel(JsonLdUtils.string(properties, Prop.Mds.DATA_MODEL));
        uiAsset.setGeoReferenceMethod(JsonLdUtils.string(properties, Prop.Mds.GEO_REFERENCE_METHOD));
        uiAsset.setTransportMode(JsonLdUtils.string(properties, Prop.Mds.TRANSPORT_MODE));
        uiAsset.setKeywords(JsonLdUtils.stringList(properties, Prop.Dcat.KEYWORDS));

        uiAsset.setHttpDatasourceHintsProxyMethod(JsonLdUtils.bool(properties, HttpDatasourceHints.METHOD));
        uiAsset.setHttpDatasourceHintsProxyPath(JsonLdUtils.bool(properties, HttpDatasourceHints.PATH));
        uiAsset.setHttpDatasourceHintsProxyQueryParams(JsonLdUtils.bool(properties, HttpDatasourceHints.QUERY_PARAMS));
        uiAsset.setHttpDatasourceHintsProxyBody(JsonLdUtils.bool(properties, HttpDatasourceHints.BODY));

        var publisher = JsonLdUtils.object(properties, Prop.Dcterms.PUBLISHER);
        uiAsset.setPublisherHomepage(JsonLdUtils.string(publisher, Prop.Foaf.HOMEPAGE));

        var creator = JsonLdUtils.object(properties, Prop.Dcterms.CREATOR);
        uiAsset.setCreatorOrganizationName(JsonLdUtils.string(creator, Prop.Foaf.NAME));

        return uiAsset;
    }

    private String buildCompactAssetJsonLd(JsonObject assetJsonLd) {
        var compacted = jsonLd.compact(assetJsonLd).orElseThrow(FailedMappingException::ofFailure);
        return JsonUtils.toJson(compacted);
    }

    @SneakyThrows
    @Nullable
    public JsonObject buildAssetJsonLd(UiAssetCreateRequest uiAssetCreateRequest) {
        var properties = getAssetProperties(uiAssetCreateRequest);
        var dataAddress = getDataAddress(uiAssetCreateRequest);

        return Json.createObjectBuilder()
                .add(Prop.ID, uiAssetCreateRequest.getId())
                .add(Prop.TYPE, Prop.Edc.TYPE_ASSET)
                .add(Prop.Edc.PROPERTIES, properties)
                .add(Prop.Edc.DATA_ADDRESS, dataAddress)
                .build();
    }

    private JsonObjectBuilder getAssetProperties(UiAssetCreateRequest uiAssetCreateRequest) {
        var properties = Json.createObjectBuilder();

        addNonNull(properties, Prop.Edc.ID, uiAssetCreateRequest.getId());
        addNonNull(properties, Prop.Dcterms.LICENSE, uiAssetCreateRequest.getLicenseUrl());
        addNonNull(properties, Prop.Dcterms.TITLE, uiAssetCreateRequest.getName());
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

        if (uiAssetCreateRequest.getPublisherHomepage() != null) {
            properties.add(Prop.Dcterms.PUBLISHER, Json.createObjectBuilder()
                    .add(Prop.Foaf.HOMEPAGE, uiAssetCreateRequest.getPublisherHomepage()));
        }

        if (uiAssetCreateRequest.getCreatorOrganizationName() != null) {
            properties.add(Prop.Dcterms.CREATOR, Json.createObjectBuilder()
                    .add(Prop.Foaf.NAME, uiAssetCreateRequest.getCreatorOrganizationName()));
        }

        var dataAddress = uiAssetCreateRequest.getDataAddressProperties();
        if (dataAddress.get(Prop.Edc.TYPE).equals("HttpData")) {
            addNonNull(properties, HttpDatasourceHints.BODY, trueIfTrue(dataAddress, Prop.Edc.PROXY_BODY));
            addNonNull(properties, HttpDatasourceHints.PATH, trueIfTrue(dataAddress, Prop.Edc.PROXY_PATH));
            addNonNull(properties, HttpDatasourceHints.QUERY_PARAMS, trueIfTrue(dataAddress, Prop.Edc.PROXY_QUERY_PARAMS));
            addNonNull(properties, HttpDatasourceHints.METHOD, trueIfTrue(dataAddress, Prop.Edc.PROXY_METHOD));
        }

        return properties;
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
}
