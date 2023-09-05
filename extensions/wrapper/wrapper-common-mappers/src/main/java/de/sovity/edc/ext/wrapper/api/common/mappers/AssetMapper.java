package de.sovity.edc.ext.wrapper.api.common.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.MappingErrors;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetDto;
import de.sovity.edc.ext.wrapper.utils.EdcPropertyUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.spi.types.domain.asset.Asset;

@RequiredArgsConstructor
public class AssetMapper {
    /**
     * This Object Mapper must be able to handle JSON-LD serialization / deserialization.
     */
    private final EdcPropertyUtils edcPropertyUtils;
    private final ObjectMapper jsonLdObjectMapper;


    /**
     * Builds a simplified UI Asset Model from a JSON-LD Asset.
     * <p>
     * This operation is lossy.
     *
     * @param asset Asset in JSON-LD format
     * @return ui asset
     */
    @SneakyThrows
    public UiAssetDto buildAssetDto(Asset asset) {
        MappingErrors errors = MappingErrors.root();
        return UiAssetDto.builder()
                .assetJsonLd(jsonLdObjectMapper.writeValueAsString(asset))
                .build();
    }

    /**
     * Builds a JSON-LD Asset from our simplified UI Asset Model.
     * <p>
     * This operation is lossless.
     *
     * @param assetCreateDto UI asset create request
     * @return Asset in JSON-LD format
     */
    @SneakyThrows
    public Asset buildAsset(UiAssetCreateRequest assetCreateDto) {


        var assetProperties = edcPropertyUtils.toMapOfObject(assetCreateDto.getProperties());
        var privateProperties = edcPropertyUtils.toMapOfObject(assetCreateDto.getPrivateProperties());

        return Asset.Builder.newInstance()
                .id(assetCreateDto.getProperties().get(Asset.PROPERTY_ID))
                .properties(assetProperties)
                .privateProperties(privateProperties)
                .dataAddress(edcPropertyUtils.buildDataAddress(assetCreateDto.getDataAddressProperties()))
                .build();
    }
}
