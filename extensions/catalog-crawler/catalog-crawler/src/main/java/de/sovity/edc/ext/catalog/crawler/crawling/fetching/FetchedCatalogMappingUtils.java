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

package de.sovity.edc.ext.catalog.crawler.crawling.fetching;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.ext.wrapper.api.common.mappers.AssetMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.utils.catalog.model.DspDataOffer;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class FetchedCatalogMappingUtils {
    private final PolicyMapper policyMapper;
    private final AssetMapper assetMapper;
    private final ObjectMapper objectMapper;

    public UiAsset buildUiAsset(
            DspDataOffer dspDataOffer,
            ConnectorRef connectorRef
    ) {
        var assetJsonLd = assetMapper.buildAssetJsonLdFromDatasetProperties(dspDataOffer.getAssetPropertiesJsonLd());
        var asset = assetMapper.buildAsset(assetJsonLd);
        var uiAsset = assetMapper.buildUiAsset(asset, connectorRef.getEndpoint(), connectorRef.getConnectorId());
        uiAsset.setCreatorOrganizationName(connectorRef.getOrganizationLegalName());
        uiAsset.setParticipantId(connectorRef.getConnectorId());
        return uiAsset;
    }

    @SneakyThrows
    public String buildUiAssetJson(UiAsset uiAsset) {
        return objectMapper.writeValueAsString(uiAsset);
    }

    @SneakyThrows
    public String buildUiPolicyJson(JsonObject policyJsonLd) {
        var policy = policyMapper.buildPolicy(policyJsonLd);
        var uiPolicy = policyMapper.buildUiPolicy(policy);
        return objectMapper.writeValueAsString(uiPolicy);
    }
}
