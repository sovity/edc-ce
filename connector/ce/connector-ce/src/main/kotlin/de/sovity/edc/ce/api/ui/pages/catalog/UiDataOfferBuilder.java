/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.catalog;

import de.sovity.edc.ce.api.common.model.UiAsset;
import de.sovity.edc.ce.api.common.model.UiPolicy;
import de.sovity.edc.ce.api.ui.model.UiContractOffer;
import de.sovity.edc.ce.api.ui.model.UiDataOffer;
import de.sovity.edc.ce.libs.mappers.AssetMapper;
import de.sovity.edc.ce.libs.mappers.PolicyMapper;
import de.sovity.edc.ce.libs.mappers.dsp.model.DspCatalog;
import de.sovity.edc.ce.libs.mappers.dsp.model.DspContractOffer;
import de.sovity.edc.ce.libs.mappers.dsp.model.DspDataOffer;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Service
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
