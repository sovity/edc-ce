package de.sovity.edc.ext.wrapper.api.common.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.AssetHelperDto;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.EdcPropertyMapperUtils;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RequiredArgsConstructor
public class AssetMapper {
    /**
     * This Object Mapper must be able to handle JSON-LD serialization / deserialization.
     */
    private final ObjectMapper jsonLdObjectMapper;
    private final EdcPropertyMapperUtils edcPropertyMapperUtils;
    private final TypeTransformerRegistry typeTransformerRegistry;

    @SneakyThrows
    public UiAsset buildUiAssetFromAsset(Asset asset) {
        var uiAsset = buildUiAssetFromAssetHelper(buildHelperDto(jsonLdObjectMapper.writeValueAsString(asset)));

        uiAsset.setId(asset.getId());
        uiAsset.setPrivateProperties(asset.getPrivateProperties());
        uiAsset.setDataAddressProperties(edcPropertyMapperUtils.truncateToMapOfString(asset.getDataAddress().getProperties()));
        uiAsset.setKeywords(uiAsset.getKeywords() == null ? List.of() : uiAsset.getKeywords());

        return uiAsset;
    }

    public Asset buildAssetFromAssetPropertiesJsonLd(JsonObject json) {
        return typeTransformerRegistry.transform(json, Asset.class).getContent();
    }

    private String buildJsonLd(Asset asset) {
        return JsonUtils.toJson(typeTransformerRegistry.transform(asset, JsonObject.class).getContent());
    }

    @SneakyThrows
    public UiAsset buildUiAssetFromAssetJsonLd(String assetJsonLd) {
        return buildUiAssetFromAssetHelper(buildHelperDto(assetJsonLd));
    }

    public UiAsset buildUiAssetFromAssetHelper(AssetHelperDto assetHelperDto) {

        return UiAsset.builder()
                .name(assetHelperDto.getNs())
                .keywords(assetHelperDto.getKeywords())
                .version(assetHelperDto.getVersion())
                .licenseUrl(assetHelperDto.getLicense())
                .creator(assetHelperDto.getCreator() != null ? assetHelperDto.getCreator().getName() : null)
                .publisher(assetHelperDto.getPublisher() != null ? assetHelperDto.getPublisher().getName() : null)
                .description(assetHelperDto.getDescription())
                .language(assetHelperDto.getLanguage())
                .title(assetHelperDto.getTitle())
                .httpDatasourceHintsProxyMethod(assetHelperDto.getHttpDatasourceHintsProxyMethod())
                .httpDatasourceHintsProxyPath(assetHelperDto.getHttpDatasourceHintsProxyPath())
                .httpDatasourceHintsProxyQueryParams(assetHelperDto.getHttpDatasourceHintsProxyQueryParams())
                .httpDatasourceHintsProxyBody(assetHelperDto.getHttpDatasourceHintsProxyBody())
                .dataCategory(assetHelperDto.getDataCategory())
                .dataSubcategory(assetHelperDto.getDataSubcategory())
                .dataModel(assetHelperDto.getDataModel())
                .geoReferenceMethod(assetHelperDto.getGeoReferenceMethod())
                .transportMode(assetHelperDto.getTransportMode())
                .landingPageUrl(assetHelperDto.getLandingPage())
                .distribution(assetHelperDto.getDistribution() != null ? assetHelperDto.getDistribution().getName() : null)
                .build();
    }

    @SneakyThrows
    public AssetHelperDto buildHelperDto(String assetJsonLd) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(assetJsonLd, AssetHelperDto.class);
    }

    @SneakyThrows
    public Asset buildAssetFromUiAssetCreateRequest(UiAssetCreateRequest uiAssetCreateRequest) {

        Asset.Builder assetBuilder = Asset.Builder
                .newInstance()
                .id(uiAssetCreateRequest.getId())
                .name(uiAssetCreateRequest.getName())
                .description(uiAssetCreateRequest.getDescription())
                .version(uiAssetCreateRequest.getVersion())
                .dataAddress(edcPropertyMapperUtils.buildDataAddress(uiAssetCreateRequest.getDataAddressProperties()));

        Map<String, Object> additionalProps = new HashMap<>();
        additionalProps.put("title", uiAssetCreateRequest.getTitle());
        additionalProps.put("language", uiAssetCreateRequest.getLanguage());
        additionalProps.put("creator", uiAssetCreateRequest.getCreator());
        additionalProps.put("publisher", uiAssetCreateRequest.getPublisher());
        additionalProps.put("licenseUrl", uiAssetCreateRequest.getLicenseUrl());
        additionalProps.put("keywords", uiAssetCreateRequest.getKeywords());
        additionalProps.put("distribution", uiAssetCreateRequest.getDistribution());
        additionalProps.put("landingPageUrl", uiAssetCreateRequest.getLandingPageUrl());
        additionalProps.put("dataCategory", uiAssetCreateRequest.getDataCategory());
        additionalProps.put("dataSubcategory", uiAssetCreateRequest.getDataSubcategory());
        additionalProps.put("dataModel", uiAssetCreateRequest.getDataModel());
        additionalProps.put("geoReferenceMethod", uiAssetCreateRequest.getGeoReferenceMethod());
        additionalProps.put("transportMode", uiAssetCreateRequest.getTransportMode());

        if(uiAssetCreateRequest.getAdditionalProperties() != null) {
            additionalProps.putAll(uiAssetCreateRequest.getAdditionalProperties());
        }
        if(uiAssetCreateRequest.getPrivateProperties() != null) {
            assetBuilder.privateProperties(new HashMap<>(uiAssetCreateRequest.getPrivateProperties()));
        }
        if(uiAssetCreateRequest.getAdditionalJsonProperties() != null) {
            additionalProps.putAll(uiAssetCreateRequest.getAdditionalJsonProperties());
        }

        assetBuilder.properties(additionalProps);

        return assetBuilder.build();
    }
    private JsonObject buildJsonLdFromProperties(JsonObject json) {
        // Try to use the EDC Prop ID, but if it's not available, fall back to the "@id" property
        var assetId = Optional.ofNullable(JsonLdUtils.string(json, Prop.Edc.ID))
                .orElseGet(() -> JsonLdUtils.string(json, Prop.ID));

        return Json.createObjectBuilder()
                .add(Prop.ID, assetId)
                .add(Prop.TYPE, Prop.Edc.TYPE_ASSET)
                .add(Prop.Edc.PROPERTIES, json)
                .build();
    }
}
