package de.sovity.edc.ext.wrapper.api.common.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetHelperDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;


@RequiredArgsConstructor
public class AssetMapper {
    /**
     * This Object Mapper must be able to handle JSON-LD serialization / deserialization.
     */
    private final ObjectMapper jsonLdObjectMapper;


    public UiAsset buildUiAsset(UiAssetHelperDto uiAssetHelperDto) {

        return UiAsset.builder()
                .name(uiAssetHelperDto.getNs())
                .keywords(uiAssetHelperDto.getKeywords())
                .version(uiAssetHelperDto.getVersion())
                .license(uiAssetHelperDto.getLicense())
                .publisher(uiAssetHelperDto.getPublisher().getName())
                .creator(uiAssetHelperDto.getCreator().getName())
                .description(uiAssetHelperDto.getDescription())
                .language(uiAssetHelperDto.getLanguage())
                .title(uiAssetHelperDto.getTitle())
                .httpDatasourceHintsProxyMethod(uiAssetHelperDto.getHttpDatasourceHintsProxyMethod())
                .httpDatasourceHintsProxyPath(uiAssetHelperDto.getHttpDatasourceHintsProxyPath())
                .httpDatasourceHintsProxyQueryParams(uiAssetHelperDto.getHttpDatasourceHintsProxyQueryParams())
                .httpDatasourceHintsProxyBody(uiAssetHelperDto.getHttpDatasourceHintsProxyBody())
                .dataCategory(uiAssetHelperDto.getDataCategory())
                .dataSubcategory(uiAssetHelperDto.getDataSubcategory())
                .dataModel(uiAssetHelperDto.getDataModel())
                .geoReferenceMethod(uiAssetHelperDto.getGeoReferenceMethod())
                .transportMode(uiAssetHelperDto.getTransportMode())
                .landingPage(uiAssetHelperDto.getLandingPage())
                .distribution(uiAssetHelperDto.getDistribution().getName())
                .build();
    }

    @SneakyThrows
    public UiAssetHelperDto buildHelperDto(String assetJsonLd) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(assetJsonLd, UiAssetHelperDto.class);
    }
}
