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

import de.sovity.edc.ext.wrapper.api.ui.model.AssetRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.IdResponseDto;
import de.sovity.edc.ext.wrapper.api.ui.pages.asset.services.AssetBuilder;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.api.model.IdResponse;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class AssetApiService {

    private final AssetBuilder assetBuilder;
    private final AssetService assetService;

    @NotNull
    public IdResponse createAsset(AssetRequest request) {
        var asset = assetBuilder.buildAsset(request);

        asset = assetService.create(asset).getContent();
        return IdResponse.Builder.newInstance().id(asset.getId()).createdAt(asset.getCreatedAt()).build();
    }

    @NotNull
    public IdResponseDto deleteAsset(String assetId) {
        var response = assetService.delete(assetId);
        return new IdResponseDto(response.getContent().getId());
    }


}
