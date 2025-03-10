/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.catalog;

import de.sovity.edc.ce.api.ui.model.UiDataOffer;
import de.sovity.edc.ce.libs.mappers.dsp.DspCatalogService;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CatalogApiService {
    private final UiDataOfferBuilder uiDataOfferBuilder;
    private final DspCatalogService dspCatalogService;

    public List<UiDataOffer> fetchDataOffers(String participantId, String connectorEndpoint) {
        var dspCatalog = dspCatalogService.fetchDataOffers(participantId, connectorEndpoint);
        return uiDataOfferBuilder.buildUiDataOffers(dspCatalog);
    }
}
