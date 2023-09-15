package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.spi.types.domain.asset.Asset;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class UiAssetBuilder {

    private final EdcPropertyMapperUtils edcPropertyMapperUtils;

    @SneakyThrows
    public UiAsset buildUiAssetFromAssetJsonLd(String assetJsonLd) {
        return buildUiAssetFromAssetHelper(buildHelperDto(assetJsonLd));
    }

    public UiAsset buildUiAssetFromAssetHelper(AssetHelperDto assetHelperDto) {

        return UiAsset.builder()
                .keywords(assetHelperDto.getKeywords())
                .version(assetHelperDto.getVersion())
                .licenseUrl(assetHelperDto.getLicense())
                .creatorOrganizationName(assetHelperDto.getCreator() != null ? assetHelperDto.getCreator().getName() : null)
                .publisherHomepage(assetHelperDto.getPublisher() != null ? assetHelperDto.getPublisher().getName() : null)
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
                .name(uiAssetCreateRequest.getTitle())
                .description(uiAssetCreateRequest.getDescription())
                .version(uiAssetCreateRequest.getVersion())
                .dataAddress(edcPropertyMapperUtils.buildDataAddress(uiAssetCreateRequest.getDataAddressProperties()));

        Map<String, Object> additionalProps = new HashMap<>();
        additionalProps.put("TITLE", uiAssetCreateRequest.getTitle());
        additionalProps.put("language", uiAssetCreateRequest.getLanguage());
        additionalProps.put("creatorOrganizationName", uiAssetCreateRequest.getCreatorOrganizationName());
        additionalProps.put("publisherHomepage", uiAssetCreateRequest.getPublisherHomepage());
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
