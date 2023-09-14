package de.sovity.edc.ext.wrapper.api.common.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.AssetHelperDto;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.EdcPropertyMapperUtils;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.spi.types.domain.asset.Asset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
public class AssetMapper {
    /**
     * This Object Mapper must be able to handle JSON-LD serialization / deserialization.
     */
    private final ObjectMapper jsonLdObjectMapper;
    private final EdcPropertyMapperUtils edcPropertyMapperUtils;

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
    public UiAsset buildUiAssetFromAsset(Asset asset) {
        var uiAsset = buildUiAssetFromAssetHelper(buildHelperDto(jsonLdObjectMapper.writeValueAsString(asset)));

        uiAsset.setId(asset.getId());
        uiAsset.setPrivateProperties(asset.getPrivateProperties());
        uiAsset.setDataAddressProperties(edcPropertyMapperUtils.truncateToMapOfString(asset.getDataAddress().getProperties()));
        uiAsset.setKeywords(uiAsset.getKeywords() == null ? List.of() : uiAsset.getKeywords());

        return uiAsset;
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
}
