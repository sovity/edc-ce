package de.sovity.edc.ext.wrapper.api.common.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.UiAssetBuilder;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
public class AssetMapper {
    /**
     * This Object Mapper must be able to handle JSON-LD serialization / deserialization.
     */
    private final ObjectMapper jsonLdObjectMapper;
    private final TypeTransformerRegistry typeTransformerRegistry;
    private final UiAssetBuilder uiAssetBuilder;


    @SneakyThrows
    public UiAsset buildUiAssetFromAsset(Asset asset) {
        var uiAsset = uiAssetBuilder.buildUiAssetFromAssetHelper(uiAssetBuilder.buildHelperDto(jsonLdObjectMapper.writeValueAsString(asset)));

        uiAsset.setId(asset.getId());
        uiAsset.setPrivateProperties(asset.getPrivateProperties());
        uiAsset.setKeywords(uiAsset.getKeywords() == null ? List.of() : uiAsset.getKeywords());

        return uiAsset;
    }

    public Asset buildAssetFromAssetPropertiesJsonLd(JsonObject json) {
        return typeTransformerRegistry.transform(json, Asset.class).getContent();
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
