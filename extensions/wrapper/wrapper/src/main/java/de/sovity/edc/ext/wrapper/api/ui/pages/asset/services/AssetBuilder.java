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


import de.sovity.edc.ext.wrapper.api.ui.model.AssetRequest;
import de.sovity.edc.ext.wrapper.api.ui.pages.asset.services.utils.AssetUtils;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.types.domain.asset.Asset;

import java.util.UUID;

@RequiredArgsConstructor
public class AssetBuilder {

    private final AssetUtils assetUtils;

    public Asset buildAsset(AssetRequest request) {
        var properties = request.getProperties();
        var privateProperties = request.getPrivateProperties();
        var dataAddress = request.getDataAddressDto();

        return Asset.Builder.newInstance().id(UUID.randomUUID().toString()).properties(properties).privateProperties(privateProperties).dataAddress(assetUtils.mapToDataAddress(dataAddress)).build();
    }
}
