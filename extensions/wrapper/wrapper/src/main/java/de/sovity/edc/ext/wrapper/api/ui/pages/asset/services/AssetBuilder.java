/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.ui.pages.asset.services;


import de.sovity.edc.ext.wrapper.api.ui.model.AssetCreateRequest;
import de.sovity.edc.ext.wrapper.api.ui.pages.asset.services.utils.AssetPropertyMapper;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.asset.Asset;

@RequiredArgsConstructor
public class AssetBuilder {
    private final AssetPropertyMapper assetPropertyMapper;

    public Asset buildAsset(AssetCreateRequest request) {
        var properties = assetPropertyMapper.toMapOfObject(request.getProperties());
        var privateProperties = assetPropertyMapper.toMapOfObject(request.getPrivateProperties());
        var dataAddress = DataAddress.Builder.newInstance().properties(request.getDataAddressProperties()).build();

        return Asset.Builder.newInstance()
                .id(request.getProperties().get(Asset.PROPERTY_ID))
                .properties(properties)
                .privateProperties(privateProperties)
                .dataAddress(dataAddress)
                .build();
    }
}
