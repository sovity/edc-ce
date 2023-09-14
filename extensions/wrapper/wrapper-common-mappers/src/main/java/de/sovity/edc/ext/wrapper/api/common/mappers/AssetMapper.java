package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.utils.JsonUtils;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;

@RequiredArgsConstructor
public class AssetMapper {
    private final TypeTransformerRegistry typeTransformerRegistry;

    public UiAsset buildUiAsset(Asset asset) {
        String json = buildJsonLd(asset);


        // TODO add more fields
        return new UiAsset(
                asset.getId(),
                asset.getName()
        );
    }

    public Asset buildAssetFromAssetPropertiesJsonLd(JsonObject json) {
        return typeTransformerRegistry.transform(json, Asset.class).getContent();
    }

    private String buildJsonLd(Asset asset) {
        return JsonUtils.toJson(typeTransformerRegistry.transform(asset, JsonObject.class).getContent());
    }
}
