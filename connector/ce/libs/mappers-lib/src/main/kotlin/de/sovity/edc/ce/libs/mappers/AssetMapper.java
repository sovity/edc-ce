/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.libs.mappers;

import de.sovity.edc.ce.api.common.model.UiAsset;
import de.sovity.edc.ce.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ce.api.common.model.UiAssetEditRequest;
import de.sovity.edc.ce.libs.mappers.asset.AssetJsonLdBuilder;
import de.sovity.edc.ce.libs.mappers.asset.AssetJsonLdParser;
import de.sovity.edc.ce.libs.mappers.asset.utils.FailedMappingException;
import de.sovity.edc.runtime.simple_di.Service;
import de.sovity.edc.utils.jsonld.JsonLdUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;

import java.util.Optional;

import static de.sovity.edc.ce.libs.mappers.dsp.TypeTransformerRegistryUtils.forManagementApiContext;

@RequiredArgsConstructor
@Service
public class AssetMapper {
    private final TypeTransformerRegistry typeTransformerRegistry;
    private final AssetJsonLdBuilder assetJsonLdBuilder;
    private final AssetJsonLdParser assetJsonLdParser;
    private final JsonLd jsonLd;

    public Asset editAsset(
        @NonNull Asset asset,
        @NonNull UiAssetEditRequest editRequest
    ) {
        var assetJsonLd = buildAssetJsonLd(asset);
        var editedJsonLd = assetJsonLdBuilder.editAssetJsonLd(assetJsonLd, editRequest);
        return buildAsset(editedJsonLd);
    }

    public UiAsset buildUiAsset(
        @NonNull Asset asset,
        @NonNull String connectorEndpoint,
        @NonNull String participantId
    ) {
        var assetJsonLd = buildAssetJsonLd(asset);
        return buildUiAsset(assetJsonLd, connectorEndpoint, participantId);
    }

    public UiAsset buildUiAsset(
        @NonNull JsonObject assetJsonLd,
        @NonNull String connectorEndpoint,
        @NonNull String participantId
    ) {
        return assetJsonLdParser.buildUiAsset(assetJsonLd, connectorEndpoint, participantId);
    }

    /**
     * Maps "DCAT Dataset JSON-LD" to "EDC Asset JSON-LD"
     *
     * @param datasetJsonLd "DCAT Dataset JSON-LD"
     * @return "EDC Asset JSON-LD"
     */
    public Asset buildAssetFromDatasetProperties(
        @NonNull JsonObject datasetJsonLd
    ) {
        var assetJsonLd = buildAssetJsonLdFromDatasetProperties(datasetJsonLd);
        return buildAsset(assetJsonLd);
    }

    public Asset buildAsset(
        @NonNull JsonObject assetJsonLd
    ) {
        var expanded = jsonLd.expand(assetJsonLd)
            .orElseThrow(FailedMappingException::ofFailure);

        return forManagementApiContext(typeTransformerRegistry).transform(expanded, Asset.class)
            .orElseThrow(FailedMappingException::ofFailure);
    }

    public JsonObject buildAssetJsonLd(
        @NonNull Asset asset
    ) {
        var assetJsonLd = forManagementApiContext(typeTransformerRegistry).transform(asset, JsonObject.class)
            .orElseThrow(FailedMappingException::ofFailure);
        return jsonLd.expand(assetJsonLd)
            .orElseThrow(FailedMappingException::ofFailure);
    }

    public JsonObject buildAssetJsonLdFromDatasetProperties(
        @NonNull JsonObject json
    ) {
        // Try to use the EDC Prop ID, but if it's not available, fall back to the "@id" property
        var assetId = Optional.ofNullable(JsonLdUtils.string(json, Prop.Edc.ID))
            .orElseGet(() -> JsonLdUtils.string(json, Prop.ID));

        return Json.createObjectBuilder()
            .add(Prop.ID, assetId)
            .add(Prop.Edc.PROPERTIES, json)
            .build();
    }
}
