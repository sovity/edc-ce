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

import de.sovity.edc.ext.wrapper.api.ui.model.UiDataOffer;
import de.sovity.edc.utils.catalog.DspCatalogService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CatalogApiService {
    private final UiDataOfferBuilder uiDataOfferBuilder;
    private final DspCatalogService dspCatalogService;

    public List<UiDataOffer> fetchDataOffers(String connectorEndpoint) {
        var dspCatalog = dspCatalogService.fetchDataOffers(connectorEndpoint);
        return uiDataOfferBuilder.buildUiDataOffers(dspCatalog);
    }
}
