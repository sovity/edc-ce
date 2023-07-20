package de.sovity.edc.ext.wrapper.api.ui.pages.assets;

import de.sovity.edc.ext.wrapper.api.common.model.AssetDto;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static de.sovity.edc.ext.wrapper.utils.EdcDateUtils.utcMillisToOffsetDateTime;
import static de.sovity.edc.ext.wrapper.utils.MapUtils.mapValues;

@RequiredArgsConstructor
public class AssetPageApiService {

    private final AssetService assetService;

    public List<AssetDto> getAssetsForAssetPage() {
        var assets = getAssets();
        return assets.stream().map(this::buildAssetDtoFromAsset).toList();
    }

    @NotNull
    private AssetDto buildAssetDtoFromAsset(Asset asset) {
        var createdAt = utcMillisToOffsetDateTime(asset.getCreatedAt());
        var properties = mapValues(asset.getProperties(), Object::toString);
        return new AssetDto(asset.getId(), createdAt, properties);
    }

    @NotNull
    private List<Asset> getAssets() {
        return assetService.query(QuerySpec.max()).getContent().toList();
    }
}
