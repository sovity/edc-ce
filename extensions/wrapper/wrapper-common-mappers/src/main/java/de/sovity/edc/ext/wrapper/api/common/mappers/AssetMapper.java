package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.types.domain.asset.Asset;

@RequiredArgsConstructor
public class AssetMapper {
    public UiAsset buildUiAsset(Asset asset) {
        throw new IllegalStateException("Not yet implemented!");
    }

    public Asset buildAssetFromAssetPropertiesJsonLd(String assetPropertiesJsonLd) {
        throw new IllegalStateException("Not yet implemented!");
    }
}
