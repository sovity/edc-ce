package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;

import java.util.Optional;

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
        var assetJsonLd = buildJsonLdFromProperties(json);
        return typeTransformerRegistry.transform(assetJsonLd, Asset.class).getContent();
    }

    private String buildJsonLd(Asset asset) {
        return JsonUtils.toJson(typeTransformerRegistry.transform(asset, JsonObject.class).getContent());
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
