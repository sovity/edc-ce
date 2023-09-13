package de.sovity.edc.ext.wrapper.api.common.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.AssetHelperDto;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.spi.types.domain.asset.Asset;

import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
public class AssetMapper {
    /**
     * This Object Mapper must be able to handle JSON-LD serialization / deserialization.
     */
    private final ObjectMapper jsonLdObjectMapper;

    @SneakyThrows
    public UiAsset buildUiAssetFromAssetJsonLd(String assetJsonLd) {
        return buildUiAsset(buildHelperDto(assetJsonLd));
    }

    public UiAsset buildUiAsset(AssetHelperDto assetHelperDto) {

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
    private UiAsset buildUiAsset(Asset asset) {
        var assetJsonLd = jsonLdObjectMapper.writeValueAsString(asset);
        var uiAssetHelperDto = buildHelperDto(assetJsonLd);
        return buildUiAsset(uiAssetHelperDto);
    }

    @SneakyThrows
    private Asset buildAssetFromAssetJsonLd(String assetPropertiesJsonLd) {

        UiAsset uiAsset = buildUiAssetFromAssetJsonLd(assetPropertiesJsonLd);

        Asset.Builder assetBuilder = Asset.Builder
                .newInstance()
                .id(uiAsset.getId())
                .name(uiAsset.getName())
                .description(uiAsset.getDescription())
                .version(uiAsset.getVersion());

        Map<String, Object> additionalProps = new HashMap<>();
        additionalProps.put("title", uiAsset.getTitle());
        additionalProps.put("language", uiAsset.getLanguage());
        additionalProps.put("creator", uiAsset.getCreator());
        additionalProps.put("publisher", uiAsset.getPublisher());
        additionalProps.put("licenseUrl", uiAsset.getLicenseUrl());
        additionalProps.put("keywords", uiAsset.getKeywords());
        additionalProps.put("distribution", uiAsset.getDistribution());
        additionalProps.put("landingPageUrl", uiAsset.getLandingPageUrl());
        additionalProps.put("httpDatasourceHintsProxyMethod", uiAsset.getHttpDatasourceHintsProxyMethod());
        additionalProps.put("httpDatasourceHintsProxyPath", uiAsset.getHttpDatasourceHintsProxyPath());
        additionalProps.put("httpDatasourceHintsProxyQueryParams", uiAsset.getHttpDatasourceHintsProxyQueryParams());
        additionalProps.put("httpDatasourceHintsProxyBody", uiAsset.getHttpDatasourceHintsProxyBody());
        additionalProps.put("dataCategory", uiAsset.getDataCategory());
        additionalProps.put("dataSubcategory", uiAsset.getDataSubcategory());
        additionalProps.put("dataModel", uiAsset.getDataModel());
        additionalProps.put("geoReferenceMethod", uiAsset.getGeoReferenceMethod());
        additionalProps.put("transportMode", uiAsset.getTransportMode());

        if(uiAsset.getAdditionalProperties() != null) {
            additionalProps.putAll(uiAsset.getAdditionalProperties());
        }

        if(uiAsset.getPrivateProperties() != null) {
            assetBuilder.privateProperties(new HashMap<>(uiAsset.getPrivateProperties()));
        }

        if(uiAsset.getAdditionalJsonProperties() != null) {
            additionalProps.putAll(uiAsset.getAdditionalJsonProperties());
        }

        assetBuilder.properties(additionalProps);

        return assetBuilder.build();
    }
}
