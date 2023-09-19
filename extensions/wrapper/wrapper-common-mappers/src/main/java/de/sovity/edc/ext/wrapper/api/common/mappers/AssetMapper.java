package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.mappers.utils.UiAssetBuilder;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;

import java.util.Map;
import java.util.Optional;


@RequiredArgsConstructor
public class AssetMapper {
    private final TypeTransformerRegistry typeTransformerRegistry;
    private final UiAssetBuilder uiAssetBuilder;

    public UiAsset buildUiAsset(Asset asset) {
        var jsonLd = buildAssetJsonLd(asset);
        return buildUiAsset(jsonLd);
    }

    public UiAsset buildUiAsset(JsonObject assetJsonLd) {
        return uiAssetBuilder.buildUiAsset(assetJsonLd);
    }

    public Asset buildAsset(UiAssetCreateRequest createRequest) {
        var assetJsonLd = uiAssetBuilder.buildAssetJsonLd(createRequest);
        return buildAsset(assetJsonLd, createRequest.getCreatorOrganizationName(), createRequest.getPublisherHomepage());
    }

    public Asset buildAssetFromDatasetProperties(JsonObject json) {
        return typeTransformerRegistry.transform(buildJsonLdFromDatasetProperties(json), Asset.class).getContent();
    }

    private JsonObject buildJsonLdFromDatasetProperties(JsonObject json) {
        // Try to use the EDC Prop ID, but if it's not available, fall back to the "@id" property
        var assetId = Optional.ofNullable(JsonLdUtils.string(json, Prop.Edc.ID))
                .orElseGet(() -> JsonLdUtils.string(json, Prop.ID));

        return Json.createObjectBuilder()
                .add(Prop.ID, assetId)
                .add(Prop.Edc.PROPERTIES, json)
                .build();
    }

    private JsonObject buildAssetJsonLd(Asset asset) {
        return typeTransformerRegistry.transform(asset, JsonObject.class).getContent();
    }

    /**
     * Builds an Asset from the provided assetJsonLd with additional properties for creator and publisher.
     *
     * @param assetJsonLd   the base JsonObject to build the Asset from
     * @param creator       the creator to add as a property to the Asset (can be null)
     * @param publisher     the publisher to add as a property to the Asset (can be null)
     * @return              the built Asset with the added properties
     */
    private Asset buildAsset(JsonObject assetJsonLd, String creator, String publisher) {
        var asset = typeTransformerRegistry.transform(assetJsonLd, Asset.class).getContent();
        var assetBuilder = asset.toBuilder();

        if (creator != null) {
            assetBuilder.property(Prop.Dcterms.CREATOR, Map.of(Prop.TYPE, Prop.Foaf.ORGANIZATION, Prop.Foaf.NAME, creator));
        }
        if (publisher != null) {
            assetBuilder.property(Prop.Dcterms.PUBLISHER, Map.of(Prop.TYPE, Prop.Foaf.ORGANIZATION, Prop.Foaf.HOMEPAGE, publisher));
        }
        return assetBuilder.build();
    }
}
