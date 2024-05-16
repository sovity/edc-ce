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

package de.sovity.edc.ext.wrapper.api.ui.pages.catalog;

import de.sovity.edc.ext.wrapper.api.common.mappers.AssetMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicy;
import de.sovity.edc.ext.wrapper.api.ui.model.UiContractOffer;
import de.sovity.edc.ext.wrapper.api.ui.model.UiDataOffer;
import de.sovity.edc.utils.catalog.model.DspCatalog;
import de.sovity.edc.utils.catalog.model.DspContractOffer;
import de.sovity.edc.utils.catalog.model.DspDataOffer;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UiDataOfferBuilder {
    private final AssetMapper assetMapper;
    private final PolicyMapper policyMapper;

    public List<UiDataOffer> buildUiDataOffers(DspCatalog dspCatalog) {
        var endpoint = dspCatalog.getEndpoint();
        var participantId = dspCatalog.getParticipantId();

        return dspCatalog.getDataOffers().stream()
                .map(dataOffer -> buildDataOffer(dataOffer, endpoint, participantId))
                .toList();
    }

    private UiDataOffer buildDataOffer(DspDataOffer dataOffer, String endpoint, String participantId) {
        var uiDataOffer = new UiDataOffer();
        uiDataOffer.setEndpoint(endpoint);
        uiDataOffer.setParticipantId(participantId);
        uiDataOffer.setAsset(buildUiAsset(dataOffer, endpoint, participantId));
        uiDataOffer.setContractOffers(buildContractOffers(dataOffer.getContractOffers()));
        return uiDataOffer;
    }

    private List<UiContractOffer> buildContractOffers(List<DspContractOffer> contractOffers) {
        return contractOffers.stream().map(this::buildContractOffer).toList();
    }

    private UiContractOffer buildContractOffer(DspContractOffer contractOffer) {
        var uiContractOffer = new UiContractOffer();
        uiContractOffer.setContractOfferId(contractOffer.getContractOfferId());
        uiContractOffer.setPolicy(buildUiPolicy(contractOffer));
        return uiContractOffer;
    }

    private UiAsset buildUiAsset(DspDataOffer dataOffer, String endpoint, String participantId) {
        var asset = assetMapper.buildAssetFromDatasetProperties(dataOffer.getAssetPropertiesJsonLd());
        return assetMapper.buildUiAsset(asset, endpoint, participantId);
    }

    private UiPolicy buildUiPolicy(DspContractOffer contractOffer) {
        var policy = policyMapper.buildPolicy(contractOffer.getPolicyJsonLd());
        return policyMapper.buildUiPolicy(policy);
    }
}
