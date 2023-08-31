/*
 *  Copyright (c) 2022 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.wrapper.api.ui.pages.asset;

import de.sovity.edc.ext.wrapper.api.ui.model.AssetCreateRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.AssetEntry;
import de.sovity.edc.ext.wrapper.api.ui.model.IdResponseDto;
import de.sovity.edc.ext.wrapper.api.ui.pages.asset.services.AssetBuilder;
import de.sovity.edc.ext.wrapper.utils.EdcPropertyUtils;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class AssetApiService {
    private final AssetBuilder assetBuilder;
    private final AssetService assetService;
    private final EdcPropertyUtils edcPropertyUtils;

    public List<AssetEntry> getAssets() {
        var assets = getAllAssets();
        return assets.stream().sorted(Comparator.comparing(Asset::getCreatedAt).reversed()).map(asset -> {
            var entry = new AssetEntry();
            entry.setProperties(edcPropertyUtils.truncateToMapOfString(asset.getProperties()));
            entry.setPrivateProperties(edcPropertyUtils.truncateToMapOfString(asset.getPrivateProperties()));
            return entry;
        }).toList();
    }

    @NotNull
    public IdResponseDto createAsset(AssetCreateRequest request) {
        var asset = assetBuilder.buildAsset(request);
        asset = assetService.create(asset).getContent();
        return new IdResponseDto(asset.getId());
    }

    @NotNull
    public IdResponseDto deleteAsset(String assetId) {
        var response = assetService.delete(assetId);
        return new IdResponseDto(response.getContent().getId());
    }

    private List<Asset> getAllAssets() {
        return assetService.query(QuerySpec.max()).getContent().toList();
    }
}
