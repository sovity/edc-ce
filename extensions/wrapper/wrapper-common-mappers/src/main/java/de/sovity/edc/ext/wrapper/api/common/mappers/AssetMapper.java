package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;

@RequiredArgsConstructor
public class AssetMapper {
    private final TypeTransformerRegistry typeTransformerRegistry;

    public UiAsset buildUiAsset(Asset asset) {
        throw new IllegalStateException("Not yet implemented!");
    }

    public Asset buildAssetFromAssetPropertiesJsonLd(JsonObject json) {
        var assetJson = buildAssetJsonLd(json);
        return typeTransformerRegistry.transform(assetJson, Asset.class).getContent();
    }

    private JsonObject buildAssetJsonLd(JsonObject properties) {
        var id = JsonLdUtils.string(properties, Prop.Edc.ID);

        return Json.createObjectBuilder()
                .add(Prop.ID, id)
                .add("properties", properties)
                .build();
    }
}
