package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.mappers.utils.FailedMappingException;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.UiAssetMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;

import java.util.Optional;


@RequiredArgsConstructor
public class AssetMapper {
    private final TypeTransformerRegistry typeTransformerRegistry;
    private final UiAssetMapper uiAssetMapper;
    private final JsonLd jsonLd;

    public UiAsset buildUiAsset(Asset asset, String connectorEndpoint, String participantId) {
        var assetJsonLd = buildAssetJsonLd(asset);
        return buildUiAsset(assetJsonLd, connectorEndpoint, participantId);
    }

    public UiAsset buildUiAsset(JsonObject assetJsonLd, String connectorEndpoint, String participantId) {
        return uiAssetMapper.buildUiAsset(assetJsonLd, connectorEndpoint, participantId);
    }

    public Asset buildAsset(UiAssetCreateRequest createRequest, String organizationName) {
        var assetJsonLd = uiAssetMapper.buildAssetJsonLd(createRequest, organizationName);
        return buildAsset(assetJsonLd);
    }

    public Asset buildAssetFromDatasetProperties(JsonObject json) {
        return buildAsset(buildAssetJsonLdFromDatasetProperties(json));
    }

    public Asset buildAsset(JsonObject assetJsonLd) {
        var expanded = jsonLd.expand(assetJsonLd)
                .orElseThrow(FailedMappingException::ofFailure);
        return typeTransformerRegistry.transform(expanded, Asset.class)
                .orElseThrow(FailedMappingException::ofFailure);
    }

    private JsonObject buildAssetJsonLd(Asset asset) {
        var assetJsonLd = typeTransformerRegistry.transform(asset, JsonObject.class)
                .orElseThrow(FailedMappingException::ofFailure);
        return jsonLd.expand(assetJsonLd)
                .orElseThrow(FailedMappingException::ofFailure);
    }

    public JsonObject buildAssetJsonLdFromDatasetProperties(JsonObject json) {
        // Try to use the EDC Prop ID, but if it's not available, fall back to the "@id" property
        var assetId = Optional.ofNullable(JsonLdUtils.string(json, Prop.Edc.ID))
                .orElseGet(() -> JsonLdUtils.string(json, Prop.ID));

        return Json.createObjectBuilder()
                .add(Prop.ID, assetId)
                .add(Prop.Edc.PROPERTIES, json)
                .build();
    }
}
