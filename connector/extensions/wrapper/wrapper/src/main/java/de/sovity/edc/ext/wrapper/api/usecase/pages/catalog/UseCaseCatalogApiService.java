/*
 *  Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.usecase.pages.catalog;

import de.sovity.edc.ext.wrapper.api.ui.model.UiDataOffer;
import de.sovity.edc.ext.wrapper.api.ui.pages.catalog.UiDataOfferBuilder;
import de.sovity.edc.ext.wrapper.api.usecase.model.CatalogQuery;
import de.sovity.edc.utils.catalog.DspCatalogService;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.query.QuerySpec;

import java.util.List;

@RequiredArgsConstructor
public class UseCaseCatalogApiService {
    private final UiDataOfferBuilder uiDataOfferBuilder;
    private final DspCatalogService dspCatalogService;
    private final FilterExpressionMapper filterExpressionMapper;

    public List<UiDataOffer> fetchDataOffers(CatalogQuery catalogQuery) {
        var querySpec = buildQuerySpec(catalogQuery);

        var dspCatalog = dspCatalogService.fetchDataOffersWithFilters(catalogQuery.getConnectorEndpoint(), querySpec);
        return uiDataOfferBuilder.buildUiDataOffers(dspCatalog);
    }

    private QuerySpec buildQuerySpec(CatalogQuery params) {
        var builder = QuerySpec.Builder.newInstance()
                .limit(withDefault(params.getLimit(), Integer.MAX_VALUE))
                .offset(withDefault(params.getOffset(), 0));

        var filterExpressions = params.getFilterExpressions();
        if (filterExpressions != null && !filterExpressions.isEmpty()) {
            builder.filter(filterExpressionMapper.buildCriteria(filterExpressions));
        }

        return builder.build();
    }

    private <T> T withDefault(T valueOrNull, T orElse) {
        return valueOrNull == null ? orElse : valueOrNull;
    }
}
