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

package de.sovity.edc.ext.brokerserver.services.api;

import de.sovity.edc.ext.wrapper.api.common.mappers.AssetMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicy;
import de.sovity.edc.utils.JsonUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DataOfferMappingUtils {
    private final PolicyMapper policyMapper;
    private final AssetMapper assetMapper;

    public UiAsset buildUiAsset(String assetJsonLd, String endpoint, String participantId) {
        var asset = assetMapper.buildAsset(JsonUtils.parseJsonObj(assetJsonLd));
        return assetMapper.buildUiAsset(asset, endpoint, participantId);
    }

    public UiPolicy buildUiPolicy(String policyJson) {
        var policy = policyMapper.buildPolicy(policyJson);
        return policyMapper.buildUiPolicy(policy);
    }
}
