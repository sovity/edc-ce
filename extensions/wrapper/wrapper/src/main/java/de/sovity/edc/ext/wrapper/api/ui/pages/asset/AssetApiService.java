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

import de.sovity.edc.ext.wrapper.api.ServiceException;
import de.sovity.edc.ext.wrapper.api.common.mappers.AssetMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.IdResponseDto;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class AssetApiService {
    private final AssetService assetService;
    private final AssetMapper assetMapper;
    private final AssetIdValidator assetIdValidator;

    public List<UiAsset> getAssets() {
        var assets = getAllAssets();
        return assets.stream().sorted(Comparator.comparing(Asset::getCreatedAt).reversed())
                .map(assetMapper::buildUiAsset).toList();
    }

    @NotNull
    public IdResponseDto createAsset(UiAssetCreateRequest request) {
        assetIdValidator.assertValid(request.getId());
        var asset = assetMapper.buildAsset(request);
        asset = assetService.create(asset).orElseThrow(ServiceException::new);
        return new IdResponseDto(asset.getId());
    }

    @NotNull
    public IdResponseDto deleteAsset(String assetId) {
        var response = assetService.delete(assetId).orElseThrow(ServiceException::new);
        return new IdResponseDto(response.getId());
    }

    private List<Asset> getAllAssets() {
        return assetService.query(QuerySpec.max()).orElseThrow(ServiceException::new).toList();
    }
}
